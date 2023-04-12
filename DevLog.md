# 基于Android的电子书智能推荐系统

## 1.Android前端

### 1.1注册登录模块

###### Todo

- [ ] 找UI组件库，GitHub开源的
- [ ] 学习Android的网络请求，注意跨域问题
- [x] 测试注册模块
- [x] 需要获取json中的code，用响应code不行
- [x] 需要解决ui消失的问题 ~~曲线解决~~

#### 1.1.1 遇到的问题以及解决方案

##### 1.1.1.1  没有找到python package

解决方案：“继承全局site-packages”选项是在使用virtualenv创建Python虚拟环境安装过程中可以选择的一个选项。当选择了这个选项之后，虚拟环境将会继承您系统上全局安装的所有包。

默认情况下，使用virtualenv创建一个虚拟环境时，它会创建一个完全隔绝的环境，并安装自己所需的一组软件包。当您希望确保项目所需的特定软件包版本已安装并避免与系统上的其他软件包冲突时，这会非常有用。

但是，在某些情况下，您可能还希望在虚拟环境中使用全局安装在系统上的软件包。这就是“继承全局site-packages”选项的作用。

当选择了这个选项后，您的虚拟环境仍将安装自己的一组软件包，但也将包括在您系统上全局安装的任何软件包。这可以节省磁盘空间，并使使用全局安装的软件包变得更加容易。

 pycharm的网络被墙了，在尝试换源无效后，我选择使用这个这个。

##### 1.1.1.2 Cleartext HTTP traffic to 127.0.0.1 not permitted

修改 AndroidManifest.xml 文件，并添加以下内容，以允许应用程序使用不安全的 HTTP

```xml
<application
   ...
   android:usesCleartextTraffic="true">
   ...
</application>
```

##### 1.1.1.3 W/System.err: java.net.SocketException: socket failed: EPERM (Operation not permitted)

原因：解决方案:这个错误可能是由于您的应用程序的网络安全配置导致的。最近的 Android 平台版本 (Android 9 及更高版本) 引入了网络安全增强功能，会限制应用程序在未加密的连接上进行网络请求。因此在应用程序中进行网络请求时，如果请求的地址配置为未加密的地址 (如http://)，则会出现 "socket failed error"。

a.在res文件夹中创建一个 xml 文件夹，然后在该文件夹中创建一个网络安全配置文件 network_security_config.xm

```xml
res/
   xml/
      network_security_config.xml

```

b.在 `network_security_config.xml` 中指定域名并配置网络安全性级别。例如，以下代码段显示了一个配置文件，指定了具有高网络安全性级别的域名：

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
   <domain-config>
       <domain includeSubdomains="true">yourdomain.com</domain>
       <trust-anchors>
           <certificates src="@raw/yourdomain" />
       </trust-anchors>
   </domain-config>
</network-security-config>

```

在这个配置文件中，我们指定了一个具有高网络安全性级别的域名，即“[yourdomain.com](http://yourdomain.com/)”。我们通过 `domain` 元素来指定要包含的域名，并使用 `trust-anchors` 元素指定证书的安全性。如果需要更多的域名，则可以使用多个 `domain` 元素。请注意，需要将证书文件放在 `res/raw` 文件夹中

c.更新Manifest文件，在 `application` 元素下添加 `android:networkSecurityConfig` 属性，并指向 `network_security_config.xml` 文件。

```xml
<application
   android:icon="@mipmap/ic_launcher"
   android:label="@string/app_name"
   android:networkSecurityConfig="@xml/network_security_config"
   ...
</application>
```

##### 1.1.1.4android.os.NetworkOnMainThreadException

原因：Android.os.NetworkOnMainThreadException 是由于应用程序在主线程上执行了网络操作而引起的异常。在 Android 上，网络操作（例如HTTP请求和Socket连接等）应该放在后台线程（例如AsyncTask或Runnable）中进行。这是因为在主线程上执行网络操作可能会导致UI线程阻塞，并使应用程序出现ANR（应用程序未响应）错误。

解决方案：

a.将网络操作放在后台线程中：使用AsyncTask或Runnable将网络操作移至后台线程

```java
new Thread(new Runnable() {
    public void run() {
        // 在此处执行网络操作
    }
}).start();
```

b.使用Handler：如果您需要将网络操作的结果更新到UI界面上，则可以使用Handler。您可以在后台线程中执行网络操作，然后将结果通过Handler发送到UI线程。

```java
private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        // 在此处更新UI
    }
};

new Thread(new Runnable() {
    public void run() {
        // 在此处执行网络操作
        mHandler.sendEmptyMessage(0);
    }
}).start();
```

最终解决方案：

在主线程中加入handler，利用handler机制传递函数

```java
private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean result = (Boolean) msg.obj;

                Toast.makeText(MainActivity.this, result ? "登录成功" : "用户名或密码错误", Toast.LENGTH_SHORT).show();
            // 通过 msg.arg1, msg.arg2, msg.obj 等获取子线程数据，然后在主线程中处理
            // 如果用户名和密码均输入正确，登录到主界面
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
    };
```

在子线程中执行网络请求，并传递信息

```java
new Thread(new Runnable() {
                        public void run() {
                            Log.d("TAG",username);
                            Log.d("TAG",password);
                            String loginUrl = "http://10.0.2.2:5000/login";
                            boolean loginResult = login_post.sendLoginRequest(loginUrl, username, password);
                            Log.d("TAG",loginUrl);
                            Message msg = new Message();
                            msg.obj=loginResult;
                            mHandler.sendMessage(msg);
                            // 在此处执行网络操作
                        }
                    }).start();
```

##### 1.1.1.5 W/System.err: java.net.ConnectException: Failed to connect to /127.0.0.1:5000

原因：在[Android](http://lib.csdn.net/base/android)开发中通过localhost或127.0.0.1访问本地服务器时，会报[Java](http://lib.csdn.net/base/javase)[.NET](http://lib.csdn.net/base/dotnet).ConnectException: localhost/127.0.0.1:8083 -Connection refused异常。因为Android模拟器本身把自己当做了localhost或127.0.0.1，而此时我们又通过localhost或127.0.0.1访问本地服务器，所以会抛出异常了。

解决方案：

```
在模拟器上可以用10.0.2.2代替127.0.0.1和localhost；
```

##### 1.1.1.6 无法获取json字符串中的数据

解决方案：将json字符串转为JsonObj

```java
//将json字符串转为JsonObj，方便读取返回值
            JSONObject person = new JSONObject(response.toString());
            String respCode = person.getString("code");
```

#### 1.1.2 待思考和解决的问题

```java
int responseCode = connection.getResponseCode();//responseCode:200,201....
            String numString = Integer.toString(responseCode);
            Log.d("TAG",numString);
            StringBuilder response = new StringBuilder();
            String line;
            if (responseCode == HttpURLConnection.HTTP_OK) {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            String str = response.toString();
            Log.d("TAG","res"+str);//{"code":200,"msg":"example"}
```

上面的str应该为字符串，但是这里明显是个对象，不知道如何读出里面的数据

### 1.2 推荐模块

初步想法是做一个listview，在用户数据少的时候直接进行随机推荐，利用fragment机制进行跳转

### 1.3 用户的评分，收藏等拓展模块

### 1.4 用户个人中心模块

### 1.5 不要更新Gradle

网络被墙，会失败，会导致无法运行程序

gradle-wrapper.properties文件下：

```json
distributionUrl=https\://services.gradle.org/distributions/gradle-7.0.2-bin.zip
```

目前版本号

位于build.gradle(Module:app)

```json
dependencies {
    classpath 'com.android.tools.build:gradle:7.0.3'
}
```

## 2.Flask后端

### 2.1注册登录模块

使用pyflask进行简单的验证

###### Todo

- [ ] 学习flask验证，并测试API
- [ ] 冷启动问题，直接随机推荐任意的，待用户喜欢的数据达到一定数量的时候，再进行推荐

### 2.2电子书推荐模块

使用sklearn进行算法推荐，只有用户标记了喜欢后，才进行推荐。如果仅仅浏览后就做出推荐，系统负载过大，不利于使用。当用户给出喜欢（favorite）后，在用户数据库增加响应的字段，然后更新模型。否则直接调用模型即可.

###### todo

- [x] 学习python和mysql的交互
- [x] 查找数据集，并进行数据清洗
- [ ] ~~学习如何从mysql中获取数据，并交由sklearn进行处理~~
- [x] 思考是否能只增加更新后那一个数据，而不必全部重新进行训练。
- [ ] [基于用户的协同过滤推荐算法](https://www.bilibili.com/video/BV1wA41197hb)
- [ ] [Apache Mahout初体验](https://blog.csdn.net/Jason_Nevermind/article/details/123982764 )
- [x]  [基于tensorflow的个性化电影推荐系统实战](https://blog.csdn.net/weixin_62075168/article/details/128431395)
- [ ] 考虑保存模型和加载模型，若无法加载模型，就推荐100个每次展示10个，曲线救国
- [ ] 获得书的ID而不是下标

初步设计思路如下：

#### 2.2.1 基于Apache Mahout的推荐算法

```java
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.List;

public class BookRecommender {
    public static void main(String[] args) throws Exception {
        // 加载用户评分数据
        DataModel model = new FileDataModel(new File("/path/to/your/data.csv"));

        // 计算相似度
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

        // 构建邻居
        UserNeighborhood neighborhood =
                new ThresholdUserNeighborhood(0.1, similarity, model);

        // 构建推荐引擎
        UserBasedRecommender recommender =
                new GenericUserBasedRecommender(model, neighborhood, similarity);

        // 获得用户ID，以及需要生成前n个推荐的书籍数量为10
        long userID = 1;
        int n = 10;

        // 生成推荐列表
        List<RecommendedItem> recommendations =
                recommender.recommend(userID, n);

        // 打印推荐列表
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }
}

```

#### 2.2.2 基于tensorflow的推荐算法（目前使用）

数据如下：

| **User ID** | Book ID | **Rating** |
| :---------: | :-----: | :--------: |
|      1      |    1    |     4      |
|      1      |    2    |     3      |
|      2      |    1    |     1      |
|     ...     |   ...   |    ...     |

<div align="center">用户-书籍评分数据表（表名为ratings）
    </div>

| Book ID | **Title**             | **Author**          | ...  |
| ------- | --------------------- | ------------------- | ---- |
| 1       | The Great Gatsby      | F. Scott Fitzgerald | ...  |
| 2       | To Kill a Mockingbird | Harper Lee          | ...  |
| 3       | 1984                  | George Orwell       | ...  |
| ...     | ...                   | ...                 |      |

<div align="center">书籍元数据表（表名为books）
    </div>
使用 TensorFlow 实现用户-物品协同过滤推荐算法的过程，使用了基于矩阵分解的协同过滤算法。步骤如下（伪代码）：

1. 准备数据：从 CSV 文件中读取用户-物品评分矩阵，评分范围为 1 到 5，0 表示用户没有评分（未读）。

   ```python
   import pandas as pd
   
   ratings_df = pd.read_csv('ratings.csv').drop_duplicates(['user_id', 'item_id'])
   ratings_matrix = ratings_df.pivot(index='user_id', columns='item_id', values='rating').fillna(0)
   ```

2. 删除重复的记录，并将评分数据转换为矩阵。

   ```python
   is_duplicated = ratings.duplicated(subset=['user_id', 'book_id'])
   ratings = ratings[~is_duplicated]
   ratings = np.array(ratings_matrix)
   ```

3. 定义模型：使用神经网络实现协同过滤，该模型由用户和物品的嵌入向量乘积组成。

   ```python
   import tensorflow as tf
   
   class CFModel(tf.keras.Model):
       def __init__(self, num_users, num_items, embedding_size):
           super(CFModel, self).__init__()
           self.user_embedding = tf.keras.layers.Embedding(...)
           self.item_embedding = tf.keras.layers.Embedding(...)
           self.predict_layer = tf.keras.layers.Dense(...)
   
       def call(self, inputs):
           user_vector = self.user_embedding(inputs[:, 0])
           item_vector = self.item_embedding(inputs[:, 1])
           dot_product = tf.reduce_sum(tf.multiply(user_vector, item_vector), axis=1)
           output = self.predict_layer(dot_product)
           return output
   
   ```

4. 训练模型：使用训练数据进行梯度下降训练，并将损失做为训练过程的指标。

   ```python
   model = CFModel(num_users, num_items, embedding_size)
   optimizer = tf.keras.optimizers.Adam(...)
   mse_loss = tf.keras.losses.MeanSquaredError()
   
   for epoch in range(num_epochs):
     for batch in batches:
       with tf.GradientTape() as tape:
           user_item_pairs = tf.gather(ratings_matrix, batch)
           predictions = model(user_item_pairs)
           loss = mse_loss(true_ratings, predictions)
       gradients = tape.gradient(loss, model.trainable_variables)
       optimizer.apply_gradients(zip(gradients, model.trainable_variables))
   
   ```
   
5. 评估模型：使用测试数据计算均方误差（MSE），用于评估模型的性能。

   ```python
   test_df = pd.read_csv('test.csv')
   test_matrix = test_df.pivot(index='user_id', columns='item_id', values='rating').fillna(0)
   user_item_pairs = tf.gather(test_matrix, batch)
   predictions = model(user_item_pairs)
   mse = mse_loss(true_ratings, predictions)
   ```

6. 使用模型进行推荐：为指定的用户推荐指定数量的未读过物品。

   ```python
   user_id = ...
   unseen_items = ...
   scores = {}
   for item_id in unseen_items:
       prediction = model(tf.constant([[user_id, item_id]]))
       scores[item_id] = prediction.numpy()[0]
   recommended_items = sorted(scores, key=scores.get, reverse=True)[:num_recommendations]
   ```

7. 更新评分矩阵：更新给定用户和给定物品之间的评分。

   ```python
   update_ratings=ratings
   def update_ratings_matrix(ratings, user_id, book_id, new_rating):
       updated_ratings = ratings.copy()
       updated_ratings[user_id, book_id] = new_rating
       return updated_ratings
   ```

8. 更新推荐：使用更新的评分矩阵，为指定的用户更新推荐物品。

   ```python
   for user_id in updated_users:
       unseen_items = ...
       scores = {}
       for item_id in unseen_items:
           prediction = model(tf.constant([[user_id, item_id]]))
           scores[item_id] = prediction.numpy()[0]
       updated_recommendations[user_id] = sorted(scores, key=scores.get, reverse=True)[:num_recommendations]
   
   ```

其中，步骤 1 到 6构成了通用的协同过滤推荐算法，而步骤 7到 8 则是针对本代码实现的扩展，用于在已有推荐基础上，实时更新推荐结果。

#### ~~2.2.3 综合作者，题材，用户给出的评分等综合推荐~~

### 2.3遇到的问题及解决方案

#### 2.3.1 去重问题：Index contains duplicate entries, cannot reshape

这个错误通常会在使用 pandas 进行数据透视表操作时出现。数据源包含了重复的记录，导致透视过程中生成的结果包含了重复的行或列索引，从而无法将结果矩阵进行成规模的重塑。

a.删除记录

```python
# 根据行和列索引判断记录是否重复
is_duplicated = df.duplicated(subset=['row_id', 'column_id'])
# 删除重复的记录
df = df[~is_duplicated]
```

b.求平均（将重复的值求平均值）

```python
pivoted_table = pd.pivot_table(
    df,
    values='value',      # 数据字段，也就是需要进行计算的值
    index='row_id',      # 行索引
    columns='column_id', # 列索引
    aggfunc='mean'       # 数据聚合方法，这里使用平均值
)
```

#### 2.3.2去除csv文件中的中文字符

```python
import pandas as pd
import string
# 定义过滤函数，将非 ASCII 字符替换为空白字符
def filter_non_ascii(s):
    return ''.join(filter(lambda x: x in string.printable, s))
# 读取 CSV 文件
df = pd.read_csv('data.csv')
# 过滤掉中文字符
df = df.applymap(filter_non_ascii)
# 对 DataFrame 进行操作，例如筛选、计算等
...
```

#### 2.3.3 如果某个用户a的评分发生变化，如何不重新训练模型的情况下更新a的推荐

思路：只需要更新评分矩阵，再调用model进行预测即可

缺点：变化的数据可能无法及时和模型进行更新，需要定期重新训练模型

```python
def update_ratings_matrix(ratings, user_id, book_id, new_rating):
    updated_ratings = ratings.copy()
    updated_ratings[user_id, book_id] = new_rating
    return updated_ratings
# 更新评分矩阵
updated_ratings = update_ratings_matrix(ratings, user_id, book_id, new_rating)
recommended_books = recommend_books(model, user_id, num_recommendations=3)
```

#### 2.3.4 tensorflow保存模型

```python
# 实例化模型并训练
num_users, num_items = train_data.shape
model = MatrixFactorization(num_users, num_items, latent_factors)
train(model, train_data, epochs=100)

# 保存模型权重
model.save_weights('matrix_factorization_model_weights.h5')
```

加载模型权重并进行推荐。这部分代码可以放在您需要进行推荐的任何位置，例如在一个单独的脚本中或在同一脚本的后面：

```python
# 创建一个与训练过的模型具有相同结构的新模型实例
loaded_model = MatrixFactorization(num_users, num_items, latent_factors)

# 加载保存的权重
loaded_model.load_weights('matrix_factorization_model_weights.h5')

# 使用加载的模型进行推荐
recommended_books = recommend_books(loaded_model, user_id, num_recommendations)
```

## 3 构建Mysql数据库

### 3.1 Books_Table

```python
import pandas as pd
import pymysql

# 读取 CSV 文件
df = pd.read_csv('test.csv', usecols=['id', 'name', 'age'])

# 连接 MySQL 数据库
conn = pymysql.connect(host='localhost', user='user', password='pwd', db='db_name', charset='utf8mb4', cursorclass=pymysql.cursors.DictCursor)

# 插入数据
with conn.cursor() as cursor:
    for index, row in df.iterrows():
        sql = "INSERT INTO test_table (id, name, age) VALUES (%s, %s, %s)"
        cursor.execute(sql, (row['id'], row['name'], row['age']))
    conn.commit()

# 关闭连接
conn.close()

```

#### 3.2 遇到的问题

##### 3.2.1 pymysql.err.IntegrityError: (1062, "Duplicate entry '1' for key 'test.PRIMARY'")

重复的主键值，解决方案同2.3.1

##### 3.2.2 UnicodeDecodeError: 'utf-8' codec can't decode byte 0xce in position 24274: invalid continuation byte

中文字符的原因，解决方案同2.3.2 ，待测试

## 4.数学相关

### 4.1ALS

ALS（Alternating Least Squares）是一种矩阵分解算法。它通过对评分矩阵进行分解，将用户和物品映射到低维的隐空间，从而可以预测用户对未评分物品的评分。ALS 算法的基本思想是通过交替优化用户和物品的隐式表示向量，以最小化预测评分和实际评分之间的均方误差。

在实现中，ALS 算法会将评分矩阵分解为用户因子矩阵和物品因子矩阵，分别为*U*和 *V*，矩阵元素为实数。其中，矩阵 U 表示用户在潜在空间中的向量表示，矩阵 *V*表示物品在潜在空间中的向量表示。当训练完成后，我们可以使用矩阵相乘的方式来计算预测评分矩阵 ，如下所示：
$$
\hat{R}=U \cdot V^T
$$

$$
其中，对于用户 u 和物品 i，预测评分 \hat{r}_{ui} 为 \hat{R}_{ui} = U_{u} \cdot V_{i}^T。
$$

矩阵分解的目的是将原始的评分矩阵分解为两个低维的因子矩阵，这样就可以在保留大量原始信息的情况下，将矩阵的存储和计算复杂度显著降低。由于ALS算法本质上是一个矩阵分解算法，因此它也是一种经典的推荐系统算法，在许多业界和学术界的系统中得到了广泛应用。

### 4.2 Adam 优化器

Adam（Adaptive Moment Estimation）是一种基于梯度的优化算法，用于训练神经网络。它是一种自适应学习率算法，可以自适应地调整每个参数的学习率，从而更好地适应不同的梯度的特征以获得更好的学习效果。

该算法通过分别计算梯度的一阶和二阶矩来确定参数的更新方向和大小。一阶矩是梯度的指数加权平均值，用来估计梯度的均值；而二阶矩是梯度平方的指数加权平均值，用来估计梯度的方差。具体来说，Adam的公式如下：
$$
1. 计算梯度的一阶矩和二阶矩：
m_t = \beta_1m_{t-1} + (1-\beta_1)g_t\\

v_t=\beta_2v_{t-1}+(1-\beta_2)g_t^2\\

其中 m_t 和 v_t 分别表示了梯度的一阶矩和二阶矩， g_t 是当前时刻的梯度。\\

2. 根据一阶矩和二阶矩，更新参数：\\

\theta_{t+1}=\theta_t-\frac{\eta}{\sqrt{\hat{v}_t}+\epsilon}\hat{m}_t\\

其中，\theta_t 表示参数值，\eta 表示学习率，\epsilon 是一个非常小的常数，用于防止除 0 错误。\hat{m}_t 和 \hat{v}_t 表示一阶矩和二阶矩的偏差修正：\\

\hat{m}_t=\frac{m_t}{1-\beta_1^t}\\

\hat{v}_t=\frac{v_t}{1-\beta_2^t}
$$

### 4.3 MES

MSE指的是均方误差（Mean Squared Error），是一个评估回归模型性能好坏的指标。它计算预测值与实际值之间差值的平方的平均值。通常情况下，MSE值越小意味着模型的拟合效果越好。

MSE可以用以下公式计算：

$$
MSE = \frac{1}{n} \sum_{i=1}^{n}(y_i - \hat{y_i})^2
$$
其中，$y_i$ 表示实际值，$\hat{y_i}$ 表示预测值，$n$ 表示样本数量。

在机器学习中，MSE通常用于回归问题的损失函数，可以通过最小化MSE来训练模型，使得模型可以更好地拟合训练数据，同时避免过度拟合。

在Tensorflow中，可以使用`tf.keras.losses.MeanSquaredError`类来计算MSE。例如：

```python
import tensorflow as tf

mse_loss = tf.keras.losses.MeanSquaredError()

y_true = [[0., 1.], [1., 1.]]
y_pred = [[1., 1.], [1., 0.]]

loss = mse_loss(y_true, y_pred)

print(loss.numpy()) # 输出 0.5
```

其中，`y_true` 表示实际值，`y_pred` 表示预测值，`loss`表示计算得到的MSE。
