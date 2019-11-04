package com.rhrbg.doorlock;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by seabu on 2016-05-30.
 */
public class LogServer extends Thread {

    static final int DoorState = 0;
    static final int DoorGetKey = 1;
    static final int DoorMoveSuccess = 2;

    protected static final int DoorClose = 1;
    protected static final int DoorOpen = 0;
    protected static final int DoorNot = 2;

    public static int THREAD_CHECK = 0;
    static final int SERVER_SUCCES = 1004;

    protected Message m_msg = null;

    //    String m_url = "http://192.168.100.32:8080/coolnfc/admin.jsp";
//        String m_url = "http://172.17.202.64:8080/coolnfc/admin.jsp";
    String m_url = "http://noring.iptime.org:7012/coolnfc/admin.jsp";
    //String m_url = "http://kdysjsp.iptime.org:8080/noriter/Login.jsp";
    StringBuffer buffer;
    boolean threadCheck = false;
    String m_userid, m_lecroom;
    static String strCheck;
    int m_type;
    Handler m_Handler;

    //요청 외 시간 목록 불러오기 요청 type = 7
    public LogServer(Handler _handler, String _id, int _type) {
        m_Handler = _handler;
        m_userid = _id;
        m_type = _type;
    }

    //요청 외 시간 목록 클릭시 문 열거나 닫기 요청 type = 2345
    public LogServer(Handler _Handler, String _id, String _lecroom, int _type) {
        m_Handler = _Handler;
        m_userid = _id;
        m_lecroom = _lecroom;
        m_type = _type;
    }

    public void run() {

        switch (m_type) {
            case 1:
                httpRespone2();

                Log.d("받은 내용은?", strCheck);
                if (strCheck.equals("     0")) {
                    m_msg = m_Handler.obtainMessage(DoorState, DoorClose);  // 0, 1
                } else if (strCheck.equals("     1")) {
                    m_msg = m_Handler.obtainMessage(DoorState, DoorOpen);   // 0, 0
                } else {
                    m_msg = m_Handler.obtainMessage(DoorState, DoorNot);    // 0, 2
                }
                m_msg.sendToTarget();

                break;
            case 2:
            case 4:
                httpRespone2();
                LogKeyJSON(strCheck);
                Log.e("send", "send");
                break;
            case 3:
            case 5:
                httpRespone2();
                m_Handler.obtainMessage(DoorMoveSuccess);  // 2
                m_msg.arg1 = m_type;
                m_msg.sendToTarget();
                break;
            case 7:
                httpRespone();
//                strCheck="";
                LogListJSON(strCheck);
                Log.e("send", "send");
                break;
        }
    }

    private void httpRespone() {
        buffer = new StringBuffer();
        buffer.append("other_user_id=").append(m_userid).append("&type=").append(m_type);
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
                    break;
                }
            }
            strCheck = str.toString();
            threadCheck = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } // try
    } // HttpPostData

    private void httpRespone2() {
        buffer = new StringBuffer();
        buffer.append("user_id=").append(m_userid).append("&lecroom=").append(m_lecroom).append("&type=").append(m_type);
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
//            StringBuilder str = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(tmp, "UTF-8"));
            //StringBuilder builder = new StringBuilder();
            String strTemp = "";
            String str;
//            Log.d("받은내용", reader.readLine());

            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        strTemp += str;
//                if (strTemp != "") {
//                    str.append(strTemp);
//                    break;
//                }
            }
            Log.d("Data", strTemp);
            strCheck = strTemp;
            threadCheck = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } // try
    } // HttpPostData

    protected void LogListJSON(String receiveData) {
        Log.d("수신된 내용!", receiveData);

        try {

            THREAD_CHECK = 1;
            JSONObject json = new JSONObject(receiveData);

            JSONArray jsonArray = json.getJSONArray("prime");

            for (int i = 0; i < jsonArray.length(); i++) {
                Message msg = m_Handler.obtainMessage(SERVER_SUCCES);

                json = jsonArray.getJSONObject(i);

                Bundle m_bundle = new Bundle();

//                m_bundle.putString("getUser", json.getString("userid"));
                m_bundle.putString("getYear", json.getString("year"));
                m_bundle.putString("getMonth", json.getString("month"));
                m_bundle.putString("getDate", json.getString("date"));
                m_bundle.putString("getS_time", json.getString("start"));
                m_bundle.putString("getE_time", json.getString("end"));
                m_bundle.putString("getLecroom", json.getString("lecroom"));

                msg.setData(m_bundle);
                m_Handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void LogKeyJSON(String receiveData) {
        Log.d("수신된 내용!", receiveData);

        try {
            JSONObject json = new JSONObject(receiveData);

            JSONArray jsonArray = json.getJSONArray("optimus");

            for (int i = 0; i < jsonArray.length(); i++) {
                Message msg = m_Handler.obtainMessage(DoorGetKey);

                json = jsonArray.getJSONObject(i);

                Bundle m_bundle = new Bundle();

                m_bundle.putString("getAES", json.getString("AES"));
                m_bundle.putString("getHASH", json.getString("HASH"));

                msg.setData(m_bundle);
                m_Handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}