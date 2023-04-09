package com.example.test;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class login_post {
    /**
     * 发送登录请求并返回登录结果
     *
     * @param urlStr     请求地址
     * @param username   用户名
     * @param password   密码
     * @return           登录结果
     */
    public static boolean sendLoginRequest(String urlStr, String username, String password) {
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
            Log.d("TAG","res"+str);
            // 判断响应结果
            if (responseCode==200) {
                Log.d("TAG","111111");

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
}
