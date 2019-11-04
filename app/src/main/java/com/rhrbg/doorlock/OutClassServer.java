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
 * Created by rhrbg on 2016-05-28.
 */
public class OutClassServer extends Thread {

//    String m_url = "http://172.17.202.64:8080/coolnfc/admin.jsp";
//        String m_url = "http://192.168.100.32:8080/coolnfc/admin.jsp";
    String m_url = "http://noring.iptime.org:7012/coolnfc/admin.jsp";
    //String m_url = "http://kdysjsp.iptime.org:8080/noriter/Login.jsp";
    StringBuffer buffer;
    boolean threadCheck = false;
    String id, date, startT, endT, classN, year, month;
    int m_type;
    protected Message m_msg = null;

    String testCODE;
//    String testCODE =  id + " " + year + " " + month + " " + date + " " + startT + " " + endT + " " + classN + " " + m_type;

    Handler m_handler;

    static final int ServerSucces = 1;
    static String strCheck;

    public OutClassServer(Handler _m_Handler, String _id, String _year, String _month, String _date, String _startT, String _endT, String _classN, int _type) {
        id = _id;
        year = _year;
        month = _month;
        date = _date;
        startT = _startT;
        endT = _endT;
        classN = _classN;
        m_type = _type;
        m_handler = _m_Handler;
    }

    public void run() {
        httpRespone();
        switch (m_type) {
            case 6:
                m_msg = m_handler.obtainMessage(ServerSucces);  // 1
                m_msg.arg1 = Integer.parseInt(strCheck);
                break;
        }
        m_msg.sendToTarget();

    }

    private void httpRespone() {
        buffer = new StringBuffer();
        buffer.append("other_user_id=").append(id).append("&other_lecroom=").append(classN)
                .append("&other_year=").append(year).append("&other_month=").append(month)
                .append("&other_date=").append(date).append("&other_start=").append(startT)
                .append("&other_end=").append(endT).append("&type=").append(m_type);
        testCODE = id + " " + year + " " + month + " " + date + " " + startT + " " + endT + " " + classN + " " + m_type;
        Log.d("담겼나 보자!", testCODE);
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
            Log.d("담겼나 보자!", testCODE);
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
            Log.d("받은 내용", strCheck);
            threadCheck = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } // try
    } // HttpPostData
}