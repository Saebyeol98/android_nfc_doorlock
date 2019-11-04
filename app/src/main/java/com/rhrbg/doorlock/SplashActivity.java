package com.rhrbg.doorlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    String userId_Str, userPw_Str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        try {
            Runtime.getRuntime().exec("su");
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
            dialog.setMessage("루팅된 흔적을 발견하여 앱을 종료합니다\n" +
                    "루팅을 해제하고 난 후 다시 실행해주세요").setCancelable(
                    false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog alert = dialog.create();
            // Title for AlertDialog
            alert.setTitle("A.N.D");
            // Icon for AlertDialog
            alert.setIcon(R.drawable.icon);
            alert.show();

        } catch (Exception e) {
            setting = getSharedPreferences("Auto_login", 0); //0 읽기 쓰기 가능
            editor = setting.edit();

            Handler hd = new Handler();
            hd.postDelayed(new splashhandler(), 2000); // 3초 후에 hd Handler 실행

        }
    }

    private class splashhandler implements Runnable {
        public void run() {

//            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//            startActivity(intent);
//            SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거

            if (setting.getBoolean("Auto_Login_enabled", false) != false && setting.getString("user_id", "") != "" && setting.getString("user_pw", "") != "") {
                userId_Str = setting.getString("user_id", "");
                userPw_Str = setting.getString("user_pw", "");
                String loginForm = userId_Str + " " + userPw_Str;
                Log.d("로그인 서버로 보낼 내용1", loginForm);

                Log.d("로그인 서버 접속1", "로그인 서버 접속 성공");
                LoginServer ls = new LoginServer(userId_Str, userPw_Str, m_Handler);
                String loginForm2 = userId_Str + " " + userPw_Str;
                Log.d("로그인 서버로 보낼 내용2", loginForm2);
                ls.start();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
            }
        }
    }

    Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LoginServer.LoginSucces:
                    Intent intent1 = new Intent(SplashActivity.this, MainActivity.class);
                    LoginActivity.strJson = LoginServer.strCheck;
                    intent1.putExtra("ID", userId_Str);
                    startActivity(intent1);
                    finish();
                    break;
                case LoginServer.LoginFail:
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
                    break;
            }
        }
    };
}
