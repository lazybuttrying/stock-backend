from datetime import datetime, timedelta
from datatype import ReturnYFinance
from fastapi import HTTPException


def get_kst_time():
    return datetime.utcnow() + timedelta(hours=9)


def convert_timestamp(time: int):
    return datetime.fromtimestamp(time).strftime('%Y-%m-%d %H:%M:%S')


def check_success(response: ReturnYFinance):
    if response.message != "Success":
        raise HTTPException(
            status_code=response.status, detail=response.message
        )


def raise_exception(e: Exception):
    raise HTTPException(
        status_code=400, detail=f"{e.__class__}: {str(e)}"
    )
