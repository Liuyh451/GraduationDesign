from flask import Flask, request, jsonify
from flask_cors import CORS
import os
from database_related import *

app = Flask(__name__)
os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'
CORS(app)  # 解决跨域请求问题

user_db = []

@app.route('/login', methods=['POST'])

def login():
    # 获取用户名和密码
    username = request.form.get('username')
    password = request.form.get('password')
    print(username,password)
    # 根据用户名和密码判断登录是否成功
    if login_db(username,password):
        print("ok")
        return jsonify(code=200, message='登录成功')
    else:
        print("no")
        return jsonify(code=201, message='用户名或密码错误')

@app.route('/register', methods=['POST'])
def register():
    username = request.form.get('username')
    password = request.form.get('password')
    # 判断用户名和密码是否为空
    print(username, password)
    if not (username and password):
        return jsonify({'message': 'Invalid username or password'}), 400
    # 校验用户是否已经存在
    if(register_db(username,password)):
        return jsonify({'code':200,'message': 'User registered successfully'})
    else:
        return jsonify({'code':201,'message': 'Error: User name already exists'})
@app.route('/getRecomBooks', methods=['GET'])
def getRecomBooks():

    # 获取前端传递的用户ID

    #todo 写一个根据用户ID获取推荐列表的函数（直接调用之前的recommendbook即可）这里为了测试方便，直接拿来用了
    book_id_list = [5928, 4698, 4765, 3432, 2441, 5176, 8038, 9334, 2659, 2105, 104, 7384, 5067, 2206, 6784, 8166, 1534, 560, 4734, 8639, 6945, 513, 1387, 1921, 9547, 5764, 6929, 802, 51, 4365]
    # 调用A函数并获取JSON数据和状态码
    data=searchBooks(book_id_list)
    page_size = 6
    init_page = 1

    # 返回JSON数据和状态码给前端
    return jsonify({'code':200,"data":data})
cnt = 0
@app.route('/api/novels')
def get_novels():
    # 获取前端传来的查询参数 "page"，默认值为 1
    global cnt
    cnt+=1
    page =cnt
    #todo 这个地方是个bug需要改的
    if(cnt==7):
        cnt=0
    book_id_list = [5928, 4698, 4765, 3432, 2441, 5176, 8038, 9334, 2659, 2105, 104, 7384, 5067, 2206, 6784, 8166, 1534,
                    560, 4734, 8639, 6945, 513, 1387, 1921, 9547, 5764, 6929, 802, 51, 4365,5928, 4698]
    # 调用A函数并获取JSON数据和状态码
    # 计算每页的起始和结束索引
    page_size = 4  # 每页显示的小说数
    start_index = (page - 1) * page_size
    end_index = start_index + page_size

    # 获取本页的小说，并将它们转化为 JSON 格式返回给前端
    book_id_list= book_id_list[start_index:end_index]
    data = searchBooks(book_id_list)
    print(len(book_id_list))
    print("第几页",str(page))
    print(book_id_list)
    return  jsonify({'code':200,"data":data})
@app.route('/api/topBooks', methods=['GET'])
def get_random_highest_ratings():
    return search_top_books()


if __name__ == '__main__':
    app.run()
