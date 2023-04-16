package com.example.test;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NetUnit {
    /**
     * 发送登录请求并返回登录结果
     *
     * @param urlStr   请求地址
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    public static int sendLoginRequest(String urlStr, String username, String password) {
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
            Log.d("TAG", "resmsg---" +respMsg);
            Log.d("TAG", "rescode---" +respCode );
            // 判断响应结果
            if (respMsg.equals("isAdmin")) {

                return 2;
            } else if (respMsg.equals("notAdmin")) {
                return 1;
            } else {
                //做测试的时候用，正常应为false
                // TODO: 2023/4/13 记得改为false 
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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
    public static boolean sendRegisterRequest(String urlStr, String username, String password) {
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
            Log.d("TAG", "rescode---" +respCode );
            // 判断响应结果
            if (respCode.equals("200")) {
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
