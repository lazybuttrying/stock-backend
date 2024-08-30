import pydantic
from fastapi import FastAPI
from yahooFinance import fetchPrice, fetchNews, fetchInfo
from db.executor import insert_price, insert_news, insert_stock_news, insert_info
from datatype import PriceRequest, BaseRequest
from util import get_kst_time, check_success, raise_exception

app = FastAPI()


@app.get("/")
async def read_root():
    return {"Hello": "World"}


@app.post("/fetch/info")
async def insertInfo(request: BaseRequest):
    try:
        request.tickers = tuple(set(request.tickers))
        response = fetchInfo(request.tickers)
        check_success(response)
        insert_info(response.data)

    except Exception as e:
        raise_exception(e)

    return {"tickers": request.tickers}


@app.post("/fetch/price")
async def insertPrice(request: PriceRequest):
    try:
        request.tickers = tuple(set(request.tickers))
        response = fetchPrice(
            request.tickers, request.startDate, request.endDate)
        check_success(response)
        insert_price(response.data)

    except Exception as e:
        raise_exception(e)

    return {"tickers": request.tickers}


@app.post("/fetch/news")
async def insertNews(request: BaseRequest):
    try:
        request.tickers = tuple(set(request.tickers))
        response = fetchNews(request.tickers)
        check_success(response)
        uuids = set([news['uuid'] for news in response.data])
        failed = insert_news(
            tuple([news for news in response.data if news['uuid'] in uuids]))
        if failed:
            print(failed)
            await insertInfo(BaseRequest(tickers=tuple(failed.keys())))
            insert_stock_news(failed)

    except Exception as e:
        raise_exception(e)

    return {"tickers": request.tickers}
