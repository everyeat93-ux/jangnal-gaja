import urllib.request
import json
import urllib.parse
import sys

# Ensure UTF-8 output
sys.stdout.reconfigure(encoding='utf-8')

key_encoded = 'fXnq0Rp85fb8coBDMN8mUdYU%2Bh6zh0QAyYOHVO%2BBm1sF8STSTdsZRSOav4rCn%2BTP9%2ByQi61NYhCb0ARmaa%2BOvw%3D%3D'

# 1. Fetch upcoming festivals from TourAPI
url = f'https://apis.data.go.kr/B551011/KorService2/searchFestival2?serviceKey={key_encoded}&numOfRows=100&pageNo=1&MobileOS=AND&MobileApp=JangnalGaja&_type=json&eventStartDate=20261001'

req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req, timeout=15) as response:
        res_text = response.read().decode('utf-8')
        data = json.loads(res_text)
        items = data['response']['body']['items']['item']
        print(f"Total upcoming festivals fetched: {len(items)}")
        for idx, it in enumerate(items[:15]):
            title = it.get('title', '')
            addr = it.get('addr1', '')
            s_date = it.get('eventstartdate', '')
            e_date = it.get('eventenddate', '')
            print(f"[{idx+1}] {title} | {addr} | ({s_date} ~ {e_date})")
except Exception as e:
    print('Error:', e)
