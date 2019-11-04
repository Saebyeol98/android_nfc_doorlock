package com.rhrbg.doorlock;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
 * Created by rhrbg on 2016-05-26.
 */
public class LoginServer extends Thread {

    protected static final int LoginSucces = 1;
    protected static final int LoginFail = 0;
    protected Message m_msg = new Message();
//  String m_url = "http://192.168.100.32:8080/coolnfc/login.jsp";
//  String m_url = "http://172.17.202.64:8080/coolnfc/admin.jsp";
        String m_url = "http://noring.iptime.org:7012/coolnfc/login.jsp";
    //String m_url = "http://kdysjsp.iptime.org:8080/noriter/Login.jsp";
    StringBuffer buffer;
    boolean threadCheck = false;
    private Handler m_Handler;
    String id, pw;
    static String strCheck;

    public LoginServer(String _id, String _pw, Handler _m_Handler) {
        id = _id;
        pw = _pw;
        m_Handler = _m_Handler;
    }

    public void run() {

        httpRespone();


        /**
         * 로그인 성공 판단 여부
         * 0 - 로그인 실패
         * 1 - 로그인 성공
         */
        if (threadCheck == true) {

            Log.d("로그인 전송 결과값", strCheck);
            if (strCheck.equals("0")) {
                m_msg = m_Handler.obtainMessage(LoginFail);
            } else {
                m_msg = m_Handler.obtainMessage(LoginSucces);
            }
            m_msg.sendToTarget();
        }

    }

    private void httpRespone() {
        buffer = new StringBuffer();
        buffer.append("user_id=").append(id).append("&user_pw=").append(pw);
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
            //--------------------------
            //   서버에서 전송받기
            //--------------------------
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
            strCheck = str.toString(); //서버에서 받은 값을 내부 변수로 저장한다
            threadCheck = true;
            Log.e("서버 접속", "서버 접속 완료");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } // try
    } // HttpPostData
}