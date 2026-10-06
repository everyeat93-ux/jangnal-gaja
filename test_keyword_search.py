import urllib.request
import json
import urllib.parse
import sys

sys.stdout.reconfigure(encoding='utf-8')
key_encoded = 'fXnq0Rp85fb8coBDMN8mUdYU%2Bh6zh0QAyYOHVO%2BBm1sF8STSTdsZRSOav4rCn%2BTP9%2ByQi61NYhCb0ARmaa%2BOvw%3D%3D'

for keyword in ['모란', '정선', '서문시장', '속초', '자갈치', '광장시장']:
    kw_encoded = urllib.parse.quote(keyword)
    # contenttypeid=15 is for Event/Performance/Festival (행사/공연/축제)
    url = f'https://apis.data.go.kr/B551011/KorService2/searchKeyword2?serviceKey={key_encoded}&numOfRows=5&pageNo=1&MobileOS=AND&MobileApp=JangnalGaja&_type=json&contentTypeId=15&keyword={kw_encoded}'
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=10) as response:
            data = json.loads(response.read().decode('utf-8'))
            items = data.get('response', {}).get('body', {}).get('items', {}).get('item', [])
            if isinstance(items, dict):
                items = [items]
            print(f"=== Keyword [{keyword}]: {len(items)} festival results ===")
            for it in items:
                print(f"  * {it.get('title')} | Addr: {it.get('addr1')} | ({it.get('eventstartdate')} ~ {it.get('eventenddate')})")
    except Exception as e:
        print(f"Error for {keyword}: {e}")
