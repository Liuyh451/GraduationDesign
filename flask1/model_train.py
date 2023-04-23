import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split
from database_related import get_ratings_from_db


# 矩阵分解该类继承了 tensorflow 的 Model 基类，并在构造函数中定义了两个 Embedding 层（即 user_embeddings 和 item_embeddings），
# 分别用于表示用户和物品，维度大小是 latent_dim。
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
    # 定义Adam 优化器，并设置学习率为 0.01
    optimizer = tf.keras.optimizers.Adam(learning_rate=0.01)
    # 定义均方误差损失函数
    loss_fn = tf.keras.losses.MeanSquaredError()
    # 计算了训练集中总共有多少个样本，即用户数乘以物品数
    num_samples = train_data.shape[0] * train_data.shape[1]
    # 代码使用NumPy库中的where函数找到所有训练集中的非零元素所对应的行和列索引，也就是需要进行模型训练的数据点。
    # 其中train_data是一个稀疏矩阵，train_data > 0，会将稀疏矩阵中的非零元素进行标记，
    # 返回一个bool类型的矩阵，对这个bool类型的矩阵进行where操作就能返回非零元素的索引。
    # 这里的user_ids和item_ids的值都是一维的，长度为非零元素的个数。
    user_ids, item_ids = np.where(train_data > 0)
    # 从训练集中按照用户和物品的索引将相应的评分值提取出来，形成一个新的一维数组 ratings
    # 其中 ratings[i] 对应 user_ids[i] 和 item_ids[i] 在原矩阵 train_data 中的值。
    # 这个 ratings 数组将被用于计算模型的损失函数。
    ratings = train_data[user_ids, item_ids]

    for epoch in range(epochs):
        # 随机批量梯度下降
        shuffled_indices = np.random.permutation(len(ratings))
        # 生成一个随机索引的数组，其中长度为训练数据的样本数量，并将生成的随机索引数组赋给 shuffled_indices
        user_ids_shuffled = user_ids[shuffled_indices]
        # 然后使用这个随机索引数组对用户、物品和评分的数组进行洗牌。
        # 这个过程的目的是确保每次批量梯度下降使用的数据点顺序都是不一样的
        item_ids_shuffled = item_ids[shuffled_indices]
        ratings_shuffled = ratings
        for i in range(0, len(ratings), batch_size):
            batch_user_ids = user_ids_shuffled[i:i + batch_size]
            batch_item_ids = item_ids_shuffled[i:i + batch_size]
            batch_ratings = ratings_shuffled[i:i + batch_size]
        # 使用 TensorFlow GradientTape 对模型进行前向传播和反向传播，计算损失函数的梯度，并使用优化器对模型的参数进行梯度下降。
        # 首先通过调用 model 函数，输入批量数据，得到模型对用户和物品的预测评分。然后计算批量数据中实际评分和模型预测值之间的均方误差。
        # tape.gradient 函数计算出了模型参数对损失函数的梯度，再调用优化器上的 apply_gradients 方法，更新神经网络中的权重和偏置，从而降低损失函数的值。
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


def model_train_fun():
    # 准备数据：用户-物品评分矩阵
    ratings_matrix = get_ratings_from_db()
    ratings = np.array(ratings_matrix)
    # 保存预处理后的评分矩阵为.npy文件
    # np.save('processed_ratings.npy', ratings)

    # 隐向量维度
    latent_factors = 3

    # 将数据分为训练集和测试集
    train_data, test_data = train_test_split(
        ratings, test_size=0.2, random_state=42)
    # 实例化模型并训练
    num_users, num_items = train_data.shape
    model = MatrixFactorization(num_users, num_items, latent_factors)
    train(model, train_data, epochs=100)
    model.save_weights('matrix_factorization_model_weights.h5')
