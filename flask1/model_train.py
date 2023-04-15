import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split

# 准备数据：用户-物品评分矩阵
# 评分范围为1-5，0表示用户没有评分（未读）nrows 读取csv文件的前n行
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
# 隐向量维度
latent_factors = 3

# 将数据分为训练集和测试集
train_data, test_data = train_test_split(
    ratings, test_size=0.2, random_state=42)


# 定义模型


class MatrixFactorization(tf.keras.Model):
    def __init__(self, num_users, num_items, latent_dim):
        super(MatrixFactorization, self).__init__()
        self.user_embeddings = tf.keras.layers.Embedding(num_users, latent_dim)
        self.item_embeddings = tf.keras.layers.Embedding(num_items, latent_dim)

    def call(self, inputs):
        user_ids, item_ids = inputs
        user_embedding = self.user_embeddings(user_ids)
        item_embedding = self.item_embeddings(item_ids)
        return tf.reduce_sum(user_embedding * item_embedding, axis=1)


# 训练模型


def train(model, train_data, epochs=100, batch_size=2):
    optimizer = tf.keras.optimizers.Adam(learning_rate=0.01)
    loss_fn = tf.keras.losses.MeanSquaredError()
    num_samples = train_data.shape[0] * train_data.shape[1]
    user_ids, item_ids = np.where(train_data > 0)
    ratings = train_data[user_ids, item_ids]

    for epoch in range(epochs):
        shuffled_indices = np.random.permutation(len(ratings))
        user_ids_shuffled = user_ids[shuffled_indices]
        item_ids_shuffled = item_ids[shuffled_indices]
        ratings_shuffled = ratings
        for i in range(0, len(ratings), batch_size):
            batch_user_ids = user_ids_shuffled[i:i + batch_size]
            batch_item_ids = item_ids_shuffled[i:i + batch_size]
            batch_ratings = ratings_shuffled[i:i + batch_size]

        with tf.GradientTape() as tape:
            predictions = model([batch_user_ids, batch_item_ids])
            loss = loss_fn(batch_ratings, predictions)

        gradients = tape.gradient(loss, model.trainable_variables)
        optimizer.apply_gradients(zip(gradients, model.trainable_variables))

    if (epoch + 1) % 10 == 0:
        print(f'Epoch {epoch + 1}/{epochs}, Loss: {loss.numpy()}')


# 评估模型


def evaluate(model, test_data):
    user_ids, item_ids = np.where(test_data > 0)
    ratings = test_data[user_ids, item_ids]
    predictions = model([user_ids, item_ids])
    mse = tf.keras.losses.MeanSquaredError()(ratings, predictions)
    return mse.numpy()


# 实例化模型并训练
num_users, num_items = train_data.shape
model = MatrixFactorization(num_users, num_items, latent_factors)
train(model, train_data, epochs=100)
model.save_weights('matrix_factorization_model_weights.h5')
# 评估模型
mse = evaluate(model, test_data)
print(f'Test MSE: {mse}')
