import psycopg2
from dataclasses import dataclass
import os


@dataclass
class DSN:
    host: str
    dbName: str
    user: str
    password: str
    port: str

    def __str__(self):
        return f"postgresql://{self.user}:{self.password}@{self.host}/{self.dbName}"


class Database:
    def __init__(self, dsn):
        self.dsn = dsn
        self.conn = None

    def connect(self):
        if self.conn is None or self.conn.closed:
            self.conn = psycopg2.connect(self.dsn)
        return self.conn

    def get_cursor(self):
        if self.conn is None or self.conn.closed:
            self.connect()
        return self.conn.cursor()

    def close(self):
        if self.conn:
            self.conn.close()

    def commit(self):
        if self.conn:
            return self.conn.commit()

    def commit_and_close(self):
        result = self.commit()
        self.close()
        return result

    def rollback(self):
        if self.conn:
            self.conn.rollback()


# 사용 예
dsn = DSN(
    host=os.getenv('DB_HOST', 'localhost'),
    dbName=os.getenv('DB_NAME', 'dbname'),
    user=os.getenv('DB_USER', 'user'),
    password=os.getenv('DB_PASSWORD', 'password'),
    port=os.getenv('DB_PORT', '5432')
)
db = Database(str(dsn))
del dsn
