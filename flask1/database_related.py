import pandas as pd
import pymysql
import json
import hashlib
import random

def read_csv(file_path):
    """
    解决UnicodeDecodeError: 'utf-8' codec can't decode bytes in position 68763-68764: invalid continuation byte
    原因是里面包含中文字符（乱码）
    这里是用gbk18030解决的
    """
    try:
        df = pd.read_csv(file_path, encoding='utf-8')
    except UnicodeDecodeError:
        try:
            df = pd.read_csv(file_path, encoding='gbk')
            print('gbk')
        except UnicodeDecodeError:
            df = pd.read_csv(file_path, encoding='gb18030')
            print('gbk18030')
    return df
def truncate_string(s, max_len):
    """
    当字符串长度超过数据库限制时，将其截断或写为默认值
    """
    if s is None:
        return 'unknown'

    if len(s) > max_len:
        return 'unknown'
    else:
        return s

def createBooksDb():
    # 连接 MySQL 数据库
    conn = pymysql.connect(host='localhost', user='root', password='123456',
                       db='reccom_system', charset='utf8mb4', cursorclass=pymysql.cursors.DictCursor)
    # 读取 CSV 文件
    df = pd.read_csv('books.csv', encoding='gb18030',usecols=['book_id', 'title', 'authors', 'original_publication_year',
                     'language_code', 'average_rating', 'work_text_reviews_count', 'image_url'])
    print(df)
    df = df.fillna('')
    # 插入数据
    with conn.cursor() as cursor:
        for index, row in df.iterrows():
            authors = truncate_string(row[0], 255)
            sql = "INSERT INTO books(book_id, title, author,year,language,rating,review_count,image_url) VALUES (%s, %s, %s,%s, %s, %s, %s, %s)"
            cursor.execute(sql, (row['book_id'], row['title'], authors, row['original_publication_year'],
                           row['language_code'], row['average_rating'], row['work_text_reviews_count'], row['image_url']))
        conn.commit()
    conn.close()


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
        print("Error: User name already exists")
        return False

    # 插入新用户信息
    sql = "INSERT INTO user (username, password) VALUES (%s, %s)"
    cursor.execute(sql, (username, encrypt_password(password)))
    db.commit()
    print("register success")
    # 关闭数据库连接
    db.close()
    return True


def login_db(username, password):
    """
    账号登录
    """
    db = connect_mysql()
    cursor = db.cursor()
    # 查询用户信息
    sql = "SELECT id, password,isAdmin FROM user WHERE username=%s"
    cursor.execute(sql, (username,))
    result = cursor.fetchone()
    if not result:
        print("Error: user name does not exist")
        return 0
    # 校验密码是否正确
    if result[1] != encrypt_password(password):
        print("Error: Incorrect password")
        return 0
    # 登录时使用错误密码，会提示密码不正确
    print(result)
    print("Login successful!")
    # 关闭数据库连接
    db.close()
    # 加入验证是否为管理员
    if (result[2] == '1'):
        return 2
    else:
        return 1
#查询评分前100的书，随机返回6本
def search_top_books():
    db=connect_mysql()
    cursor = db.cursor()
    query = 'SELECT * FROM books ORDER BY rating DESC LIMIT 100'
    cursor.execute(query)
    results = cursor.fetchall()
    books = random.sample(results, k=6)
    book_list = []
    for row in books:
        row_data = {
            "title": row[1],
            "authors": row[2],
            "rating": row[5],
            "image_url": row[7],
            # 添加其他需要的字段
        }
        book_list.append(row_data)

    json_result = json.dumps(book_list)
    return json_result
def get_all_Books_db():
    db=connect_mysql()
    cursor=db.cursor()

    # 查询随机生成的300个图书
    query = 'SELECT * FROM books ORDER BY rating DESC LIMIT 1000'
    cursor.execute(query)
    results = cursor.fetchall()
    books = random.sample(results, k=300)
    # 将查询结果转换成字典类型的列表，并返回JSON格式响应
    books = []
    for row in results:
        book = {
            'book_id': row[0],
            'title': row[1],
            'authors': row[2],
            'image_url': row[3],
            #todo 加入 'price': row[4]
        }
        books.append(book)
    return books






