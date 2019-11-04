package com.rhrbg.doorlock;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.NfcF;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class ScheduleActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    //    private TextView SC01, SC02, SC03;
    private TextView tvSC[][];
    //    private TextView SC25, SC26, SC27;
    ScheduleActivity m_schedule;
    private int strType;
    String strTemp;
    String roomNum;
    NfcAdapter mNfcAdapter; // NFC 어댑터
    PendingIntent mPendingIntent; // 수신받은 데이터가 저장된 인텐트
    IntentFilter[] mIntentFilters; // 인텐트 필터
    String[][] mNFCTechLists;
    String[][] sup;
    static String userID = null;
    boolean ocCheck = true;
    String strKeyData, strKeyData2;
    TextView tvUserID = null;
    String readResult;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private TextView tvopen, tvclose;
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //userID = LoginActivity.userId_Str;

        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        Log.e("UserId", userID + "   ,   " + LoginActivity.userId_Str);

        tvUserID = (TextView) findViewById(R.id.tv_userID);
        /**
         * TextView 를 xml 에서 선언한 id와 매칭을 시킨다
         */
        sup = new String[8][5];
        int idTvSC[][] = {{R.id.SC01, R.id.SC11, R.id.SC21, R.id.SC31, R.id.SC41},
                {R.id.SC02, R.id.SC12, R.id.SC22, R.id.SC32, R.id.SC42},
                {R.id.SC03, R.id.SC13, R.id.SC23, R.id.SC33, R.id.SC43},
                {R.id.SC04, R.id.SC14, R.id.SC24, R.id.SC34, R.id.SC44},
                {R.id.SC05, R.id.SC15, R.id.SC25, R.id.SC35, R.id.SC45},
                {R.id.SC06, R.id.SC16, R.id.SC26, R.id.SC36, R.id.SC46},
                {R.id.SC07, R.id.SC17, R.id.SC27, R.id.SC37, R.id.SC47},
                {R.id.SC08, R.id.SC18, R.id.SC28, R.id.SC38, R.id.SC48}};
        tvSC = new TextView[8][5];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 5; j++) {
                tvSC[i][j] = (TextView) findViewById(idTvSC[i][j]);
            }
        }

        if (LoginActivity.strJson != null) {
            getJsonText();
//            tvUserID.setText("사용자: " + userID);
        } else {
            Toast.makeText(getApplicationContext(), "시간표 정보를 불러올수 없습니다", Toast.LENGTH_SHORT);
        }
//        SC25 = (TextView) findViewById(R.id.SC25);
//        SC26 = (TextView) findViewById(R.id.SC26);
//        SC27 = (TextView) findViewById(R.id.SC27);

        tvopen = (TextView) findViewById(R.id.tvopen);


        // NFC 어댑터를 구한다
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent targetIntent = new Intent(this, ScheduleActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        mIntentFilters = new IntentFilter[]{ndef,};
        mNFCTechLists = new String[][]{new String[]{NfcF.class.getName()}};

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String action = passedIntent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                processTag(passedIntent);
            }
        }
        // NFC 어댑터가 null 이라면 칩이 존재하지 않는 것으로 간주
        if (mNfcAdapter == null) {
            Toast.makeText(this, "This phone is not NFC enable.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(this, "Scan a NFC tag", Toast.LENGTH_SHORT).show();
        // NFC 데이터 활성화에 필요한 인텐트를 생성
//        Intent intent2 = new Intent(this, getClass());
//        intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        mPendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);
//        // NFC 데이터 활성화에 필요한 인텐트 필터를 생성
//        IntentFilter iFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        try {
//            iFilter.addDataType("*/*");
//            mIntentFilters = new IntentFilter[]{iFilter};
//        } catch (Exception e) {
//            Toast.makeText(this, "Make IntentFilter error", Toast.LENGTH_SHORT).show();
//        }
//        mNFCTechLists = new String[][]{new String[]{NfcF.class.getName()}};
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        m_schedule = this;
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

    }


    /**
     * JSON을 받아오는 클래스
     * 실질적인 작업은 이 클래스 내부에서 다 처리한다
     */
    private void getJsonText() {
        StringBuffer sb = new StringBuffer();

        LoginActivity la = new LoginActivity();
        int strData1 = 0;
        int strData2 = 0;
        String strData3 = "";
        String strData4 = "";
        String strData5 = "";
        int strData6 = 0;


        //색상을 배열로 저장하여 배경색을 채워준다
        int color[] = {Color.rgb(218, 218, 255), Color.rgb(200, 229, 251), Color.rgb(255, 126, 126), Color.rgb(0, 217, 163), Color.GRAY, Color.DKGRAY};

        try {
            /**
             * JSON 파싱
             * JSONObject 를 이용해 Json을 받아온곳 변수를 오브젝트 안에 담아놓고
             * JSON 형태에 따라 파싱 정보가 달리된다
             * 현재 JSON구조는 [{home=[내부 정보{}]}]
             */
            JSONObject object = new JSONObject(la.strJson);

            JSONArray Array = new JSONArray(object.getString("home"));

            for (int i = 0; i < Array.length(); i++) {

                JSONObject let = Array.getJSONObject(i);

                strData1 = let.getInt("class_end"); //끝나는 시간
                Log.d("STR", String.valueOf(strData1));
                strData2 = let.getInt("class_day"); //요일
                Log.d("STR2", String.valueOf(strData2));
                strData3 = let.getString("class_place"); //장소
                Log.d("STR3", strData3);
                strData4 = let.getString("class_name"); //과목명
                Log.d("STR4", strData4);
                strData5 = let.getString("class_code"); //과목코드(현재는 사용안함)
                Log.d("STR5", strData5);
                strData6 = let.getInt("class_start"); //시작시간
                Log.d("STR6", String.valueOf(strData6));

                roomNum = strData3; //장소를 담는다

                final String strTemp = roomNum;
                for (int j = strData6; j <= strData1; j++) {
                    tvSC[j - 1][strData2 - 1].setBackgroundColor(color[i % color.length]);

                    tvSC[j - 1][strData2 - 1].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //type1 도어락 개폐정보
                            roomNum = strTemp;
                            strType = 1;

                            ScheduleServer scheduleS = new ScheduleServer(userID, roomNum, strType, m_Handler);
                            scheduleS.start();

//                    Toast.makeText(getApplicationContext(),"Toast : " + j,Toast.LENGTH_SHORT).show();
                        }


                    });
                }
                tvSC[strData6 - 1][strData2 - 1].setText(strData4);
                tvSC[strData1 - 1][strData2 - 1].setText(strData3);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onResume() {
        super.onResume();
        // 앱이 실행될때 NFC 어댑터를 활성화 한다
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters,
                    mNFCTechLists);
        }
    }

    public void onPause() {
        super.onPause();
        // 앱이 종료될때 NFC 어댑터를 비활성화 한다
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    // NFC 태그 정보 수신 함수. 인텐트에 포함된 정보를 분석해서 화면에 표시
    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }
    void processIntent(Intent intent) {
//        readResult = (TextView) findViewById(R.id.readResult);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
//        readResult.append("\n\n" + new String(msg.getRecords()[0].getPayload()));
        readResult = new String(msg.getRecords()[0].getPayload());

//        Log.e("수신된 값!", readResult);
//        Log.e("비교 비교", readResult+"       "+roomNum);

        if (readResult.equals("enSuccess")) {

            ScheduleServer scheduleServer = new ScheduleServer(userID, roomNum, strType + 1, m_Handler);
            scheduleServer.start();
            if (strType == 2) {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
                alt_bld.setMessage(roomNum + "이 열렸습니다.").setCancelable(
                        false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Action for 'Yes' Button
//                                strType = 3;
//                                DoorServer doorServer1 = new DoorServer(userID, roomNum, strType, m_Handler);
//                                doorServer1.start();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                // Title for AlertDialog
                alert.setTitle("A.N.D");
                // Icon for AlertDialog
                alert.setIcon(R.drawable.icon);
                alert.show();
            } else {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
                alt_bld.setMessage(roomNum + "이 닫혔습니다.").setCancelable(
                        false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Action for 'Yes' Button
//                                strType = 5;
//
//                                DoorServer doorServer2 = new DoorServer(userID, roomNum, strType, m_Handler);
//                                doorServer2.start();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                // Title for AlertDialog
                alert.setTitle("A.N.D");
                // Icon for AlertDialog
                alert.setIcon(R.drawable.icon);
                alert.show();
            }
            strType = 0;
        } else {
            Toast.makeText(ScheduleActivity.this, "강의실이 맞나 확인해보세요.", Toast.LENGTH_SHORT).show();
            strType = 0;
        }
//        ScheduleServer scheduleSe = new ScheduleServer(userID, roomNum, strType, m_Handler);
//        scheduleSe.start();


    }

    // onNewIntent 메소드 수행 후 호출되는 메소드
    private void processTag(Intent passedIntent) {
        Parcelable[] rawMsgs = passedIntent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs == null) {
            return;
        }
        // 참고! rawMsgs.length : 스캔한 태그 개수
        Toast.makeText(getApplicationContext(), "스캔 성공!", Toast.LENGTH_SHORT).show();

        NdefMessage[] msgs;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                showTag(msgs[i]); // showTag 메소드 호출
            }
        }
    }

    // NFC 태그 정보를 읽어들이는 메소드
    private int showTag(NdefMessage mMessage) {
        List<ParsedRecord> records = NdefMessageParser.parse(mMessage);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            ParsedRecord record = records.get(i);

            int recordType = record.getType();
            String recordStr = ""; // NFC 태그로부터 읽어들인 텍스트 값
            if (recordType == ParsedRecord.TYPE_TEXT) {
                recordStr = "TEXT : " + ((TextRecord) record).getText();
            } else if (recordType == ParsedRecord.TYPE_URI) {
                recordStr = "URI : " + ((UriRecord) record).getUri().toString();
            }
            readResult = recordStr;


//            readResult = new String(recordStr.getRecords()[0].getPayload());
//            readResult.append(recordStr); // 읽어들인 텍스트 값을 TextView에 덧붙임
//            Toast.makeText(ScheduleActivity.this, "옿호홀롷", Toast.LENGTH_SHORT);
        }

        return size;
    }

    // NDEF 메시지를 화면에 출력
    public void showMsg(NdefMessage mMessage) {
        String strMsg = "", strRec = "";
        // NDEF 메시지에서 NDEF 레코드 배열을 구한다
        NdefRecord[] recs = mMessage.getRecords();
        for (int i = 0; i < recs.length; i++) {
            // 개별 레코드 데이터를 구한다
            NdefRecord record = recs[i];
            byte[] payload = record.getPayload();
            // 레코드 데이터 종류가 텍스트 일때
            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                // 버퍼 데이터를 인코딩 변환
                strRec = byteDecoding(payload);
                strMsg = strRec;
            }
            // 레코드 데이터 종류가 URI 일때
            else if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                strRec = new String(payload, 0, payload.length);
                strRec = "URI: " + strRec;
            }
            strMsg = (strRec);
        }

        strTemp = strMsg;
        //읽어드린 문자가 지정한 문자와 같은지 확인
        if (ocCheck == true) {
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
            alt_bld.setMessage(roomNum + "이 열렸습니다.").setCancelable(
                    false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Action for 'Yes' Button
                            strType = 3;
                            DoorServer doorServer1 = new DoorServer(userID, roomNum, strType, m_Handler);
                            doorServer1.start();
                        }
                    });
            AlertDialog alert = alt_bld.create();
            // Title for AlertDialog
            alert.setTitle("A.N.D");
            // Icon for AlertDialog
            alert.setIcon(R.drawable.icon);
            alert.show();
        } else {
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
            alt_bld.setMessage(strTemp + "이 닫혔습니다.").setCancelable(
                    false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Action for 'Yes' Button
                            strType = 5;

                            DoorServer doorServer2 = new DoorServer(userID, roomNum, strType, m_Handler);
                            doorServer2.start();
                        }
                    });
            AlertDialog alert = alt_bld.create();
            // Title for AlertDialog
            alert.setTitle("A.N.D");
            // Icon for AlertDialog
            alert.setIcon(R.drawable.icon);
            alert.show();
        }
    }


    // 버퍼 데이터를 디코딩해서 String 으로 변환
    public String byteDecoding(byte[] buf) {
        String strText = "";
        String textEncoding = ((buf[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langCodeLen = buf[0] & 0077;

        try {
            strText = new String(buf, langCodeLen + 1,
                    buf.length - langCodeLen - 1, textEncoding);
        } catch (Exception e) {
            Log.d("tag1", e.toString());
        }
        return strText;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Schedule Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.rhrbg.doorlock/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Schedule Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.rhrbg.doorlock/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_read, container, false);
            return rootView;
        }
    }

    Handler m_Handler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case ScheduleServer.DoorState:
                    /**현재 문이 닫혀있습니까?*/
                    if ((int) msg.obj == ScheduleServer.DoorClose) {
                        /**
                         * 닫혀있는 상태에서 문을 열려고 요청을 하면
                         * 라즈베리 파이에 기록되어있는 NFC Key값과 매칭을 시작한다
                         * 만약 NFC Key 값이 서로 맞다면 아래 다이얼로그를 실행시킨다
                         */
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ScheduleActivity.this);
                        dialog.setMessage("허가된 사용자 입니다. 문을 여시겠습니까?").setCancelable(
                                true).setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                strType = 2;

                                ScheduleServer scheduleS1 = new ScheduleServer(userID, roomNum, strType, m_Handler);
                                scheduleS1.start();
                                ocCheck = true;
//                                // NFC 태그 스캔으로 앱이 자동 실행되었을때
//                                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
//                                    // 인텐트에 포함된 정보를 분석해서 화면에 표시
//                                    onNewIntent(getIntent());
//                                if (createNdefMessage(event).equals(getIntent().getAction())) {
//                                    onNewIntent(getIntent());
//
//                                }

                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = dialog.create();
                        // Title for AlertDialog
                        alert.setTitle("A.N.D");
                        // Icon for AlertDialog
                        alert.setIcon(R.drawable.icon);
                        alert.show();
                        /**현재 문이 열려있습니까?*/
                    } else if (msg.arg1 == ScheduleServer.DoorOpen) {
                        /**
                         * 열려있는 상태에서 문을 닫을려고 요청을 하면
                         * 라즈베리 파이에 기록되어있는 NFC Key값과 매칭을 시작한다
                         * 만약 NFC Key 값이 서로 맞다면 아래 다이얼로그를 실행시킨다
                         */
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(ScheduleActivity.this);
                        dialog1.setMessage("허가된 사용자 입니다. 문을 닫으시겠습니까?").setCancelable(
                                true).setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        strType = 4;
                                        ScheduleServer scheduleS2 = new ScheduleServer(userID, roomNum, strType, m_Handler);
                                        scheduleS2.start();

                                    }
                                }).setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert1 = dialog1.create();
                        // Title for AlertDialog
                        alert1.setTitle("A.N.D");
                        // Icon for AlertDialog
                        alert1.setIcon(R.drawable.icon);
                        alert1.show();
                        /**
                         * 라즈베리 파이와 NFC Key값을 매칭할때 만약 서로 Key값이 다르면
                         * 아래 토스트알림을 띄워준다
                         */
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "이용가능 시간이 아닙니다", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;

                /**
                 * 핸드폰과 라즈베리 파이간의 NFC Key 값이 서로 맞는지 확인하는 부분
                 * Key: 도어락 비밀번호
                 */
                case ScheduleServer.DoorGetKey:
                    //문 열고 닫을 때 키받는 부분 (NFC 보내기 추가해야됨
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    //해당 도어락 비밀번호

                    String strTemp = bundle.getString("getAES");
                    String strTemp2 = bundle.getString("getHASH");

                    Log.d("Test", strTemp + "     " + strTemp2);
                    strKeyData = strTemp;
                    strKeyData2 = strTemp2;
                    mNfcAdapter.setNdefPushMessageCallback(m_schedule, m_schedule);
                    Toast.makeText(getApplicationContext(), "도어락에 태그하세요!", Toast.LENGTH_SHORT).show();

                    break;
                /**
                 * 정상적으로 모든 작업이 수행되면 아래 토스트알림을 띄워준다
                 * 3 - 문이 닫혀있으면 열렸다고 수행한다
                 * 5 - 문이 열려있으면 닫혔다고 수행한다
                 */
                case ScheduleServer.DoorMoveSuccess:
                    if (msg.arg1 == 3) {
                        Toast.makeText(ScheduleActivity.this, roomNum + "이 열렸습니다", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == 5) {
                        Toast.makeText(ScheduleActivity.this, roomNum + "이 닫혔습니다", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    //접촉시 실행하는 엑티비티 메소드
    //NFC리드 켜기

    /**
     * NFC Write를 실행하기 위한 클래스
     * 위에 반드시 implements NfcAdapter.CreateNdefMessageCallback 가 선언 되어야 함
     * 서버에서 받아오는 Key값을 받아 Write한다
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.heart);
        // stream = new ByteArrayOutputStream();
        //bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
        //String strTemp = "C4141414";
//        strKeyData2 += userID;
        byte[] byteArray = strKeyData.getBytes(); //aes
//        byte[] byteArray2 = strKeyData2.getBytes(); //hash
//        byte[] byteArray3 = userID.getBytes();
        //byte[] byteArray = strTemp.getBytes();
        NdefMessage img = new NdefMessage(NdefRecord.createMime("AND", byteArray));

        return img;
    }

    /**
     * NFC Write가 성공하면 실행되는 클래스
     * Write가 끝나면 Key값을 초기화 하고 타입을 0으로 초기화 하고 NFC Write기능을 초기화한다
     */
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        strKeyData = "";
        strKeyData2 = "";
        mNfcAdapter.setNdefPushMessageCallback(null, this);
    }
}
