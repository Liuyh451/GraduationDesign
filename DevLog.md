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

## 2.Flask后端

### 2.1注册登录模块

使用pyflask进行简单的验证

###### Todo

- [ ] 学习flask验证，并测试API
- [ ] 冷启动问题，直接随机推荐任意的，待用户喜欢的数据达到一定数量的时候，再进行推荐

### 2.2电子书推荐模块

使用sklearn进行算法推荐，只有用户标记了喜欢后，才进行推荐。如果仅仅浏览后就做出推荐，系统负载过大，不利于使用。当用户给出喜欢（favorite）后，在用户数据库增加响应的字段，然后更新模型。否则直接调用模型即可.

###### todo

- [ ] 学习python和mysql的交互
- [ ] 查找数据集，并进行数据清洗
- [ ] 学习如何从mysql中获取数据，并交由sklearn进行处理
- [ ] 思考是否能只增加更新后那一个数据，而不必全部重新进行训练。
- [ ] https://www.bilibili.com/video/BV1wA41197hb?p=8&vd_source=6368fab9a1f9e5ff082e1e1f68e1de89基于协同过滤
- [ ] https://blog.csdn.net/weixin_62075168/article/details/128431395 基于tensorflow
- [ ] https://blog.csdn.net/Jason_Nevermind/article/details/123982764
- [ ] https://blog.csdn.net/weixin_62075168/article/details/128431395

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

#### 2.2.2 基于tensorflow的推荐算法

首先，您需要使用用户对书籍的评分数据对TensorFlow模型进行训练，以便模型可以学习用户的偏好和行为模式。然后，您可以使用模型对新的用户-书籍组合进行推荐。

以下是一个例子，展示如何在TensorFlow中构建和使用推荐模型。这个例子使用了基于矩阵分解的协同过滤算法。

首先，您需要加载数据和对数据进行预处理。假设您的数据如下：

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

使用pandas库加载数据：

```python
import pandas as pd

# 加载评分数据
ratings = pd.read_csv('ratings.csv')

# 将评分数据转化为矩阵
ratings_matrix = ratings.pivot(
    index='user_id',
    columns='book_id',
    values='rating'
).fillna(0)

# 加载书籍元数据
books = pd.read_csv('books.csv')
```

然后，您需要建立TensorFlow模型。基于矩阵分解的协同过滤算法可以将用户和书籍都映射到低维向量上，然后计算这些向量之间的余弦相似度。下面是一个简单的TensorFlow模型示例：

```python
import tensorflow as tf
import numpy as np

# 定义模型
n_user = ratings_matrix.shape[0]
n_book = ratings_matrix.shape[1]
n_embedding = 20

user_input = tf.placeholder(tf.int32, [None])
book_input = tf.placeholder(tf.int32, [None])
rating_input = tf.placeholder(tf.float32, [None])

user_embedding = tf.get_variable('user_embedding', shape=(n_user, n_embedding))
book_embedding = tf.get_variable('book_embedding', shape=(n_book, n_embedding))

user_bias = tf.get_variable('user_bias', shape=(n_user,))
book_bias = tf.get_variable('book_bias', shape=(n_book,))

user_embed = tf.nn.embedding_lookup(user_embedding, user_input)
book_embed = tf.nn.embedding_lookup(book_embedding, book_input)

user_bias_embed = tf.nn.embedding_lookup(user_bias, user_input)
book_bias_embed = tf.nn.embedding_lookup(book_bias, book_input)

prediction = tf.reduce_sum(user_embed * book_embed, axis=1) + user_bias_embed + book_bias_embed

# 定义损失函数
loss = tf.reduce_mean(tf.square(rating_input - prediction))

# 定义优化器
train_op = tf.train.AdamOptimizer().minimize(loss)

```

通过训练模型学习用户和书籍的偏好向量，模型可以预测一个用户对于某本书的评分。然后，您可以使用模型对新的用户-书籍组合进行推荐。

```python
# 训练模型
n_iter = 1000
batch_size = 512

with tf.Session() as sess:
    sess.run(tf.global_variables_initializer())
    for i in range(n_iter):
        # 从评分数据中随机取出一个batch的数据进行训练
        idx = np.random.choice(len(ratings), batch_size)
        batch_users = ratings.loc[idx, 'user_id'].values
        batch_books = ratings.loc[idx, 'book_id'].values
        batch_ratings = ratings.loc[idx, 'rating'].values

        feed_dict = {
            user_input: batch_users,
            book_input: batch_books,
            rating_input: batch_ratings
        }
        _, l = sess.run([train_op, loss], feed_dict=feed_dict)
        if i % 100 == 0:
            print(f'Iteration {i} Loss {l}')

    # 预测某个用户对于所有书籍的评分
    user_id = 1
    user_ratings = ratings_matrix.loc[user_id]
    book_ids = list(user_ratings.index)
    feed_dict = {
        user_input: [user_id] * len(book_ids),
        book_input: book_ids,
    }
    predicted_ratings = sess.run(prediction, feed_dict=feed_dict)
    
    # 选出最高的前10个评分，推荐给用户
    top_n = 10
    recommendation_idx = np.argsort(predicted_ratings)[-top_n:][::-1]
    recommendations = books.loc[book_ids[recommendation_idx], 'Title']
    print(f'Recommended books for user {user_id}:')
    for r in recommendations:
        print(r)

```

通过调整模型结构和参数，可以得到更加精确和个性化的推荐结果。

#### 2.2.3 综合作者，题材，用户给出的评分等综合推荐

## 3 不要更新Gradle

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
