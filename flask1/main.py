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
    k,id=login_db(username, password)
    if k == 1:
        print("ok")
        return jsonify(code=200, message='notAdmin',uid=id)
    elif k == 2:
        return jsonify(code=200, message='isAdmin',uid=id)
    else:
        print("no")
        return jsonify(code=201, message='用户名或密码错误')

#用户注册，用户和管理员接口
#todo 管理员注册时，认证密钥
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
    uid=int(uid)
    if (flag):
        return jsonify({'code': 200, 'uid': uid})
    else:
        return jsonify({'code': 201, 'message': 'Error: User name already exists'})

# 为用户推荐书籍，用户端接口
@app.route('/getRecomBooks', methods=['POST'])
def getRecomBooks():
    # 获取前端传递的用户ID
    #uid = request.form.get('uid')#用于postman测试
    data = json.loads(request.form['data'])
    uid = data['uid']
    print("user_id",uid)
    book_data=get_recom_books_for_user(uid)
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
    # todo 这个地方是个bug需要改的
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
    json_result=get_all_users_db()
    return jsonify({'users': json_result})


# 获取全部图书，管理端接口
@app.route('/getAllBooks', methods=['GET'])
def get_all_books():
    books = get_all_Books_db()
    #todo 这里有个小bug，有时候前端接收不到，也可能是前端的问题
    return jsonify({'books': books})


# 获取全部评论，管理端接口
@app.route('/allReviews', methods=['GET'])
def get_all_reviews():
    review_list = get_all_reviews_db()
    return jsonify({'reviews': review_list})
# 获取全部订单，管理端接口
@app.route('/allOrders', methods=['GET'])
def get_all_orders():
    json_result = get_all_orders_db()
    return jsonify({'orders': json_result})

#获取该书籍的全部评论，用户端接口
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

# 保存用户对书籍的评分，必要时更新模型，用户端接口
@app.route("/rating_and_recom", methods=["POST"])
def rating_and_recom_fun():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    rating = data['rating']
    print(user_id,book_id,rating)
    # 立即返回状态码 200
    if(change_user_ratings(user_id,book_id,rating)):
        calculation_thread = threading.Thread(target=model_train_fun)
        calculation_thread.start()
    # 开启新线程并执行计算任务
    else:

        if(get_user_modify(user_id)):
            user_id=int(user_id)

            calculation_thread = threading.Thread(target=recom_fun(user_id-1))
            calculation_thread.start()
    return "OK", 200
@app.route("/bookrating", methods=["POST"])
def get_user_to_book_rating():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    print(user_id,book_id)
    # book_id = request.form.get('book_id')
    # user_id = request.form.get('user_id')
    result=get_user_to_book_rating_db(user_id,book_id)
    if result:
        return jsonify({"ratings": result[0]})
    else:
        return jsonify({"ratings": "0"})
@app.route("/getUserInfo", methods=["POST"])
def get_user_info():
    data = json.loads(request.form['data'])
    user_id = data['user_id']

    # user_id = request.form.get('user_id')
    print(data)
    result=getUserInfo_db(user_id)
    return jsonify({"userInfo": result})
@app.route('/bookSearch',methods=["POST"])
def book_search():
    title = request.form.get('title')
    print(title)
    # title = request.args.get('title') # 获取查询参数
    books = fuzzy_search_book(title)
    return jsonify(books)
# 下单接口，用户端接口
@app.route('/createOrder', methods=['POST'])
def create_order():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    title = data['title']
    author = data['author']
    book_cover = data['book_cover']
    price = data['price']
    quantity = data['quantity']
    address = data['address']
    phone = data['phone']
    # user_id = request.form.get('userid')
    # book_id = request.form.get('bookid')
    # title = request.form.get('title')
    # author = request.form.get('author')
    # book_cover = request.form.get('book_cover')
    # price = request.form.get('price')
    # quantity = request.form.get('quantity')
    # address = request.form.get('address')
    # phone = request.form.get('phone')

    # 计算总价
    total_price = float(price) * float(quantity)
    if(place_an_order(user_id, book_id, title, author, book_cover, price, quantity, total_price, address, phone)):
        return jsonify({'success': 'Order created successfully!'})
    else:
        return jsonify({'error': 'Failed to insert data into database'})
# 做出评论，用户端接口
@app.route('/makecomment', methods=['POST'])
def create_review():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    book_id = data['book_id']
    rating = data['rating']
    comment = data['comment']
    if(make_comment_db(user_id,book_id,comment,rating)):
        return jsonify({'msg': 'review created successfully!'})
    else:
        return jsonify({'msg': 'Failed to insert data into database'})
# 更新用户信息，管理端接口
@app.route('/update_user_info', methods=['POST'])
def update_user_info():
    data = json.loads(request.form['data'])
    user_id = data['user_id']
    username = data['username']
    password = data['password']
    avatar = data['avatar']
    address = data['address']
    print(password)
    if(address!=None):
        print(address)
    if(update_user_info_db(username,password,avatar,address,user_id)):
        result = {'code': 1, 'message': 'success'}
        return jsonify(result)
    else:
        result = {'code': 0, 'message': 'error', 'error_message': 'error'}
        return jsonify(result)
@app.route("/modify_book", methods=["POST"])
def modify_book():
    data = json.loads(request.form['data'])
    book_id=data['book_id']
    book_cover = data['bookCover']
    book_title=data['title']
    book_author=data['author']
    book_price=data['price']
    book_description=data['description']
    book_language=data['language']
    # book_id = request.form.get("book_id")
    # book_cover = request.form.get("book_cover")
    # book_title = request.form.get("book_title")
    # book_author = request.form.get("book_author")
    # book_price = request.form.get("book_price")
    # book_description = request.form.get("book_description")
    # book_language = request.form.get("book_language")
    result=modify_book_db(book_id,book_cover,book_title,book_author,book_price,book_description,book_language)
    print(result)
    if(result==1):
        return jsonify({"code": 1, "msg": "修改成功"})
    elif(result==2):
        #todo 加入书籍后需要进行训练
        return jsonify({"code": 1, "msg": "添加成功"})
    else:
        return jsonify({"code": 0, "msg": "发生错误"})




if __name__ == '__main__':
    app.run()
