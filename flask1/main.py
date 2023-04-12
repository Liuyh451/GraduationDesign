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


if __name__ == '__main__':
    app.run()
