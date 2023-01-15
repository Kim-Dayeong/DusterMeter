import pymysql


class Database():
    def __init__(self):
        self.db = pymysql.connect(
            host='localhost', user='root', passwd='raspberry', db='sensor', charset='utf8')
        self.cursor = self.db.cursor()

    def show(self):
        sql = """SELECT * from value """
        # SQL query start
        self.cursor.execute(sql)
        result = self.cursor.fetchall()
        return (result)

    def insert(self, date, pm1, pm15, pm20, hum, temper):
        sql = """insert into value values (%s, %s, %s, %s, %s, %s)"""
        self.cursor.execute(sql, (date, pm1, pm15, pm20, hum, temper))
        self.db.commit()


if __name__ == "__main__":
    db = Database()
    db.show()
