import numpy as np
import pandas as pd
from model_train import *
from sklearn.model_selection import train_test_split

ratings = pd.read_csv('ratings.csv')
# 删除重复的记录
is_duplicated = ratings.duplicated(subset=['user_id', 'book_id'])
ratings = ratings[~is_duplicated]
# 将评分数据转化为矩阵
ratings_matrix = ratings.pivot(
    index='user_id',
    columns='book_id',
    values='rating'
).fillna(0)
ratings = np.array(ratings_matrix)
train_data, test_data = train_test_split(ratings, test_size=0.2, random_state=42)
latent_factors = 3
num_users, num_items = train_data.shape
# 创建一个与训练过的模型具有相同结构的新模型实例
loaded_model = MatrixFactorization(num_users, num_items, latent_factors)
# 创建模型变量
_ = loaded_model(np.array([[0], [0]]))
# 加载保存的权重
loaded_model.load_weights('matrix_factorization_model_weights.h5')
# 加载模型
# 为用户0推荐3本书
user_id = 0
num_recommendations = 3
"""
@param model 训练好的模型
@param user_id 需更新的用户ID
@param book_id 需更新的书籍ID
@param num_recommendations=3 推荐的书的数量
@return   推荐的书
"""


def recommend_books(model, user_id, num_recommendations=30):
    user_ratings = ratings[user_id]
    unrated_books = np.where(user_ratings == 0)[0]
    predicted_ratings = model(
        [np.array([user_id] * len(unrated_books)), unrated_books])
    top_book_indices = np.argsort(-predicted_ratings)[:num_recommendations]
    return unrated_books[top_book_indices]


# 获取书的iD


def get_books_id(books_index, ratings_matrix):
    col_names = ratings_matrix.columns.tolist()
    recommend_books_list = []
    for i in books_index:
        col_key = col_names[i]
        recommend_books_list.append(col_key)
    return recommend_books_list


# 更新推荐


def recommend_books_update(model, user_id, num_recommendations=30):
    user_ratings = update_ratings[user_id]
    unrated_books = np.where(user_ratings == 0)[0]
    predicted_ratings = model(
        [np.array([user_id] * len(unrated_books)), unrated_books])
    top_book_indices = np.argsort(-predicted_ratings)[:num_recommendations]
    return unrated_books[top_book_indices]


# 给出推荐
recommended_books = recommend_books(
    loaded_model, user_id=6, num_recommendations=30)
print(f'Recommended books for user 0: {recommended_books}')
print('Recommended books_id for user 0',
      get_books_id(recommended_books, ratings_matrix))

"""
@param ratings 原始用户-评分矩阵
@param user_id 需更新的用户ID
@param book_id 需更新的书籍ID
@param new_rating 需更新的评分
@return   更新后的用户-评分矩阵
"""
update_ratings = ratings


def update_ratings_matrix(ratings, user_id, book_id, new_rating):
    updated_ratings = ratings.copy()
    updated_ratings[user_id, book_id] = new_rating
    return updated_ratings


# 假设用户0给书2一个新评分4
user_id = 6
book_id = 2
new_rating = 4
# 更新评分矩阵
update_ratings = update_ratings_matrix(
    update_ratings, user_id, book_id, new_rating)
# 为用户0更新推荐3本书
recommended_books = recommend_books_update(
    loaded_model, user_id, num_recommendations=30)
print(f'Recommended books for user 0: {recommended_books}')
print('Recommended books_id for user 0',
      get_books_id(recommended_books, ratings_matrix))
