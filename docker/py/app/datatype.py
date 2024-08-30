from dataclasses import dataclass
from pydantic import BaseModel
from typing import List
from pandas import DataFrame


MARKET = {
    "NMS": "NASDAQ",
    "NYQ": "NYSE",
    "KNK": "KONEX",
    "KOE": "KOSDAQ",
    "KSC": "KOSPI",
    "N/A": "N/A"
}


class BaseRequest(BaseModel):
    tickers: List[str]


class PriceRequest(BaseModel):
    tickers: List[str]
    startDate: str
    endDate: str


@dataclass
class ReturnYFinance:
    data: List[DataFrame]
    message: str
    status: int
