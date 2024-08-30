from pandas import DataFrame
from util import get_kst_time, convert_timestamp
from .query import *
from .db import db
from psycopg2.errors import InternalError, IntegrityError


def insert_info(data: list[dict]) -> None:
    now = get_kst_time()
    with db.get_cursor() as cur:
        for stock in data:
            cur.execute(INSERT_STOCK_QUERY, (
                stock['ticker'],
                stock['name'],
                stock['industry'],
                stock['sector'],
                stock['market'],
                now
            ))
    db.commit_and_close()


def insert_stock_news(data: dict) -> None:
    with db.get_cursor() as cur:
        for ticker, news in data.items():
            for news_uuid in news:
                cur.execute(INSERT_STOCK_NEWS_RELATION, (ticker, news_uuid))
    db.commit_and_close()


def insert_news(data: tuple[dict]) -> None | dict:

    failed = {}
    with db.get_cursor() as cur:
        for news in data:
            if not news:
                continue

            cur.execute(INSERT_THUMBNAIL_QUERY)
            thumbnail_id = cur.fetchone()[0]
            if 'thumbnail' in news.keys():
                for res in news['thumbnail']['resolutions']:
                    cur.execute(INSERT_RESOLUTION_QUERY, (
                        res['url'],
                        res['width'],
                        res['height'],
                        res['tag'],
                        thumbnail_id
                    ))
            cur.execute(INSERT_NEWS_QUERY, (
                news['uuid'],
                news['title'],
                news['publisher'],
                news['link'],
                convert_timestamp(news['providerPublishTime']),
                news['type'],
                thumbnail_id
            ))
            for ticker in news['relatedTickers']:
                try:
                    print(news)
                    cur.execute(INSERT_STOCK_NEWS_RELATION,
                                (ticker, news['uuid']))
                except InternalError:
                    pass
                except IntegrityError:
                    cur.execute("rollback")
                    if ticker not in failed.keys():
                        failed[ticker] = []
                    failed[ticker].append(news['uuid'])
                    print(ticker)
                except Exception as e:
                    print(e, e.__class__)

    db.commit_and_close()
    return failed


def insert_price(data: DataFrame) -> None:
    now = get_kst_time()
    with db.get_cursor() as cur:
        for index, row in data.iterrows():
            index = index.strftime('%Y-%m-%d')
            row = {k: (float(v) if not isinstance(v, str) else v)
                   for k, v in row.items()}
            cur.execute(INSERT_PRICE_QUERY, (
                row['Ticker'],
                index,
                row['Open'],
                row['High'],
                row['Low'],
                row['Close'],
                row['Adj Close'],
                row['Volume'],
                now
            ))
    db.commit_and_close()
