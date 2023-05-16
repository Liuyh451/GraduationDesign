package com.example.test;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.bean.Product;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetUnit {
    /**
     * 发送登录请求并返回登录结果
     *
     * @param urlStr   请求地址
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    public static int[] sendLoginRequest(String urlStr, String username, String password) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // 构建请求参数
            StringBuilder sb = new StringBuilder();
            sb.append("username=" + URLEncoder.encode(username, "UTF-8"));
            sb.append("&");
            sb.append("password=" + URLEncoder.encode(password, "UTF-8"));
            byte[] body = sb.toString().getBytes();


            // 创建连接
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(body.length));
            connection.setDoOutput(true);
            connection.getOutputStream().write(body);

            // 发送请求并读取结果
            int responseCode = connection.getResponseCode();

            StringBuilder response = new StringBuilder();
            String line;
            if (responseCode == HttpURLConnection.HTTP_OK) {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            //将json字符串转为JsonObj，方便读取返回值
            JSONObject person = new JSONObject(response.toString());
            String respCode = person.getString("code");
            String respMsg = person.getString("message");
            int resid = person.getInt("uid");
            // 判断响应结果
            if (respMsg.equals("isAdmin")) {
                int[] result = {2, resid};
                return result;
            } else if (respMsg.equals("notAdmin")) {
                int[] result = {1, resid};
                return result;
            } else {
                //做测试的时候用，正常应为false
                int[] result = {0, 0};
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            int[] result = {0, 0};
            return result;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static int[] sendRegisterRequest(String urlStr, String username, String password) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // 构建请求参数
            StringBuilder sb = new StringBuilder();
            sb.append("username=" + URLEncoder.encode(username, "UTF-8"));
            sb.append("&");
            sb.append("password=" + URLEncoder.encode(password, "UTF-8"));
            byte[] body = sb.toString().getBytes();


            // 创建连接
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(body.length));
            connection.setDoOutput(true);
            connection.getOutputStream().write(body);

            // 发送请求并读取结果
            int responseCode = connection.getResponseCode();


            StringBuilder response = new StringBuilder();
            String line;
            if (responseCode == HttpURLConnection.HTTP_OK) {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            //将json字符串转为JsonObj，方便读取返回值
            JSONObject person = new JSONObject(response.toString());
            String respCode = person.getString("code");
            Log.d("TAG", "rescode---" + respCode);
            int resid = person.getInt("uid");
            // 判断响应结果
            if (respCode.equals("200")) {
                int[] result = {1, resid};
                return result;
            } else {
                int[] result = {0, 0};
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            int[] result = {0, 0};
            return result;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void getRating(Context context, String uid, String book_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/bookrating";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                    jsonObject.put("book_id", book_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void getUserInfo(Context context, String uid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/getUserInfo";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void placeOrder(Context context, String tag, String buyerName, String address, String phone, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/order/create";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("buyerName", buyerName);
                    jsonObject.put("address", address);
                    jsonObject.put("tag", tag);
                    jsonObject.put("phone", phone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void modifyOrder(Context context, String orderId, String buyerName, String price, String quantity, String address, String phone, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/order/modify";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("orderId", orderId);
                    jsonObject.put("price", price);
                    jsonObject.put("quantity", quantity);
                    jsonObject.put("address", address);
                    jsonObject.put("phone", phone);
                    jsonObject.put("buyerName", buyerName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void deleteOrder(Context context, String orderId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/order/delete";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("orderId", orderId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void getMyOrder(Context context, String userId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/order/myOrder";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void makeComment(Context context, String uid, String bookid, String rating, String comment, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/makecomment";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                    jsonObject.put("book_id", bookid);
                    jsonObject.put("rating", rating);
                    jsonObject.put("comment", comment);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void updateUserInfo(Context context, String uid, String username, String password, String avatarPath, String address, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/user/updateInfo";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                    jsonObject.put("avatar", avatarPath);
                    jsonObject.put("address", address);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void userInfoModify(Context context, String uid, String nickName, String phone, String password, String avatarPath, String address, String gender, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/user/uInfoModify";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                    jsonObject.put("nickName", nickName);
                    jsonObject.put("phone", phone);
                    jsonObject.put("password", password);
                    jsonObject.put("avatar", avatarPath);
                    jsonObject.put("address", address);
                    jsonObject.put("gender", gender);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void modifyBook(Context context, String bookId, String title, String author, String bookCover, String price, String description, String language, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/book/modify";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("book_id", bookId);
                    jsonObject.put("bookCover", bookCover);
                    jsonObject.put("title", title);
                    jsonObject.put("author", author);
                    jsonObject.put("price", price);
                    jsonObject.put("description", description);
                    jsonObject.put("language", language);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void addToCart(Context context, String bookId, String title, String author, String bookCover, String price, String count, String uid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/cart/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("book_id", bookId);
                    jsonObject.put("bookCover", bookCover);
                    jsonObject.put("title", title);
                    jsonObject.put("author", author);
                    jsonObject.put("price", price);
                    jsonObject.put("count", count);
                    jsonObject.put("uid", uid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void deleteBook(Context context, String bookId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/book/delete";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("book_id", bookId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void searchBook(Context context, String title, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/bookSearch";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("title", title);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void addFavorite(Context context, String uid, String bookId, String title, String author, String rating, String bookCover, String date, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/book/favorite";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                    jsonObject.put("book_id", bookId);
                    jsonObject.put("title", title);
                    jsonObject.put("author", author);
                    jsonObject.put("rating", rating);
                    jsonObject.put("bookCover", bookCover);
                    jsonObject.put("date", date);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void getFavorite(Context context, String uid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/book/getfavorite";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }


    public static void getUserRating(Context context, String uid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/user/rating";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void getCartList(Context context, String uid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/cart/getInfo";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void getOrderInfo(Context context, String tag, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/order/getinfo";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("tag", tag);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void cartSettle(Context context, String uid, String productList, int orderId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/cart/settle";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                    jsonObject.put("products", productList);
                    jsonObject.put("tag", orderId);
                    jsonObject.put("paycheck", "0");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void cartDelete(Context context, String uid, String productList, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://10.0.2.2:5000/cart/delete";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", uid);
                    jsonObject.put("products", productList);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());
                Log.d("param", params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(context).add(stringRequest);
    }

}

