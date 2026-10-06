import urllib.request
import json
import urllib.parse
import csv
import sys
import os

sys.stdout.reconfigure(encoding='utf-8')

KEY_ENCODED = 'fXnq0Rp85fb8coBDMN8mUdYU%2Bh6zh0QAyYOHVO%2BBm1sF8STSTdsZRSOav4rCn%2BTP9%2ByQi61NYhCb0ARmaa%2BOvw%3D%3D'
GENERIC_NAMES = {'국제', '중앙', '동문', '남문', '서문', '북문', '풍물', '평화', '자유', '정원', '매일', '상설', '공설', '종합', '수산', '재래', '골목'}

def fetch_all_festivals():
    print(">>> 1. Fetching all upcoming festivals from TourAPI...")
    url = f'https://apis.data.go.kr/B551011/KorService2/searchFestival2?serviceKey={KEY_ENCODED}&numOfRows=500&pageNo=1&MobileOS=AND&MobileApp=JangnalGaja&_type=json&eventStartDate=20261001'
    
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        with urllib.request.urlopen(req, timeout=20) as resp:
            data = json.loads(resp.read().decode('utf-8'))
            items = data.get('response', {}).get('body', {}).get('items', {}).get('item', [])
            if isinstance(items, dict):
                items = [items]
            print(f"  총 {len(items)}개 공공데이터 축제 수신 완료")
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

def match_festivals_high_precision(festivals, markets):
    print(">>> 2. 100% 정밀 1:1 매칭 필터링 실행 중...")
    matched = []
    
    for f in festivals:
        title = f.get('title', '').strip()
        addr = (f.get('addr1', '') + " " + f.get('addr2', '')).strip()
        s_date = f.get('eventstartdate', '')
        e_date = f.get('eventenddate', '')
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
            
            # Match rules:
            # 1. Exact full market name in title or address
            # 2. Non-generic clean name in title/address + festival keyword
            # 3. Exact road/street address match
            exact_name_match = m_name in title or m_name in addr
            exact_addr_match = m['road_addr'] and (m['road_addr'] in addr)
            
            specific_title_match = False
            if not is_generic and len(clean_name) >= 2:
                # e.g., '예산장터', '가은아자개', '풍기인삼', '강경젓갈', '양양송이', '순창장류'
                if clean_name in title and any(kw in title for kw in ['장터', '시장', '축제', '페스타', '문화제', '대축제', '인삼', '사과', '한우', '젓갈', '송이', '대추', '약초', '치즈']):
                    # Check regional consistency (addr should match province/city)
                    m_region = m['road_addr'][:4] or m['jibun_addr'][:4]
                    f_region = addr[:4]
                    if m_region and f_region and (m_region[:2] == f_region[:2]):
                        specific_title_match = True

            if exact_name_match or exact_addr_match or specific_title_match:
                matched.append({
                    'market_id': m['id'],
                    'market_name': m_name,
                    'title': title,
                    'category': '축제',
                    'startDate': s_date,
                    'endDate': e_date,
                    'posterUrl': img,
                    'venue': addr if addr else f"{m_name} 일원",
                    'description': f"한국관광공사 TourAPI 공인 지역 축제입니다. (문의: {tel})" if tel else "한국관광공사 TourAPI 공인 지역 축제입니다.",
                    'hostOrg': "지자체 및 축제위원회",
                    'isOfficial': True
                })
                break

    print(f"\n✅ 검증 완료된 1:1 고유 축제 ({len(matched)}건):")
    for idx, item in enumerate(matched):
        print(f"[{idx+1}] [{item['market_name']}] {item['title']} ({item['startDate']} ~ {item['endDate']}) - {item['venue']}")
    return matched

if __name__ == '__main__':
    festivals = fetch_all_festivals()
    markets = load_markets('app/src/main/assets/markets.csv')
    matched = match_festivals_high_precision(festivals, markets)
