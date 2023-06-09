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

##### 1.1.1.7 去掉顶部的导航栏

把`values\themes\themes.xml`中的字段替换

```xml
<style name="Theme.Test" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
替换为
<style name="Theme.Test" parent="Theme.AppCompat.Light.NoActionBar">
```

#### 1.1.2 待思考和解决的问题

a. ~~str应该为字符串，但是这里明显是个对象，不知道如何读出里面的数据~~    **已解决：参见1.1.1.6**

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

#### 1.1.3 设计思路：

**(a):用户端**

**(b):管理端**：本来想通过switch调整登录的状态（on：表示管理员，off：表示用户），在进行设置Listener的时候遇到了函数外部的值传不进去，后来改为在后端实现。在数据表user中加入isAdmin字段，以此判断是否为管理员

```python
sql = "SELECT id, password,isAdmin FROM user WHERE username=%s"
result = cursor.fetchone()
# 加入验证是否为管理员
if (result[2] == '1'):
        return 2
    else:
        return 1
#在API函数中
    if login_db(username,password)==1:
        print("ok")
        return jsonify(code=200, message='notAdmin')
    elif login_db(username,password)==2:
        return jsonify(code=200, message='isAdmin')
    else:
        print("no")
        return jsonify(code=201, message='用户名或密码错误')
```

在Java的`public static int sendLoginRequest(String urlStr, String username, String password)`函数中识别meg，进行判断是否为管理员，在根据此进行启动对应的活动。

```Java
if (respMsg.equals("isAdmin")) {
                return 2;
            } else if (respMsg.equals("notAdmin")) {
                return 1;
            } else {
                //做测试的时候用，正常应为false
                // TODO: 2023/4/13 记得改为false 
                return 0;
            }
```

### 1.2 推荐模块

todo

- [x] 整合fragment，之前写的。
- [x] 对接网络模块
- [x] 加载网络图片，glide不行的话，试试其他的

初步想法是做一个listview，在用户数据少的时候直接进行随机推荐，利用fragment机制进行跳转

#### 1.2.1遇到的问题以及解决方案

**a.RecyclerView下拉背景有残留**



![image-20230413230526639](C:\Users\86156\AppData\Roaming\Typora\typora-user-images\image-20230413230526639.png)

曲线解决：不知道如何产生的，但是，我解决了。（但愿）去掉padding

```xml
//novel_item.xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:background="?android:attr/selectableItemBackground"
   ...
    android:padding="8dp">
```

搞了我快3个小时，fuck！！！！！！！！！！！！！！！！！！！！！！！！！

### 1.3 用户个人中心模块（信息、评分、评论、订单等）

### 1.4 管理员：用户管理

- [x] 用户编辑
- [ ] 搜索

#### 1.4.1 设计思路

采用RecyclerView的形式展示用户列表，使用Volley库向后端请求用户信息。当管理员点击某个用户时，跳转到用户详情页面，使用基于回调函数实现参数的传递（用户名，密码，头像等）直接展示在用户详情页面，管理员可以对用户信息进行编辑。对于用户头像的修改，直接使用本地图库里的照片，并把路径上传到数据库。

#### 1.4.2遇到的问题及解决方案

a.用户上传图片后，根据路径解码加载图片遇到权限问题

E/BitmapFactory: Unable to decode stream: java.io.FileNotFoundException: /storage/emulated/0/Download/640.jpeg: open failed: EACCES (Permission denied)

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

```Java
// 检查是否有读取存储权限
if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

    // 申请读取存储权限
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
}
@Override
public void onRequestPermissionsResult(int requestCode,
        String[] permissions, int[] grantResults) {
    switch (requestCode) {
        case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
            // 如果请求被取消，则结果数组为空
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 读取存储权限已授权，可以进行后续操作
            } else {
                // 读取存储权限被拒绝，无法进行后续操作
            }
            return;
        }
    }
}
```

上面的方法是行不通的由chatgpt提供。查阅资料后得到了正确的解决方案

```xml
<application
        .......
        android:requestLegacyExternalStorage="true">
```

b.图片的路径改为了本地路径，而部分图片依然是网络图片的形式，两种加载方式冲突

```Java
// 加载图片
if (new File(filePath).exists()) {
    // 如果本地文件存在，则使用本地文件
    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
    avatarImageView.setImageBitmap(bitmap);
} else {
    // 如果本地文件不存在，则使用 Glide 进行网络加载
    Glide.with(this).load("https://www.example.com/avatar.jpg").into(avatarImageView);
}
```

### 1.5 管理员：图书管理

- [x] 图书编辑
- [x] 搜索
- [ ] 漂浮按钮图标太小

#### 1.5.1设计思路

使用RecyclerView的形式展示图书列表，并借助Volley库向后端请求图书信息。当管理员点击其中一个图书时，就会跳转到相应的图书详情页面。使用基于回调函数实现参数传递（书名、作者、出版社等），直接展示在图书详情页面中，并且管理员可以对图书信息进行修改。

在管理员编辑信息时，我们提供了上传基于本地图库里的照片的功能，管理员可以为图书选择一个本地照片并上传到后端数据库中，以便于更方便地对图书信息进行维护与管理。

当管理员想要添加用户时，不再向编辑页面传递值。

#### 1.5.2 遇到问题及解决方案

a).漂浮按钮

```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout
                       .......                     >
<androidx.recyclerview.widget.RecyclerView/>
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fab_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/my_icon"
    android:contentDescription="@string/add_ebook"
    app:backgroundTint="@color/my_fab_color" />
   </androidx.coordinatorlayout.widget.CoordinatorLayout>

```

b).添加或者修改图书时不能输入空值，否则服务器报错

获取焦点法，整个背景都是红色，不好看，放弃

```Java
private EditText bookIdEditText;
private EditText bookTitleEditText;
private EditText bookAuthorEditText;

private void initView() {
    bookIdEditText = findViewById(R.id.novel_id);
    bookTitleEditText = findViewById(R.id.novel_title);
    bookAuthorEditText = findViewById(R.id.novel_author);
    
    // 为每个EditText添加OnFocusChangeListener来处理焦点变化事件
    bookIdEditText.setOnFocusChangeListener(new EditTextFocusChangeListener());
    bookTitleEditText.setOnFocusChangeListener(new EditTextFocusChangeListener());
    bookAuthorEditText.setOnFocusChangeListener(new EditTextFocusChangeListener());
    
    // ...
}
private class EditTextFocusChangeListener implements View.OnFocusChangeListener {
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {  // 输入框失去焦点时
            EditText editText = (EditText) v;
            String text = editText.getText().toString();
            if (TextUtils.isEmpty(text.trim())) {
                editText.setBackgroundColor(Color.RED);  // 设置背景颜色为红色
            } else {
                editText.setBackgroundColor(Color.WHITE);  // 设置背景颜色为白色
            }
        }
    }
}
```

修改布局文件，用来提醒用户

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
   <item android:state_focused="true">
       <shape android:shape="rectangle">
           <solid android:color="@android:color/white"/>
           <stroke android:color="@android:color/holo_red_dark" android:width="2dp"/>
           <corners android:radius="5dp"/>
       </shape>
   </item>
   <item android:state_focused="false" android:state_empty="true">
       <shape android:shape="rectangle">
           <solid android:color="@android:color/white"/>
           <stroke android:color="@android:color/holo_red_dark" android:width="2dp"/>
           <corners android:radius="5dp"/>
       </shape>
   </item>
   <item android:state_focused="false">
       <shape android:shape="rectangle">
           <solid android:color="@android:color/white"/>
           <stroke android:color="@android:color/darker_gray" android:width="2dp"/>
           <corners android:radius="5dp"/>
       </shape>
   </item>
</selector>
```



### 1.6 管理员：订单管理

- [ ] 订单编辑
- [ ] 搜索

#### 1.6.1 设计思路

只可对价格，数量，收货地址，姓名，电话等信息进行编辑，可以对订单进行删除。

#### 1.6.2 遇到问题及解决方案

a）.对订单进行删除或者修改后，finish完成页面后，页面并不刷新，有些愚蠢。

在`FragmentA`中调用`startActivityForResult`方法，跳转到`ActivityB`：

```Java
Intent intent = new Intent(getActivity(), ActivityB.class);
startActivityForResult(intent, REQUEST_CODE_B);
```

在`ActivityB`中通过`setResult`方法将数据返回给`FragmentA`并关闭当前页面：

```Java
setResult(RESULT_OK, intent);
finish();
```

在`FragmentA`中重写`onActivityResult`方法，获取返回数据并进行页面数据的更新

```Java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_B && resultCode == RESULT_OK) {
        // 获取从ActivityB返回的数据
        String dataString = data.getStringExtra("data");

        // 在此进行数据的更新操作
        // ...
    }
}
```

**可以拓展到其他模块**

### 1.7 用户：订单模块

#### 1.7.1 设计思路

通过intent进行值传递，加载图书的相关信息。使用两个button来控制购买的数量，使用spinner组件方便用户选择地址，editview获取用户的电话，将这些信息打包，上传到后端服务器。

```python
//TODO 通过网络获得用户的地址 
```

### 1.8 电子书详情模块

- [x] 解决ratingbar的评分无法显示的问题

`onResponse` 中打印出 `bookRating` 的值是 `4.0`，但在函数外面却是 `0`，这可能是因为 `onResponse` 中的代码是在另一个线程中运行的，而函数外的打印语句则是在主线程中执行的。由于线程之间的数据不共享，所以可能会导致不同的线程对同一个变量的访问出现不一致的情况。

a).将打印语句放在 `onResponse` 中：

b).将 `bookRating` 定义为 `volatile`：

```java
private volatile float bookRating = 0.0f; // 将变量定义为 volatile
```

### 1.9 电子书搜索模块

- [ ] 加入搜索历史的功能

#### 1.9.1 设计思路

每个页面的搜索框都只是个摆设，点击后直接跳转到搜索的activity，加入一个判断是否为管理员的字段is_admin，通过intent.putExtra方法传递，在search活动中监听novelAdapter.setOnItemClickListener，并通过is_admin判断跳转到图书详情页面还是图书编辑页面。

### 1.10 不要更新Gradle

网络被墙，会失败，会导致无法运行程序

解决：给Android Studio挂代理

![image-20230509195903565](C:\Users\86156\AppData\Roaming\Typora\typora-user-images\image-20230509195903565.png)

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

### 1.11 购物车实现

https://cloud.tencent.com/developer/article/1779909

https://github.com/lilongweidev/ShoppingCart/tree/master

- [x] 从网络请求数据
- [ ] 不需要商家，删除那个图标
- [x] 结算的时候，获得数据，在calculationPrice()函数中，有提示
- [x] 对于如果发送的是一个列表，后端如何接收

在这个大佬的模板基础上修改，从网络获取数据，创建cart表，当用户从书的详情页点击加入购物车后，把书的数据加入到表中，默认count为1，重复的自动加1。购物车页面根据用户ID从后端拉取数据，可以调整数量。点击删除时，根据书的ID和用户ID对cart表的数据进行删除。点击结算后跳转到下订单的页面，上面用list显示勾选的商品。因为要显示，所以目前的思路是点击结算后随机生成一个tag，并把勾选的信息直接导入到order中，下订单时从order中根据tag拉取信息，由于书的其他的信息已经写入了order，在下订单时只需要填写地址，电话等，即可。这造成了一个问题，如果用户未完成支付，order表中有废弃的数据。已解决。

在用户完成支付后，会自动删除购物车的相关内容。

解决方案：

a.直接在cart页面把数据传给order页面，和收货信息一起提交，较麻烦。

b.在order中增加一个字段，paycheck，点击支付订单后置1，否则为0，在用户拉取订单时，只拉取paycheck为1的，且删除为0的订单。（√）

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
- [ ] 整合代码，目前推荐代码还未完成，保存在lyh/...
- [ ] 继续优化推荐函数，比如继续减少用户？
- [ ] 冷启动问题
- [x] 查找数据集，并进行数据清洗
- [ ] ~~学习如何从mysql中获取数据，并交由sklearn进行处理~~
- [x] 思考是否能只增加更新后那一个数据，而不必全部重新进行训练。
- [ ] [基于用户的协同过滤推荐算法](https://www.bilibili.com/video/BV1wA41197hb)
- [ ] [Apache Mahout初体验](https://blog.csdn.net/Jason_Nevermind/article/details/123982764 )
- [x]  [基于tensorflow的个性化电影推荐系统实战](https://blog.csdn.net/weixin_62075168/article/details/128431395)
- [ ] 考虑保存模型和加载模型，若无法加载模型，就推荐100个每次展示10个，曲线救国
- [x] 获得书的ID而不是下标
- [x] 解决加载慢的问题：放弃使用ratings.csv直接从mysql中读取数据，以自己创建的用户为元素进行学习

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

修改的思路：（伪代码）**2023年4月18日13点17分**

```python
#（1）直接搜索recom_books表若存在，则给出推荐，若是新用户则直接随机推荐：
def getRecomBooks(user_id):
    # 获取前端传递的用户ID
    user_id = request.form.get('user_id')
    data=get_recom_books_for_user(user_id)
#（2）什么时候刷新推荐表
#
def change_user_ratings(user_id,book_id,rating):
    if is_new_user==1:
        #对于新用户，需要训练模型，此时可以不给出推荐
        run_in_thread(model_train)()
     else：
    	#对于老用户来说，当他的评分修改达到5个时，开启子线程，调用recom_algorithm进行推荐既可（不必再训练）
    	if change_rating%5==0:
			run_in_thread(recom_algorithm)()
```

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

4. 训练模型：使用训练数据进行随机梯度下降训练，并将损失做为训练过程的指标。

   **为什么选择随机梯度下降？**
   
   ​	随机梯度下降（Stochastic Gradient Descent，SGD）是一种基于随机抽样的梯度下降算法，它在每一轮迭代中随机选择一个小数据集（称为 minibatch）作为训练集，将梯度下降的计算限制在这个小数据集上进行。和传统的梯度下降算法相比，SGD 的计算代价更小，因此可以更快地更新模型，加快收敛。在训练数据量非常大的情况下，采用随机梯度下降就可以大大提升训练速度。
   
   同时，由于SGD随机选择样本进行计算，也就是每次的计算结果都会略有不同，这也等价于对梯度进行噪声加入，可以一定程度上防止模型过拟合。此外，SGD 的更新规则也可以被加速，例如通过 GPU 等硬件加速。
   
   在深度学习中，通常很难一次读入整个训练集，因此随机梯度下降和其变体（如批量梯度下降和小批量梯度下降）已成为深度学习优化算法的主要选择。
   
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

曲线救国，换个编码方式

解决UnicodeDecodeError: 'utf-8' codec can't decode bytes in position 68763-68764: invalid continuation byte
原因是里面包含中文字符（乱码）
这里是用gbk18030解决的

```python
def read_csv(file_path):

    try:
        df = pd.read_csv(file_path, encoding='utf-8')
    except UnicodeDecodeError:
        try:
            df = pd.read_csv(file_path, encoding='gbk')
            print('gbk')
        except UnicodeDecodeError:
            df = pd.read_csv(file_path, encoding='gb18030')
            print('gbk18030')
    return df
```

下面这种方法不好使，try-except都不行

```python
import pandas as pd
import string
try:
    df = pd.read_csv('2.csv', index_col=False, encoding='utf-8')
except pd.errors.ParserError as e:
    # 打印出出错信息
    print(e)
    # 打印出不能读取的行的索引
    with open('2.csv', 'r', encoding='utf-8') as f:
        for i, line in enumerate(f):
            try:
                # 尝试读取一行数据
                pd.read_csv(line, header=None, encoding='utf-8')
            except pd.errors.ParserError as e:
                # 如果读取出错则说明该行不能被解析
                print(f"Cannot parse line {i}: {line}")

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

### 3.2 遇到的问题

##### 3.2.1 pymysql.err.IntegrityError: (1062, "Duplicate entry '1' for key 'test.PRIMARY'")

重复的主键值，解决方案同2.3.1

##### 3.2.2 UnicodeDecodeError: 'utf-8' codec can't decode byte 0xce in position 24274: invalid continuation byte

中文字符的原因，解决方案同2.3.2 ，~~待测试~~

##### 3.2.3 pymysql.err.DataError: (1406, "Data too long for column 'author' at row 1")，如何把太长的数据写为unknown

可以在代码中对过长的字段进行处理，将过长的字段写为 `unknown` 或者其他默认值。

```python
def truncate_string(s, max_len):
    """
    当字符串长度超过数据库限制时，将其截断或写为默认值
    """
    if s is None:
        return 'unknown'

    if len(s) > max_len:
        return 'unknown'
    else:
        return s

```

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

