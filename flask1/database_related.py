import pandas as pd
import pymysql
import json
import hashlib
import random
import threading


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


def add_book_with_price():
    # 连接数据库
    conn = connect_mysql()

    try:
        # 开始事务
        conn.begin()

        # 获取游标
        cursor = conn.cursor()

        # 查询表中的记录
        cursor.execute("SELECT book_id FROM books")

        # 获取查询结果
        results = cursor.fetchall()

        # 更新每条记录的price字段
        for result in results:
            price = round(random.uniform(10, 100), 2)
            cursor.execute("UPDATE books SET price = %s WHERE book_id = %s", (price, result[0]))

        # 提交事务
        conn.commit()
    except Exception as e:
        # 出现异常时回滚事务
        conn.rollback()
        print(e)


    finally:
        # 关闭游标和连接
        cursor.close()
        conn.close()


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
    df = pd.read_csv('ratings.csv', encoding='utf-8', usecols=['user_id', 'book_id', 'rating', ], nrows=50000)
    with conn.cursor() as cursor:
        for index, row in df.iterrows():
            sql = "INSERT INTO ratings(user_id, book_id, ratings) VALUES (%s, %s, %s)"
            cursor.execute(sql, (row['user_id'], row['book_id'], row['rating']))
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
    query = f"SELECT * FROM books2 WHERE book_id IN ({ids})"

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
            "year": row[3],
            "language_code": row[4],
            "rating": row[5],
            "work_text_reviews_count": row[6],
            "image_url": row[7],
            'price': row[8]
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
    is_admin = '0'
    if result:
        print("Error: User name already exists")
        return 0, 0

    # 插入新用户信息
    sql = "INSERT INTO user (username, password,isAdmin) VALUES (%s, %s,%s)"
    cursor.execute(sql, (username, password, is_admin))
    db.commit()
    print("register success")
    # 编写 SQL 语句
    sql = "SELECT id FROM user WHERE username = %s"

    # 执行 SQL 语句
    cursor.execute(sql, (username,))

    # 获取查询结果
    result = cursor.fetchone()
    print("查询id的结果：", result)
    # 将查询结果中的第一列(user_id)赋值给变量id
    id = result[0]

    # 关闭数据库连接
    db.close()
    return 1, id


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
    id = result[0]
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
        return 2, id
    else:
        return 1, id


# 查询评分前100的书，随机返回6本
def search_top_books():
    db = connect_mysql()
    cursor = db.cursor()
    query = 'SELECT * FROM books2 ORDER BY rating DESC LIMIT 60'
    cursor.execute(query)
    results = cursor.fetchall()
    books = random.sample(results, k=6)
    book_list = []
    for row in books:
        row_data = {
            "book_id": row[0],
            "title": row[1],
            "authors": row[2],
            "language_code": row[4],
            "rating": row[5],
            "image_url": row[7],
            "price":row[8]
            # 添加其他需要的字段
        }
        book_list.append(row_data)
    query = 'SELECT * FROM books2 '
    cursor.execute(query)
    results = cursor.fetchall()
    books = random.sample(results, k=3)
    for row in books:
        row_data = {
            "book_id": row[0],
            "title": row[1],
            "authors": row[2],
            "language_code": row[4],
            "rating": row[5],
            "image_url": row[7],
            "price": row[8]
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
    query = 'SELECT * FROM books2 ORDER BY rating DESC LIMIT 80'
    cursor.execute(query)
    results = cursor.fetchall()
    books = random.sample(results, k=50)
    # 将查询结果转换成字典类型的列表，并返回JSON格式响应
    books_list = []
    for row in books:
        book = {
            'book_id': row[0],
            'title': row[1],
            'authors': row[2],
            'image_url': row[7],
            'price': row[8]
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
    db = connect_mysql()
    cursor = db.cursor()
    cursor.execute("DELETE FROM orders WHERE paycheck = 0")
    db.commit()
    query = 'SELECT * FROM orders'
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


def get_my_order_db(user_id):
    db = connect_mysql()
    try:
        cursor = db.cursor()
        cursor.execute("DELETE FROM orders WHERE paycheck = 0")
        db.commit()
        cursor.execute("SELECT * FROM orders WHERE userid=%s", (user_id,))
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
        cursor.close()
        return json_result
    except Exception as e:
        return str(e)


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


# 获取该用户对书的评论，返回书的信息和评论
def get_user_reviews_db(user_id):
    db = connect_mysql()
    # 使用 with 语句保证连接自动关闭
    with db.cursor() as cursor:
        # 执行查询语句
        sql = 'SELECT * FROM reviews WHERE user_id = %s'
        cursor.execute(sql, (user_id,))
        review_results = cursor.fetchall()
        # 将查询结果组成一个列表，其中每个元素是一个包含用户名和评价内容的字典
        book_id_list = [r[1] for r in review_results]
        # 准备查询语句
        ids = ",".join([str(id) for id in book_id_list])
        query = f"SELECT * FROM books2 WHERE book_id IN ({ids})"

        # 执行查询
        cursor = db.cursor()
        cursor.execute(query)

        # 提取结果
        rows = cursor.fetchall()

        # 将结果转换为JSON格式
        result = []
        reviews = []
        for row in review_results:
            reviews.append(row[2])
        i = 0
        for row in rows:
            row_data = {
                'book_id':row[0],
                "title": row[1],
                "authors": row[2],
                "image_url": row[7],
                "review": reviews[i],
                # 添加其他需要的字段
            }
            result.append(row_data)
            i += 1

        json_result = json.dumps(result)
    # 关闭数据库连接
    db.close()
    # 返回评价列表
    return json_result
def delete_reviews_from_db(uid,bookid):
    conn=connect_mysql()
    try:
        # 创建游标对象
        with conn.cursor() as cursor:
            # 构造 SQL 语句
            sql = 'DELETE FROM reviews WHERE user_id=%s AND book_id=%s'

            # 执行 SQL 语句
            cursor.execute(sql, (uid, bookid))

        # 提交事务
        conn.commit()
        print('Delete successfully!')
        return True

    except Exception as e:
        # 发生错误时回滚事务
        conn.rollback()
        print('Error:', e)
        return  False

    finally:
        # 关闭数据库连接
        conn.close()


# 获取全部评分
def get_ratings_from_db():
    # 创建数据库连接
    connection = pymysql.connect(host='localhost', user='root', password='123456',
                                 db='reccom_system', charset='utf8mb4', cursorclass=pymysql.cursors.DictCursor)

    # 执行查询
    sql_query = "SELECT user_id, book_id, ratings FROM ratings_copy1"
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


# 获取该用户全部评分
def get_user_rating_db(user_id):
    conn = connect_mysql()
    cursor = conn.cursor()
    sql = "SELECT user_id, book_id, ratings FROM ratings_copy1 WHERE user_id=%s"
    cursor.execute(sql, user_id)
    result = cursor.fetchall()
    ratings = []
    for row in result:
        ratings.append(row[2])
    # 将查询结果组成一个列表，其中每个元素是一个包含用户名和评价内容的字典
    book_id_list = [r[1] for r in result]
    # 准备查询语句
    ids = ",".join([str(id) for id in book_id_list])
    query = f"SELECT * FROM books2 WHERE book_id IN ({ids})"
    # 执行查询
    cursor = conn.cursor()
    cursor.execute(query)

    # 提取结果
    rows = cursor.fetchall()
    # 将结果转换为JSON格式
    result = []
    i = 0
    for row in rows:
        row_data = {
            "title": row[1],
            "authors": row[2],
            "image_url": row[7],
            "rating": ratings[i],
            # 添加其他需要的字段
        }
        result.append(row_data)
        i += 1
    json_result = json.dumps(result)
    # 关闭数据库连接
    cursor.close()
    conn.close()
    # 返回评价列表
    return json_result


def get_recom_books_for_user(user_id):
    db = connect_mysql()
    cursor = db.cursor()
    # 查询该用户的所有推荐书籍编号
    sql = f"SELECT book_id FROM user_recom_books WHERE user_id={user_id}"
    cursor.execute(sql)
    results = cursor.fetchall()

    # 若该用户没有推荐书籍则调用函数A
    if len(results) == 0:
        results = search_top_books()
        print("未找到推荐书籍，随机推荐")
        # 差一步jsonfy
        return results
    # 将查询结果转化为列表返回
    bookids = [r[0] for r in results]
    return searchBooks(bookids)


def save_user_books(user_id, book_ids):
    # 连接数据库
    db = connect_mysql()
    cursor = db.cursor()

    try:
        # 删除 uid=n 的所有记录
        delete_sql = "DELETE FROM user_recom_books WHERE user_id = %s"
        cursor.execute(delete_sql, (user_id,))
        db.commit()

        # 构造待保存的数据列表
        rows = [(user_id, book_id) for book_id in book_ids]

        # 执行插入操作，并提交事务
        insert_sql = "INSERT INTO user_recom_books(user_id, book_id) VALUES (%s, %s)"
        cursor.executemany(insert_sql, rows)
        db.commit()
    except Exception as e:
        # 如果出错，则回滚事务
        db.rollback()
        raise e
    finally:
        # 关闭数据库连接
        cursor.close()
        db.close()


# 开启子线程
def run_in_thread(func):
    def wrapper(*args, **kwargs):
        t = threading.Thread(target=func, args=args, kwargs=kwargs)
        t.start()

    return wrapper


def change_user_ratings(user_id, book_id, rating):
    db = connect_mysql()
    cursor = db.cursor()
    try:
        sql = "SELECT COUNT(*) FROM `ratings_copy1` WHERE user_id = %s" % user_id
        cursor.execute(sql)
        count = cursor.fetchone()[0]
        # 查询用户，判断用户是否存在
        if count == 0:
            is_new_user = 1
            print("is new user,train")
        else:
            is_new_user = 0
            print("not new user")
        # 查询该用户对该书籍是否已经评分过
        sql = f"SELECT ratings FROM ratings_copy1 WHERE user_id={user_id} AND book_id={book_id}"
        cursor.execute(sql)
        result = cursor.fetchone()

        # 如果该用户已经评分过，则更新评分值
        if result is not None:
            old_rating = result[0]
            new_rating = rating
            sql = f"UPDATE ratings_copy1 SET ratings={new_rating} WHERE user_id={user_id} AND book_id={book_id}"
            cursor.execute(sql)
            db.commit()
            print("update rating")

        # 如果该用户未评分过，则插入评分值
        else:
            sql = f"INSERT INTO ratings_copy1(user_id, book_id, ratings) VALUES ({user_id}, {book_id}, {rating})"
            cursor.execute(sql)
            db.commit()
            is_new_user = 1
            print("insert rating,train")
        return is_new_user
    except Exception as e:
        # 如果出错，则回滚事务
        db.rollback()
        raise e
    finally:
        # 关闭数据库连接
        cursor.close()
        db.close()


def get_user_modify(user_id):
    db = connect_mysql()
    cursor = db.cursor()
    # 查询总评分数量，如果是5的倍数，调用函数A
    sql = f"SELECT modify FROM user_modify WHERE user_id={user_id}"
    cursor.execute(sql)
    result = cursor.fetchone()
    if result is None:
        modify = 0
    else:
        modify = result[0]
    modify = int(modify)
    print("modify", modify)
    modify += 1
    if modify % 2 == 0:
        updata = 1
    else:
        updata = 0
    sql = f"REPLACE INTO user_modify (user_id, modify) VALUES ({user_id}, {modify})"
    cursor.execute(sql)
    db.commit()
    return updata


def get_user_to_book_rating_db(user_id, book_id):
    # 建立数据库连接
    conn = connect_mysql()

    # 使用游标查询数据
    with conn.cursor() as cursor:
        sql = f"SELECT ratings FROM ratings_copy1 WHERE user_id=%s AND book_id=%s"
        cursor.execute(sql, (user_id, book_id))
        result = cursor.fetchone()

    # 关闭数据库连接
    conn.close()
    return result


def getUserInfo_db(uid):
    conn = connect_mysql()
    cursor = conn.cursor()

    # 查询 uid 的 username 和 avatar
    sql = """SELECT username, avatar,password,nickname,address,gender,phone FROM user WHERE id = %s"""
    cursor.execute(sql, uid)
    result = cursor.fetchone()

    data = {'username': result[0],
            'avatar': result[1],
            'password': result[2],
            'nickname': result[3],
            'address': result[4],
            'gender': result[5],
            'phone': result[6]
            }
    json_data = json.dumps(data)

    # 关闭数据库连接
    cursor.close()
    conn.close()
    return json_data


def fuzzy_search_book(title):
    conn = connect_mysql()
    cur = conn.cursor()
    # 使用拼接字符串的方式实现模糊搜索
    sql = "SELECT book_id, title, author, rating,image_url,price FROM books2 WHERE title LIKE '%%" + title + "%%'"
    cur.execute(sql)
    results = cur.fetchall()
    books_list = []
    for row in results:
        book = {
            'book_id': row[0],
            'title': row[1],
            'authors': row[2],
            'average_rating': row[3],
            'image_url': row[4],
            'price': row[5]

        }
        books_list.append(book)
    json_result = json.dumps(books_list)
    return json_result


def delete_cart_items_by_bookid(connection, book_id):
    """删除购物车中指定书籍的记录"""
    try:
        with connection.cursor() as cursor:
            # 查询购物车中是否存在该书籍
            sql = "SELECT * FROM cart WHERE bookid=%s"
            cursor.execute(sql, book_id)
            result = cursor.fetchone()

            if result is not None:
                # 如果存在，则将对应的记录删除
                sql = "DELETE FROM cart WHERE bookid=%s"
                cursor.execute(sql, book_id)
    except:
        # 发生异常时回滚事务
        connection.rollback()
        raise


# 下订单
def place_an_order(tag, buyerName, address, phone):
    # 保存订单信息到orders表
    conn = connect_mysql()
    paycheck="1"
    try:
        with conn.cursor() as cursor:
            sql = "UPDATE orders SET buyername = %s, address = %s, phone = %s,paycheck=%s WHERE tag = %s"
            cursor.execute(sql, (buyerName, address, phone,paycheck, tag))
            print("订单创建成功")
            # 查询所有tag字段等于指定值的订单
            sql = "SELECT book_id FROM orders WHERE tag=%s"
            cursor.execute(sql, tag)
            results = cursor.fetchall()
            # 遍历所有的订单
            for result in results:
                # 删除购物车中对应书籍的记录
                delete_cart_items_by_bookid(conn, result[0])
                # cursor.execute(sql, result['id'])
            print("购物车删除成功")
            conn.commit()
        return True
    except Exception as e:
        conn.rollback()
        print(str(e))
        return False
    finally:
        conn.close()


# 修改订单
def update_order_db(order_id, quantity, price, name, phone, address, totalPrice):
    conn = connect_mysql()
    cursor = conn.cursor()
    try:
        # 修改订单
        sql = "UPDATE orders SET quantity=%s,price=%s,buyername=%s,phone=%s,address=%s,totalPrice=%s WHERE orderid=%s"
        cursor.execute(sql, (quantity, price, name, phone, address, totalPrice, order_id))
        # 提交事务
        conn.commit()
        return True
    except:
        # 出现异常，回滚事务
        conn.rollback()
        return False
    finally:
        # 关闭游标和连接
        cursor.close()
        conn.close()


# 删除订单
def delete_order_db(order_id):
    conn = connect_mysql()
    cursor = conn.cursor()
    try:
        # 删除订单
        sql = "DELETE FROM orders WHERE orderid=%s"
        cursor.execute(sql, (order_id,))
        # 提交事务
        conn.commit()
        return True
    except:
        # 出现异常，回滚事务
        conn.rollback()
        return False
    finally:
        # 关闭游标和连接
        cursor.close()
        conn.close()


# 用户评论
def make_comment_db(user_id, book_id, comment, rating):
    connection = connect_mysql()
    try:
        with connection.cursor() as cursor:
            # 插入数据到MySQL数据库中
            sql = "INSERT INTO `reviews` (`user_id`, `book_id`,`review`, `rating` ) VALUES (%s, %s, %s, %s)"
            cursor.execute(sql, (user_id, book_id, comment, rating))
            connection.commit()

            # 返回JSON响应

            return True

    except Exception as ex:
        # 处理异常，返回错误响应
        error_message = f"An error occurred: {ex}"
        print(error_message)
        return False


# 更新用户信息，管理员
def update_user_info_db(username, password, avatar, address, uid):
    try:
        # 连接数据库并开启事务
        db = connect_mysql()
        cursor = db.cursor()
        cursor.execute('START TRANSACTION')

        # 执行更新操作
        sql = 'UPDATE user SET username=%s, password=%s, avatar=%s, address=%s WHERE id=%s'
        cursor.execute(sql, (username, password, avatar, address, uid))

        # 提交事务
        db.commit()

        # 返回成功结果
        return True
    except Exception as e:
        # 发生错误时回滚事务
        db.rollback()
        print(str(e))
        # 返回错误结果
        return False
    finally:
        # 关闭数据库连接
        db.close()

def add_user_db(avatar, username, password, address):
    conn = connect_mysql()
    cursor = conn.cursor()
    try:
        # 添加用户
        sql = "INSERT INTO user (avatar, username, password, address) VALUES (%s, %s, %s, %s)"
        cursor.execute(sql, (avatar, username, password, address))
        conn.commit()
        return True
    except:
        # 出现异常，回滚事务
        conn.rollback()
        return False
    finally:
        # 关闭游标和连接
        cursor.close()
        conn.close()
    # 将参数插入到数据库中


# 删除用户
def delete_user_db(user_id):
    conn = connect_mysql()
    cursor = conn.cursor()
    try:
        # 删除用户
        sql = "DELETE FROM user WHERE id=%s"
        cursor.execute(sql, (user_id,))
        # 提交事务
        conn.commit()
        return True
    except:
        # 出现异常，回滚事务
        conn.rollback()
        return False
    finally:
        # 关闭游标和连接
        cursor.close()
        conn.close()


# 修改个人信息
def user_info_modify_db(nickname, password, avatar, address, phone, gender, uid):
    try:
        # 连接数据库并开启事务
        db = connect_mysql()
        cursor = db.cursor()
        cursor.execute('START TRANSACTION')

        # 执行更新操作
        sql = 'UPDATE user SET nickname=%s, password=%s, avatar=%s, address=%s,phone=%s,gender=%s WHERE id=%s'
        cursor.execute(sql, (nickname, password, avatar, address, phone, gender, uid))

        # 提交事务
        db.commit()

        # 返回成功结果
        return True
    except Exception as e:
        # 发生错误时回滚事务
        db.rollback()
        print(str(e))
        # 返回错误结果
        return False
    finally:
        # 关闭数据库连接
        db.close()


def modify_book_db(book_id, book_cover, book_title, book_author, book_price, book_description, book_language):
    db = connect_mysql()
    cursor = db.cursor()
    # 检查是否存在该书
    select_query = f"SELECT * FROM books2 WHERE book_id='{book_id}'"
    cursor.execute(select_query)
    exist_book = cursor.fetchone()

    if exist_book:
        # 如果存在该书，则进行更新操作
        update_query = f"UPDATE books2 SET image_url='{book_cover}', title='{book_title}', author='{book_author}', price={book_price}, description='{book_description}', language='{book_language}' WHERE book_id='{book_id}'"
        try:
            cursor.execute(update_query)
            db.commit()
            return 1
        except Exception as e:
            db.rollback()  # 回滚事务
            msg = str(e)
            print(msg)
            return 0

    else:
        # 如果不存在该书，则进行插入操作
        insert_query = f"INSERT INTO books2(book_id, image_url, title, author, price, description, language) VALUES ('{book_id}', '{book_cover}', '{book_title}', '{book_author}', {book_price}, '{book_description}', '{book_language}')"
        try:
            cursor.execute(insert_query)
            db.commit()
            return 2
        except Exception as e:
            db.rollback()  # 回滚事务
            msg = str(e)
            print(msg)
            return 0


# 删除图书
def delete_book_db(book_id):
    conn = connect_mysql()
    cursor = conn.cursor()
    try:
        # 删除图书
        sql = "DELETE FROM books2 WHERE book_id=%s"
        cursor.execute(sql, (book_id,))
        # 提交事务
        conn.commit()
        return True
    except:
        # 出现异常，回滚事务
        conn.rollback()
        return False
    finally:
        # 关闭游标和连接
        cursor.close()
        conn.close()


def add_favorite(userid, bookid, title, author, rating, date, bookCover):
    db = connect_mysql()
    if not all([userid, bookid, title, author, rating, date]):
        return False, 'missing parameters'
    try:
        cursor = db.cursor()
        cursor.execute("SELECT * FROM user_favorite WHERE userid=%s AND bookid=%s", (userid, bookid))
        if cursor.fetchone():
            return False, 'record already exists'
        cursor.execute(
            "INSERT INTO user_favorite (userid, bookid, title, author, rating, date_n,bookCover) VALUES (%s, %s, %s, %s, %s, %s,%s)",
            (userid, bookid, title, author, rating, date, bookCover))
        db.commit()
        cursor.close()
        return True, 'success!'
    except Exception as e:
        db.rollback()
        return False, str(e)
def delete_favorite_from_db(uid,bookid):
    conn=connect_mysql()
    try:
        # 创建游标对象
        with conn.cursor() as cursor:
            # 构造 SQL 语句
            sql = 'DELETE FROM user_favorite WHERE userid=%s AND bookid=%s'

            # 执行 SQL 语句
            cursor.execute(sql, (uid, bookid))

        # 提交事务
        conn.commit()
        print('Delete successfully!')
        return True

    except Exception as e:
        # 发生错误时回滚事务
        conn.rollback()
        print('Error:', e)
        return  False

    finally:
        # 关闭数据库连接
        conn.close()

def get_order_info_db(tag):
    connection = connect_mysql()
    # 查询用户收藏记录
    try:
        with connection.cursor() as cursor:
            sql = 'SELECT * FROM orders WHERE tag = %s'
            cursor.execute(sql, tag)
            rows = cursor.fetchall()
            # 将结果转换为JSON格式
            result = []
            for row in rows:
                row_data = {
                    "book_id": row[0],
                    "title": row[3],
                    "authors": row[2],
                    "image_url": row[1],
                    "price": row[4],
                    "quantity": row[6],
                    # 添加其他需要的字段
                }
                result.append(row_data)

            json_result = json.dumps(result)

    finally:
        connection.close()
    return json_result


def get_favorite(userId):
    connection = connect_mysql()
    # 查询用户收藏记录
    try:
        with connection.cursor() as cursor:
            sql = 'SELECT bookid, title,author,bookCover,rating,date_n FROM user_favorite WHERE userid = %s'
            cursor.execute(sql, userId)
            rows = cursor.fetchall()

            # 将结果转换为JSON格式
            result = []
            for row in rows:
                row_data = {
                    "book_id": row[0],
                    "title": row[1],
                    "authors": row[2],
                    "image_url": row[3],
                    "rating": row[4],
                    "date": row[5],
                    # 添加其他需要的字段
                }
                result.append(row_data)

            json_result = json.dumps(result)

    finally:
        connection.close()
    return json_result


def get_cart_list(uid):
    connection = connect_mysql()
    try:
        # Query the database
        with connection.cursor() as cursor:
            # Get the cart data from the database
            sql = "SELECT * FROM cart WHERE uid = %s"
            cursor.execute(sql, uid)
            cart_data = cursor.fetchall()
            shop_id = 1
            shop_name = 'My Shop'
            # Group the cart data by shop
            shop_data = {shop_id: {'shopId': shop_id, 'shopName': shop_name, 'cartlist': []}}
            for item in cart_data:
                shop_data[shop_id]['cartlist'].append({
                    'id': 1,
                    'shopId': shop_id,
                    'shopName': shop_name,
                    'defaultPic': item[3],
                    'productId': item[6],
                    'productName': item[0],
                    'color': item[4],
                    'price': item[1],
                    'count': item[2]
                })

        # Convert the shop data to JSON and return
        return list(shop_data.values())

    finally:
        # Close the database connection
        connection.close()


def settle_cart_db(tag, products,paycheck):
    connection = connect_mysql()
    try:
        # 将每个条目插入到数据库中
        for item in products:
            image = item['image']
            name = item['name']
            price = item['price']
            quantity = item['quantity']
            user_id = item['userId']
            bookId = item['bookId']
            author=item['author']
            print(type(price))
            print(type(quantity))
            total_price = eval(price) * quantity
            # 插入到数据库中
            with connection.cursor() as cursor:
                sql = "INSERT INTO orders (book_cover,author, title, price, quantity, userid,book_id,totalPrice,tag,paycheck) VALUES (%s,%s, %s, %s, %s, %s,%s,%s,%s,%s)"
                cursor.execute(sql, (image,author, name, price, quantity, user_id, bookId, total_price, tag,paycheck))
            connection.commit()

    except Exception as e:
        print("Error occurred: ", e)
        connection.rollback()
        return False

    finally:
        # 关闭数据库连接
        connection.close()
    return True


def add_to_cart_db(book_id, book_cover, title, price, count, author, uid):
    connection = connect_mysql()
    try:
        # 插入到数据库中
        with connection.cursor() as cursor:
            # 查询购物车中是否已经存在该书籍
            sql = "SELECT * FROM cart WHERE bookid=%s AND uid=%s"
            cursor.execute(sql, (book_id, uid))
            result = cursor.fetchone()
            if result is not None:
                # 如果存在，则将对应bookid的count加1
                sql = "UPDATE cart SET count=count+1 WHERE bookid=%s AND uid=%s"
                cursor.execute(sql, (book_id, uid))
            else:
                # 如果不存在，则插入到数据库中
                sql = "INSERT INTO cart (bookid, defaultPic, productName, price, count, author, uid) VALUES (%s, %s, %s, %s, %s, %s, %s)"
                cursor.execute(sql, (book_id, book_cover, title, price, count, author, uid))
        connection.commit()
        # 返回成功消息
        return True

    except Exception as e:
        # 返回错误消息
        return False

    finally:
        # 关闭数据库连接
        connection.close()


def delete_cart_items_by_uid_and_bookid(products):
    connection = connect_mysql()
    """删除购物车中指定用户和指定书籍的记录"""
    try:
        for item in products:
            print(item)
            user_id = item['userId']
            book_id = item['bookId']
            with connection.cursor() as cursor:
                # 查询购物车中是否存在该用户和书籍
                sql = "SELECT * FROM cart WHERE uid=%s AND bookid=%s"
                cursor.execute(sql, (user_id, book_id))
                result = cursor.fetchone()

                if result is not None:
                    # 如果存在，则将对应的记录删除
                    sql = "DELETE FROM cart WHERE uid=%s AND bookid=%s"
                    cursor.execute(sql, (user_id, book_id))
            connection.commit()
        return True
    except Exception as e:
        # 发生异常时回滚事务
        connection.rollback()
        print(e)
        raise
