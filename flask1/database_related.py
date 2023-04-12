import pandas as pd
import pymysql
import json
import hashlib

def createBooksDb():
    # 连接 MySQL 数据库
    conn = pymysql.connect(host='localhost', user='root', password='123456',
                       db='reccom_system', charset='utf8mb4', cursorclass=pymysql.cursors.DictCursor)
    # 读取 CSV 文件
    df = pd.read_csv('test.csv', usecols=['book_id', 'title', 'authors', 'original_publication_year',
                     'language_code', 'average_rating', 'work_text_reviews_count', 'image_url'])
    print(df)
    df = df.fillna('')
    # 插入数据
    with conn.cursor() as cursor:
        for index, row in df.iterrows():
            sql = "INSERT INTO books(book_id, title, author,year,language,rating,review_count,image_url) VALUES (%s, %s, %s,%s, %s, %s, %s, %s)"
            cursor.execute(sql, (row['book_id'], row['title'], row['authors'], row['original_publication_year'],
                           row['language_code'], row['average_rating'], row['work_text_reviews_count'], row['image_url']))
        conn.commit()
    conn.close()

book_id_list = [1, 11, 1202]

def searchBooks(book_id_list):
    # 连接到MySQL数据库
    db = pymysql.connect(
        host="localhost",
        user="root",
        password="123456",
        database="reccom_system"
    )
     # 准备查询语句
    ids = ",".join([str(id) for id in book_id_list])
    query = f"SELECT * FROM books WHERE book_id IN ({ids})"

    # 执行查询
    cursor = db.cursor()
    cursor.execute(query)

    # 提取结果
    rows = cursor.fetchall()

    # 将结果转换为JSON格式
    result = []
    for row in rows:
        row_data = {
            "book_id": row[0],
            "title": row[1],
            "authors": row[2],
            "original_publication_year": row[3],
            "language_code": row[4],
            "average_rating": row[5],
            "work_text_reviews_count": row[6],
            "image_url": row[7],            
            # 添加其他需要的字段
        }
        result.append(row_data)

    json_result = json.dumps(result)

    # 关闭数据库连接
    db.close()

    return json_result
# 连接数据库
def connect_mysql():
    db = pymysql.connect(host="localhost",
                        user="root",
                        password="123456",
                        db="reccom_system",
                        charset='utf8mb4')
    # 获取游标对象
    return db
def encrypt_password(password):
    """
    对密码进行加密
    """
    md5 = hashlib.md5()
    md5.update(password.encode("utf8"))
    return md5.hexdigest()


def register_db(username, password):
    """
    注册账号
    """
    db=connect_mysql()
    cursor = db.cursor()
    # 查询用户名是否已经存在
    sql = "SELECT id FROM user WHERE username=%s"
    cursor.execute(sql, (username,))
    result = cursor.fetchone()
    if result:
        # print("Error: User name already exists")
        return False

    # 插入新用户信息
    sql = "INSERT INTO user (username, password) VALUES (%s, %s)"
    cursor.execute(sql, (username, encrypt_password(password)))
    db.commit()
    # print("register success")
    # 关闭数据库连接
    db.close()
    return True


def login_db(username, password):
    """
    账号登录
    """
    db=connect_mysql()
    cursor = db.cursor()
    # 查询用户信息
    sql = "SELECT id, password FROM user WHERE username=%s"
    cursor.execute(sql, (username,))
    result = cursor.fetchone()
    if not result:
        # print("Error: user name does not exist")
        return False
    #校验密码是否正确
    if result[1] != encrypt_password(password):
        # print("Error: Incorrect password")
        return False
    # 登录时使用错误密码，会提示密码不正确   
    # print("Login successful!")
    # 关闭数据库连接
    db.close()
    return True

#print(searchBooks(book_id_list))

