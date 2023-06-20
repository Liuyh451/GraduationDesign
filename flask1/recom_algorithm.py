import numpy as np
from model_train import *
from database_related import get_ratings_from_db
from database_related import save_user_books


def recom_fun(user_id):
    # 准备数据：用户-物品评分矩阵
    ratings_matrix = get_ratings_from_db()
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

    num_recommendations = 10
    """
    @param model 训练好的模型
    @param user_id 需更新的用户ID
    @param book_id 需更新的书籍ID
    @param num_recommendations=3 推荐的书的数量
    @return   推荐的书
    """

    def recommend_books(model, user_id, num_recommendations):
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

    recommended_books = recommend_books(
        loaded_model, user_id, num_recommendations)
    # print(f'Recommended books for user 0: {recommended_books}')
    print('Recommended books_id for user {}'.format(user_id+1),
          get_books_id(recommended_books, ratings_matrix))
    save_user_books(user_id + 1, get_books_id(recommended_books, ratings_matrix))

