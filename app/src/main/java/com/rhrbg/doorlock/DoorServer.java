package com.rhrbg.doorlock;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rhrbg on 2016-05-30.
 */
public class DoorServer extends Thread {

    String m_url = "http://192.168.100.44:8080/coolnfc/admin.jsp";
    //String m_url = "http://kdysjsp.iptime.org:8080/noriter/Login.jsp";
    StringBuffer buffer;
    Handler m_Handler;
    boolean threadCheck = false;
    String userID, room;
    int type;
    static String strCheck;

    public DoorServer(String _id, String _room, int _type, Handler _Handler) {
        userID = _id;
        room = _room;
        type = _type;
        m_Handler=_Handler;

    }

    public void run() {
        httpRespone();
    }

    private void httpRespone() {
        buffer = new StringBuffer();
        buffer.append("user_id=").append(userID).append("&lecroom=").append(room).append("&type=").append(type);
        //buffer.append("id=").append(id).append("&passwd=").append(pw);
        // request method is POST
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------

            URL url = new URL(m_url);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST
            http.setConnectTimeout(50000);

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   서버로 값 전송
            //--------------------------
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            //-----------------------------
            //   서버에서 전송받기
            //-----------------------------
            outStream.close();
            writer.close();
            InputStream tmp = http.getInputStream();
            StringBuilder str = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(tmp, "UTF-8"));
            //StringBuilder builder = new StringBuilder();
            String strTemp = "";

            while ((strTemp = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                if (strTemp != "") {
                    str.append(strTemp);
                }
            }
            strCheck = str.toString();
            threadCheck = true;
            Message msg = m_Handler.obtainMessage();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } // try
    } // HttpPostData
}