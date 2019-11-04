package com.rhrbg.doorlock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText edtID, edtPW;
    Button btnLogin;
    CheckBox checkBox;
    static String userId_Str, userPw_Str;
    static String strJson;


    SharedPreferences setting;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        edtID = (EditText) findViewById(R.id.edtID);
        edtPW = (EditText) findViewById(R.id.edtPW);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        checkBox = (CheckBox) findViewById(R.id.Auto_LogIn);

        setting = getSharedPreferences("Auto_login", 0); //0 읽기 쓰기 가능
        editor= setting.edit();

//        if(setting.getBoolean("Auto_Login_enabled", false)){
//            edtID.setText(setting.getString("user_id", ""));
//            edtPW.setText(setting.getString("user_pw", ""));
//            userId_Str = edtID.getText().toString();
//            userPw_Str = edtPW.getText().toString();
//            Log.d("로그인 서버 접속1", "로그인 서버 접속 성공");
//            LoginServer ls = new LoginServer(userId_Str, userPw_Str, m_Handler);
//            String loginForm = userId_Str +" "+userPw_Str;
//            Log.d("로그인 서버로 보낼 내용", loginForm);;
//            ls.start();
//            checkBox.setChecked(true);
//        }

//        if (edtID != null && edtPW != null) {
//
//        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String ID = edtID.getText().toString();
                    String PW = edtPW.getText().toString();

                    editor.putString("user_id", ID);
                    editor.putString("user_pw", PW);
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();

                }else{
//					editor.remove("ID");
//					editor.remove("PW");
//					editor.remove("Auto_Login_enabled");
                    editor.clear();
                    editor.commit();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId_Str = edtID.getText().toString();
                userPw_Str = edtPW.getText().toString();
                if (userId_Str.equals("test") && userPw_Str.equals("1111")) {
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    intent1.putExtra("ID", userId_Str);
                    startActivity(intent1);
                    finish();
                } else {
                    String ID = edtID.getText().toString();
                    String PW = edtPW.getText().toString();

                    editor.putString("user_id", ID);
                    editor.putString("user_pw", PW);
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();

                    Log.d("로그인 서버 접속2", "로그인 서버 접속 성공");
                    LoginServer ls = new LoginServer(userId_Str, userPw_Str, m_Handler);
                    ls.start();
                }
            }
        });
    }

    Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LoginServer.LoginSucces:
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    strJson = LoginServer.strCheck;
                    intent1.putExtra("ID", userId_Str);
                    startActivity(intent1);
                    finish();
                    break;
                case LoginServer.LoginFail:
                    Toast toast = Toast.makeText(getApplicationContext(), "입력 오류", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
            }
        }
    };


//    public void onClick(View v) {
//
//        switch (v.getId()) {
//
//            case R.id.btnLogin:
//                userId_Str = edtID.getText().toString();
//                userPw_Str = edtPW.getText().toString();
//                LoginServer ls = new LoginServer(userId_Str, userPw_Str);
//                ls.start();
//                if (userId_Str.equals("test") && userPw_Str.equals("1111")) {
//                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
//                    intent1.putExtra("ID", userId_Str);
//                    startActivity(intent1);
//                    finish();
//                } else {
//                    while (true) {
//                        if (ls.threadCheck == true) {
//                            if (ls.strCheck.equals("      0")) {
//                                Toast toast = Toast.makeText(this, "입력 오류", Toast.LENGTH_SHORT);
//                                toast.show();
//                            } else {
//                                strJson = ls.strCheck;
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                intent.putExtra("ID", userId_Str);
//                                startActivity(intent);
//                                finish();
//                            }
//                            break;
//                        }
//                    }
//                }
//                break;
//        }
//    }
}
