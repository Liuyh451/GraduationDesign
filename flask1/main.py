from flask import Flask, request, jsonify
from flask_cors import CORS
import os
from database_related import *
from model_train import model_train_fun
from recom_algorithm import recom_fun

app = Flask(__name__)
os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'
CORS(app)  # 解决跨域请求问题


@app.route('/login', methods=['POST'])
def login():
    # 获取用户名和密码
    username = request.form.get('username')
    password = request.form.get('password')
    print(username, password)
    # 根据用户名和密码判断登录是否成功
    k, id = login_db(username, password)
    if k == 1:
        print("ok")
        return jsonify(code=200, message='notAdmin', uid=id)
    elif k == 2:
        return jsonify(code=200, message='isAdmin', uid=id)
    else:
        print("no")
        return jsonify(code=201, message='用户名或密码错误')


# 用户注册，用户和管理员接口
@app.route('/register', methods=['POST'])
def register():
    username = request.form.get('username')
    password = request.form.get('password')
    # 判断用户名和密码是否为空
    print(username, password)
    if not (username and password):
        return jsonify({'message': 'Invalid username or password'}), 400
    # 校验用户是否已经存在
    flag, uid = register_db(username, password)
    uid = int(uid)
    if (flag):
        return jsonify({'code': 200, 'uid': uid})
    else:
        return jsonify({'code': 201, 'message': 'Error: User name already exists'})


# 为用户推荐书籍，用户端接口
@app.route('/getRecomBooks', methods=['POST'])
def getRecomBooks():
    # 获取前端传递的用户ID
    # uid = request.form.get('uid')#用于postman测试
    data = json.loads(request.form['data'])
    uid = data['uid']
    print("user_id", uid)
    book_data = get_recom_books_for_user(uid)
    # 返回JSON数据和状态码给前端
    return jsonify({'code': 200, "data": book_data})


cnt = 0


# 获取为用户推荐的图书，用户端接口，废弃版本
@app.route('/api/novels')
def get_novels():
    # 获取前端传来的查询参数 "page"，默认值为 1
    global cnt
    cnt += 1
    page = cnt
    if (cnt == 7):
        cnt = 0
    book_id_list = [5928, 4698, 4765, 3432, 2441, 5176, 8038, 9334, 2659, 2105, 104, 7384, 5067, 2206, 6784, 8166, 1534,
                    560, 4734, 8639, 6945, 513, 1387, 1921, 9547, 5764, 6929, 802, 51, 4365, 5928, 4698]
    # 调用A函数并获取JSON数据和状态码
    # 计算每页的起始和结束索引
    page_size = 4  # 每页显示的小说数
    start_index = (page - 1) * page_size
    end_index = start_index + page_size

    # 获取本页的小说，并将它们转化为 JSON 格式返回给前端
    book_id_list = book_id_list[start_index:end_index]
    data = searchBooks(book_id_list)
    print(len(book_id_list))
    print("第几页", str(page))
    print(book_id_list)
    return jsonify({'code': 200, "data": data})


@app.route('/api/topBooks', methods=['GET'])
def get_random_highest_ratings():
    return search_top_books()



# 获取全部用户，管理端接口
@app.route('/getAllUsers', methods=['GET'])
def get_all_users():
    json_result = get_all_users_db()
    return jsonify({'users': json_result})


# 获取全部图书，管理端接口
@app.route('/getAllBooks', methods=['GET'])
def get_all_books():
    books = get_all_Books_db()
    # todo 这里有个小bug，有时候前端接收不到，也可能是前端的问题
    return jsonify({'books': books})


# 获取全部评论，管理端接口
@app.route('/allReviews', methods=['GET'])
def get_all_reviews():
    review_list = get_all_reviews_db()
    return jsonify({'reviews': review_list})


# 获取该书籍的全部评论，用户端接口
@app.route('/book/reviews', methods=['POST'])
def get_book_reviews():
    request_data = request.json  # 通过 request 对象获取请求数据
    book_id = request_data.get('book_id')  # 获取 book_id 参数
    # book_id = request.form.get('book_id')
    if book_id is None:
        return jsonify({'error': 'book_id is required'}), 400  # 如果 book_id 不存在则返回错误信息

    reviews = get_book_reviews_db(book_id)  # 调用自定义函数从数据库表中获取相关评价

    if len(reviews) == 0:
        return jsonify({'error': 'no reviews found for book_id {}'.format(book_id)}), 404  # 如果没有找到相关评价，则返回 404 错误

    # 将包含评价信息的列表作为JSON返回值
    return jsonify({'book_id': book_id, 'reviews': reviews}), 200


# 获取该用户的全部评论，用户端接口
@app.route('/user/reviews', methods=['POST'])
def get_user_reviews():
    request_data = request.json  # 通过 request 对象获取请求数据
    user_id = request_data.get('uid')
    # userid=request.form.get("uid")
    data = get_user_reviews_db(user_id)
    return jsonify({'code': 200, "data": data})
@app.route('/user/reviews/delete', methods=['POST'])
def delete_user_reviews():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    if(delete_reviews_from_db(user_id,book_id)):
        return jsonify({'code': 200, "msg": "删除成功！"})
    else:
        return jsonify({'code': 201, "msg": "删除失败！"})


# 添加用户收藏，用户端接口
@app.route('/book/favorite', methods=['POST'])
def my_favorite():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    rating = data['rating']
    title = data['title']
    date = data['date']
    bookCover = data['bookCover']
    author = data['author']
    result, msg = add_favorite(user_id, book_id, title, author, rating, date, bookCover)
    return jsonify({'msg': msg})


# 获取用户收藏，用户端接口
@app.route('/book/getfavorite', methods=['POST'])
def get_my_favorite():
    # userid = request.form.get("userid")
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    books = get_favorite(user_id)
    return jsonify({"data": books})
@app.route('/book/favorite/delete', methods=['POST'])
def delete_user_favorite():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    if(delete_favorite_from_db(user_id,book_id)):
        return jsonify({'code': 200, "msg": "删除成功！"})
    else:
        return jsonify({'code': 201, "msg": "删除失败！"})


# 保存用户对书籍的评分，必要时更新模型，用户端接口
@app.route("/rating_and_recom", methods=["POST"])
def rating_and_recom_fun():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    rating = data['rating']
    print(user_id, book_id, rating)
    # 立即返回状态码 200
    if (change_user_ratings(user_id, book_id, rating)):
        print("执行训练")
        model_train_fun()
        # calculation_thread = threading.Thread(target=model_train_fun)
        # calculation_thread.start()
    # 开启新线程并执行计算任务
    if (get_user_modify(user_id)):
        print("执行推荐")
        user_id = int(user_id)
        calculation_thread = threading.Thread(target=recom_fun(user_id - 1))
        calculation_thread.start()
    return "OK", 200


# 获取用户对某本书籍的评分，用户端接口
@app.route("/bookrating", methods=["POST"])
def get_user_to_book_rating():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    print(user_id, book_id)
    # book_id = request.form.get('book_id')
    # user_id = request.form.get('user_id')
    result = get_user_to_book_rating_db(user_id, book_id)
    if result:
        return jsonify({"ratings": result[0]})
    else:
        return jsonify({"ratings": "0"})


# 获取用户对已评价的图书的评分
@app.route("/user/rating", methods=["POST"])
def get_user_all_book_ratings():
    # userid=request.form.get("uid")
    data = json.loads(request.form['data'])
    userid = data['user_id']
    result = get_user_rating_db(userid)
    return jsonify({"ratings": result})


# 获取用户的个人信息，用户端接口
@app.route("/getUserInfo", methods=["POST"])
def get_user_info():
    data = json.loads(request.form['data'])
    user_id = data['user_id']

    # user_id = request.form.get('user_id')
    print(data)
    result = getUserInfo_db(user_id)
    return jsonify({"userInfo": result})


# 搜索图书接口，通用接口
@app.route('/bookSearch', methods=["POST"])
def book_search():
    data = json.loads(request.form['data'])
    title = data['title']
    # title = request.form.get('title')
    print(title)
    # title = request.args.get('title') # 获取查询参数
    books = fuzzy_search_book(title)
    return jsonify({"data": books})


# 获取全部订单，管理端接口
@app.route('/allOrders', methods=['GET'])
def get_all_orders():
    json_result = get_all_orders_db()
    return jsonify({'orders': json_result})


@app.route('/order/myOrder', methods=['POST'])
def get_my_order():
    data = json.loads(request.form['data'])
    user_id = data['userId']
    json_result = get_my_order_db(user_id)
    return jsonify({'orders': json_result})


# 下单接口，用户端接口
@app.route('/order/create', methods=['POST'])
def create_order():
    data = json.loads(request.form['data'])
    tag = data['tag']
    name = data['buyerName']
    address = data['address']
    phone = data['phone']
    if (place_an_order(tag, name, address, phone)):
        return jsonify({'msg': '订单创建成功!'})
    else:
        return jsonify({'msg': '订单创建失败'})


# 订单修改，管理端接口
@app.route('/order/modify', methods=['POST'])
def update_order():
    data = json.loads(request.form['data'])
    username = data['buyerName']
    order_id = data['orderId']
    price = data['price']
    quantity = data['quantity']
    address = data['address']
    phone = data['phone']
    # 计算总价
    amount = float(quantity) * float(price)
    if (update_order_db(order_id, quantity, price, username, phone, address, amount)):
        return jsonify({'success': True, 'msg': '订单操作成功'})
    else:
        return jsonify({'success': False, 'msg': '订单操作失败'})


# 订单删除，管理端接口
@app.route('/order/delete', methods=['POST'])
def delete_order():
    data = json.loads(request.form['data'])
    order_id = data['orderId']
    if (delete_order_db(order_id)):
        return jsonify({'success': True, 'msg': '订单删除成功'})
    else:
        return jsonify({'success': False, 'msg': '订单删除失败'})


@app.route('/order/getinfo', methods=['POST'])
def get_order_info():
    # userid = request.form.get("userid")
    data = json.loads(request.form['data'])
    tag = data['tag']
    books = get_order_info_db(tag)
    return jsonify({"data": books})


# 做出评论，用户端接口
@app.route('/makecomment', methods=['POST'])
def create_review():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    rating = data['rating']
    comment = data['comment']
    if (make_comment_db(user_id, book_id, comment, rating)):
        return jsonify({'msg': 'review created successfully!'})
    else:
        return jsonify({'msg': 'Failed to insert data into database'})


# 更新用户信息，管理端接口
@app.route('/user/updateInfo', methods=['POST'])
def update_user_info():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    username = data['username']
    password = data['password']
    avatar = data['avatar']
    address = data['address']
    print(password)
    if (update_user_info_db(username, password, avatar, address, user_id)):
        result = {'code': 1, 'message': 'success'}
        return jsonify(result)
    else:
        result = {'code': 0, 'message': 'error', 'error_message': 'error'}
        return jsonify(result)

@app.route('/user/add', methods=['POST'])
def add_user():
    # 从请求中获取参数
    data = json.loads(request.form['data'])
    username = data['username']
    password = data['password']
    avatar = data['avatar']
    address = data['address']
    if(add_user_db(avatar,username,password,address)):
        # 返回响应
        response = {'message': 'User added successfully'}
        return jsonify(response), 200
    else:
        response = {'message': 'User added error'}
        return jsonify(response), 201

@app.route('/user/uInfoModify', methods=['POST'])
def user_info_modify():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    username = data['nickName']
    password = data['password']
    avatar = data['avatar']
    address = data['address']
    phone = data['phone']
    gender = data['gender']
    if (user_info_modify_db(username, password, avatar, address, phone, gender, user_id)):
        result = {'code': 1, 'message': 'success'}
        return jsonify(result)
    else:
        result = {'code': 0, 'message': 'error', 'error_message': 'error'}
        return jsonify(result)


# 删除用户，管理端接口
@app.route('/user/delete', methods=['POST'])
def delete_user():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    if (delete_user_db(user_id)):
        result = {'code': 1, 'message': '用户删除成功'}
        return jsonify(result)
    else:
        result = {'code': 0, 'message': '用户删除失败', 'error_message': 'error'}
        return jsonify(result)


# 修改图书，管理端接口
@app.route("/book/modify", methods=["POST"])
def modify_book():
    data = json.loads(request.form['data'])
    book_id = data['book_id']
    book_cover = data['bookCover']
    book_title = data['title']
    book_author = data['author']
    book_price = data['price']
    book_description = data['description']
    book_language = data['language']
    # book_id = request.form.get("book_id")
    # book_cover = request.form.get("book_cover")
    # book_title = request.form.get("book_title")
    # book_author = request.form.get("book_author")
    # book_price = request.form.get("book_price")
    # book_description = request.form.get("book_description")
    # book_language = request.form.get("book_language")
    result = modify_book_db(book_id, book_cover, book_title, book_author, book_price, book_description, book_language)
    print(result)
    if (result == 1):
        return jsonify({"code": 1, "msg": "修改成功"})
    elif (result == 2):
        print("添加图书，执行训练")
        #model_train_fun()
        return jsonify({"code": 1, "msg": "添加成功"})
    else:
        return jsonify({"code": 0, "msg": "发生错误"})


# 删除图书，管理员接口
@app.route("/book/delete", methods=["POST"])
def delete_book():
    data = json.loads(request.form['data'])
    book_id = data['book_id']
    if (delete_book_db(book_id)):
        result = {'code': 1, 'message': '图书删除成功'}
        print("删除图书，执行训练")
        #model_train_fun()
        return jsonify(result)
    else:
        result = {'code': 0, 'message': '图书删除失败', 'error_message': 'error'}
        return jsonify(result)


@app.route("/cart/getInfo", methods=["POST"])
def cart_get_info():
    data = json.loads(request.form['data'])
    uid = data['user_id']
    data = get_cart_list(uid)
    return jsonify({'code': 200, 'orderData': data})


# 添加至购物车接口
@app.route('/cart/add', methods=['POST'])
def add_to_cart():
    data = json.loads(request.form['data'])
    uid = data['uid']
    book_id = data['book_id']
    book_cover = data['bookCover']
    book_title = data['title']
    book_author = data['author']
    book_price = data['price']
    count = data['count']
    if (add_to_cart_db(book_id, book_cover, book_title, book_price, count, book_author, uid)):
        return jsonify({'code': 200, 'msg': '添加成功，已在购物车等待！'})
    else:
        return jsonify({'status': 'error'})


@app.route('/cart/settle', methods=['POST'])
def cart_settle():
    data = json.loads(request.form['data'])
    tag = data['tag']
    paycheck=data['paycheck']
    product_list = data['products']
    products = json.loads(product_list)
    print(products)
    if (settle_cart_db(tag, products,paycheck)):
        # 返回响应
        response = {'msg': '支付成功'}
        return jsonify(response), 200


@app.route('/cart/delete', methods=['POST'])
def cart_delete():
    data = json.loads(request.form['data'])
    product_list = data['products']
    products = json.loads(product_list)
    if (delete_cart_items_by_uid_and_bookid(products)):
        # 返回响应
        response = {'msg': '删除成功'}
        return jsonify(response), 200


if __name__ == '__main__':
    #app.run(host='0.0.0.0', port=5000,debug=True)
    app.run(debug=True)
