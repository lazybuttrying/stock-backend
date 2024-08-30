INSERT_STOCK_QUERY = '''
INSERT INTO stock (ticker, name, market, industry, sector, created_at)
VALUES (%s, %s, %s, %s, %s, %s)
'''

INSERT_PRICE_QUERY = '''
INSERT INTO price (ticker, time, open, high, low, close, adj_close, volume, created_at)
VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
ON CONFLICT (ticker, time) DO NOTHING;
'''

INSERT_NEWS_QUERY = '''
INSERT INTO news (uuid, title, publisher, link, provider_publish_time, type, thumbnail_id)
VALUES (%s, %s, %s, %s, %s, %s, %s)
ON CONFLICT (uuid) DO NOTHING;
'''

INSERT_THUMBNAIL_QUERY = '''
INSERT INTO thumbnail DEFAULT VALUES
RETURNING id;
'''

INSERT_RESOLUTION_QUERY = '''
INSERT INTO thumbnail_resolutions (url, width, height, tag, thumbnail_id)
VALUES (%s, %s, %s, %s, %s);
'''

INSERT_STOCK_NEWS_RELATION = '''
INSERT INTO stock_news (stock_ticker, news_id)
VALUES (%s, %s);
'''
