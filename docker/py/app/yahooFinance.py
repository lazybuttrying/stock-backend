import yfinance as yf
from datatype import ReturnYFinance, MARKET
from datetime import datetime
from pandas import DataFrame, concat


def fetchInfo(tickers: tuple[str]) -> ReturnYFinance:
    """
    Apple 주식 정보 데이터를 yfinance를 사용하여 수집합니다.

    :return: DataFrame containing the stock info data
    """
    data = []
    for ticker in tickers:
        symbol = ticker
        if ticker.isdigit() and len(ticker) == 6:
            symbol = f"{ticker}.KS"
            stock_info = yf.Ticker(symbol).info
            if len(stock_info.keys()) < 2:
                symbol = f"{ticker}.KQ"

        stock_info = yf.Ticker(symbol).info
        if len(stock_info.keys()) > 1:
            data.append(dict(
                ticker=ticker,
                name=stock_info.get("shortName", "N/A"),
                industry=stock_info.get("industryKey", "N/A"),
                sector=stock_info.get("sectorKey", "N/A"),
                market=stock_info.get("exchange", "N/A"),
            ))
            data[-1]["market"] = MARKET.get(
                data[-1]["market"], data[-1]["market"])

    if not data:
        return ReturnYFinance(
            data=data,
            message=f"No stock info data found (Maybe wrong ticker)",
            status=422)
    return ReturnYFinance(data=data, message="Success", status=200)


def fetchNews(tickers: tuple[str]) -> ReturnYFinance:
    """
    Apple 주식 뉴스 데이터를 yfinance를 사용하여 수집합니다.

    :return: DataFrame containing the news data
    """
    data = []
    for ticker in tickers:
        news = yf.Ticker(ticker).news
        if news:
            data += news

    if not data:
        return ReturnYFinance(
            data=data,
            message=f"No news data found (Maybe wrong ticker)",
            status=422)
    return ReturnYFinance(data=data, message="Success", status=200)


def addTickerColumn(ticker: str, data: DataFrame) -> DataFrame:
    """
    주식 데이터에 Ticker 열을 추가합니다.

    :param ticker: 주식 종목 코드
    :param data: 주식 데이터
    :return: Ticker 열이 추가된 주식 데이터
    """
    data["Ticker"] = ticker
    return data


def fetchPrice(
    tickers: tuple[str], start: str, end: str, interval: str = "1d"
) -> ReturnYFinance:
    """
    Apple 주식 데이터를 yfinance를 사용하여 수집합니다.

    :param start: 데이터 수집 시작 날짜
    :param end: 데이터 수집 종료 날짜
    :param interval: 데이터 수집 간격
    :return: DataFrame containing the stock data
    """
    try:
        start = datetime.strptime(start, "%Y-%m-%d")
        end = datetime.strptime(end, "%Y-%m-%d")
    except ValueError:
        raise ReturnYFinance(data=DataFrame(),
                             message="Invalid date format. Use YYYY-MM-DD.",
                             status=422)

    if start > end:
        raise ReturnYFinance(data=DataFrame(),
                             message="Start date must be before end date.",
                             status=422)

    data = concat([
        addTickerColumn(ticker, yf.download(
            ticker, start=start, end=end, interval=interval))
        for ticker in tickers
    ])

    if data.empty:
        return ReturnYFinance(
            data=data,
            message=f"No price data found (Maybe wrong ticker)",
            status=422)
    return ReturnYFinance(data=data, message="Success", status=200)
