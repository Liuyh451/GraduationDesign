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
    df = pd.read_csv('books.csv', encoding='gb18030',
                     usecols=['book_id', 'title', 'authors', 'original_publication_year',
                              'language_code', 'average_rating', 'work_text_reviews_count', 'image_url'])
    print(df)
    df = df.fillna('')
    # 插入数据
    with conn.cursor() as cursor:
        for index, row in df.iterrows():
            authors = truncate_string(row[0], 255)
            sql = "INSERT INTO books(book_id, title, author,year,language,rating,review_count,image_url) VALUES (%s, %s, %s,%s, %s, %s, %s, %s)"
            cursor.execute(sql, (row['book_id'], row['title'], authors, row['original_publication_year'],
                                 row['language_code'], row['average_rating'], row['work_text_reviews_count'],
                                 row['image_url']))
        conn.commit()
    conn.close()
def create_ratings_db():
    # 连接 MySQL 数据库
    conn = pymysql.connect(host='localhost', user='root', password='123456',
                       db='reccom_system', charset='utf8mb4', cursorclass=pymysql.cursors.DictCursor)
    df = pd.read_csv('ratings.csv', encoding='utf-8',usecols=['user_id', 'book_id', 'rating',],nrows=50000)
    with conn.cursor() as cursor:
        for index, row in df.iterrows():
            sql = "INSERT INTO ratings(user_id, book_id, ratings) VALUES (%s, %s, %s)"
            cursor.execute(sql, (row['user_id'], row['book_id'],  row['rating']))
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
    db = connect_mysql()
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
    cursor.execute(sql, (username, password))
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
    if result[1] != password:
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


# 查询评分前100的书，随机返回6本
def search_top_books():
    db = connect_mysql()
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
    db.close()
    return json_result


def get_all_Books_db():
    db = connect_mysql()
    cursor = db.cursor()

    # 查询随机生成的300个图书
    query = 'SELECT * FROM books ORDER BY rating DESC LIMIT 3000'
    cursor.execute(query)
    results = cursor.fetchall()
    books = random.sample(results, k=300)
    # 将查询结果转换成字典类型的列表，并返回JSON格式响应
    books_list = []
    for row in books:
        book = {
            'book_id': row[0],
            'title': row[1],
            'authors': row[2],
            'image_url': row[7],
            # todo 加入 'price': row[4]
        }
        books_list.append(book)
    json_result = json.dumps(books_list)
    db.close()
    return json_result


def get_all_reviews_db():
    db = connect_mysql()
    cursor = db.cursor()
    # 查询reviews表中的review列
    query = 'SELECT review, user_id FROM reviews'
    cursor.execute(query)
    results = cursor.fetchall()

    # 构造包含review列的列表
    review_list = [{'review': r[0], 'user_id': r[1]} for r in results]
    db.close()
    return review_list


def get_all_users_db():
    query = 'SELECT * FROM user'
    db = connect_mysql()
    cursor = db.cursor()
    cursor.execute(query)
    result = cursor.fetchall()
    user = []
    for row in result:
        row_data = {
            "id": row[0],
            "username": row[1],
            "password": row[2],
            "isAdmin": row[3],
            "avatar": row[4]
            # 添加其他需要的字段
        }
        user.append(row_data)
    json_result = json.dumps(user)
    db.close()
    return json_result


def get_all_orders_db():
    query = 'SELECT * FROM orders'
    db = connect_mysql()
    cursor = db.cursor()
    cursor.execute(query)
    result = cursor.fetchall()
    orders = []
    for row in result:
        row_data = {
            "orderid": row[0],
            "book_cover": row[1],
            "author": row[2],
            "title": row[3],
            "price": row[4],
            "buyername": row[5],
            "quantity": row[6],
            "totalPrice": row[7],
            "address": row[8],
            "phone": row[9]
            # 添加其他需要的字段
        }
        orders.append(row_data)
    json_result = json.dumps(orders)
    db.close()
    return json_result


def get_book_reviews_db(book_id):
    db = connect_mysql()
    # 使用 with 语句保证连接自动关闭
    with db.cursor() as cursor:
        # 执行查询语句
        sql = 'SELECT * FROM reviews WHERE book_id = %s'
        cursor.execute(sql, (book_id,))
        results = cursor.fetchall()
        # 将查询结果组成一个列表，其中每个元素是一个包含用户名和评价内容的字典
        reviews = []
        for row in results:
            row_data = {
                "user_id": row[0],
                "book_id": row[1],
                "review": row[2],
                # 添加其他需要的字段
            }
            reviews.append(row_data)
        json_result = json.dumps(reviews)
    # 关闭数据库连接
    db.close()
    # 返回评价列表
    return json_result
def get_ratings_from_db():
    # 创建数据库连接
    connection = pymysql.connect(host='localhost', user='root', password='123456',
                        db='reccom_system', charset='utf8mb4', cursorclass=pymysql.cursors.DictCursor)

    # 执行查询
    sql_query = "SELECT user_id, book_id, ratings FROM ratings"
    with connection.cursor() as cursor:
        cursor.execute(sql_query)
        result = cursor.fetchall()
    
    # 将查询结果转换为DataFrame
    ratings = pd.DataFrame(result)

    # 删除重复的记录
    is_duplicated = ratings.duplicated(subset=['user_id', 'book_id'])
    ratings = ratings[~is_duplicated]
    ratings['ratings'] = ratings['ratings'].astype(int)
    # 将评分数据转化为矩阵
    ratings_matrix = ratings.pivot(
        index='user_id',
        columns='book_id',
        values='ratings'
    ).fillna(0)
    # 输出用户1对书籍1180的评分

    # 关闭数据库连接
    connection.close()

    # 输出结果
    return ratings_matrix