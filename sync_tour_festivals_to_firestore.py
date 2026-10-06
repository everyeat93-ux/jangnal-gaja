import urllib.request
import json
import urllib.parse
import csv
import sys
import os
import time

sys.stdout.reconfigure(encoding='utf-8')

KEY_ENCODED = 'fXnq0Rp85fb8coBDMN8mUdYU%2Bh6zh0QAyYOHVO%2BBm1sF8STSTdsZRSOav4rCn%2BTP9%2ByQi61NYhCb0ARmaa%2BOvw%3D%3D'
FIREBASE_API_KEY = 'AIzaSyBfSOpffCzL9wylVcQuG4kH31FBVhBNWF0'
PROJECT_ID = 'jangnal-gaja'

GENERIC_NAMES = {'국제', '중앙', '동문', '남문', '서문', '북문', '풍물', '평화', '자유', '정원', '매일', '상설', '공설', '종합', '수산', '재래', '골목'}

def fetch_all_festivals():
    print(">>> 1. 한국관광공사 TourAPI 최신 축제/공연 데이터 수집 중...")
    url = f'https://apis.data.go.kr/B551011/KorService2/searchFestival2?serviceKey={KEY_ENCODED}&numOfRows=500&pageNo=1&MobileOS=AND&MobileApp=JangnalGaja&_type=json&eventStartDate=20261001'
    
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        with urllib.request.urlopen(req, timeout=20) as resp:
            data = json.loads(resp.read().decode('utf-8'))
            items = data.get('response', {}).get('body', {}).get('items', {}).get('item', [])
            if isinstance(items, dict):
                items = [items]
            print(f"  ✓ 총 {len(items)}개의 전국 공공 축제/공연 수신 완료")
            return items
    except Exception as e:
        print(f"  Error: {e}")
        return []

def load_markets(csv_path):
    markets = []
    with open(csv_path, 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        for idx, row in enumerate(reader):
            if not row or len(row) < 7:
                continue
            name = row[0].strip()
            road_addr = row[2].strip() if len(row) > 2 else ""
            jibun_addr = row[3].strip() if len(row) > 3 else ""
            try:
                lat = float(row[5]) if len(row) > 5 and row[5] else 0.0
                lon = float(row[6]) if len(row) > 6 and row[6] else 0.0
            except:
                lat, lon = 0.0, 0.0
            markets.append({
                'id': idx + 1,
                'name': name,
                'road_addr': road_addr,
                'jibun_addr': jibun_addr,
                'lat': lat,
                'lon': lon
            })
    return markets

def match_festivals(festivals, markets):
    print(">>> 2. 전국 1,400개 전통시장 1:1 정밀 매칭 검증 중...")
    matched = []
    
    for f in festivals:
        content_id = str(f.get('contentid', ''))
        title = f.get('title', '').strip()
        addr = (f.get('addr1', '') + " " + f.get('addr2', '')).strip()
        s_date = str(f.get('eventstartdate', ''))
        e_date = str(f.get('eventenddate', ''))
        img = f.get('firstimage', '') or f.get('firstimage2', '')
        tel = f.get('tel', '')
        
        if len(s_date) == 8:
            s_date = f"{s_date[:4]}.{s_date[4:6]}.{s_date[6:]}"
        if len(e_date) == 8:
            e_date = f"{e_date[:4]}.{e_date[4:6]}.{e_date[6:]}"

        for m in markets:
            m_name = m['name']
            clean_name = m_name.replace('전통시장', '').replace('시장', '').replace('민속', '').replace('5일장', '').replace('오일장', '').strip()
            
            is_generic = clean_name in GENERIC_NAMES or len(clean_name) < 2
            
            exact_name_match = m_name in title or m_name in addr
            exact_addr_match = m['road_addr'] and (m['road_addr'] in addr)
            
            specific_title_match = False
            if not is_generic and len(clean_name) >= 2:
                if clean_name in title and any(kw in title for kw in ['장터', '시장', '축제', '페스타', '문화제', '대축제', '인삼', '사과', '한우', '젓갈', '송이', '대추', '약초', '치즈']):
                    m_region = m['road_addr'][:4] or m['jibun_addr'][:4]
                    f_region = addr[:4]
                    if m_region and f_region and (m_region[:2] == f_region[:2]):
                        specific_title_match = True

            if exact_name_match or exact_addr_match or specific_title_match:
                doc_id = f"tourapi_{content_id}" if content_id else f"tourapi_{m['id']}_{int(time.time())}"
                matched.append({
                    'doc_id': doc_id,
                    'market_id': m['id'],
                    'market_name': m_name,
                    'title': title,
                    'category': '축제',
                    'startDate': s_date,
                    'endDate': e_date,
                    'posterUrl': img,
                    'venue': addr if addr else f"{m_name} 일원",
                    'description': f"한국관광공사 TourAPI 공인 축제·행사입니다. (문의: {tel})" if tel else "한국관광공사 TourAPI 공인 축제·행사입니다.",
                    'hostOrg': "지자체 및 축제위원회",
                    'isOfficial': True,
                    'createdAt': int(time.time() * 1000)
                })
                break

    print(f"  ✓ 1:1 매칭 성공: {len(matched)}건의 실제 시장 축제 확보!")
    return matched

def upload_to_firestore(matched_list):
    print(f">>> 3. Firebase Firestore (markets/{{id}}/festivals) 실시간 자동 적재 시작...")
    success_count = 0
    
    for item in matched_list:
        market_id = item['market_id']
        doc_id = item['doc_id']
        
        url = f'https://firestore.googleapis.com/v1/projects/{PROJECT_ID}/databases/(default)/documents/markets/{market_id}/festivals?documentId={doc_id}&key={FIREBASE_API_KEY}'
        
        fields = {
            "title": {"stringValue": item['title']},
            "category": {"stringValue": item['category']},
            "startDate": {"stringValue": item['startDate']},
            "endDate": {"stringValue": item['endDate']},
            "posterUrl": {"stringValue": item['posterUrl']},
            "venue": {"stringValue": item['venue']},
            "description": {"stringValue": item['description']},
            "hostOrg": {"stringValue": item['hostOrg']},
            "isOfficial": {"booleanValue": item['isOfficial']},
            "createdAt": {"integerValue": str(item['createdAt'])}
        }
        
        body = json.dumps({"fields": fields}).encode('utf-8')
        req = urllib.request.Request(url, data=body, headers={'Content-Type': 'application/json'}, method='POST')
        
        try:
            with urllib.request.urlopen(req, timeout=10) as resp:
                success_count += 1
                print(f"  ✓ [{success_count}/{len(matched_list)}] 등록 성공: [{item['market_name']}] {item['title']}")
        except urllib.error.HTTPError as e:
            # If document already exists (409 Conflict), update it via PATCH
            if e.code == 409:
                patch_url = f'https://firestore.googleapis.com/v1/projects/{PROJECT_ID}/databases/(default)/documents/markets/{market_id}/festivals/{doc_id}?key={FIREBASE_API_KEY}'
                patch_req = urllib.request.Request(patch_url, data=body, headers={'Content-Type': 'application/json'}, method='PATCH')
                try:
                    with urllib.request.urlopen(patch_req, timeout=10) as patch_resp:
                        success_count += 1
                        print(f"  ✓ [{success_count}/{len(matched_list)}] 갱신 성공: [{item['market_name']}] {item['title']}")
                except Exception as patch_err:
                    print(f"  ✗ Update error for {item['title']}: {patch_err}")
            else:
                print(f"  ✗ Upload error for {item['title']}: {e}")
        except Exception as e:
            print(f"  ✗ Upload error for {item['title']}: {e}")

    print(f"\n🎉 Firestore 실시간 동기화 완료! (총 {success_count}건 등록/갱신 성공)")

if __name__ == '__main__':
    festivals = fetch_all_festivals()
    markets = load_markets('app/src/main/assets/markets.csv')
    matched = match_festivals(festivals, markets)
    if matched:
        upload_to_firestore(matched)
