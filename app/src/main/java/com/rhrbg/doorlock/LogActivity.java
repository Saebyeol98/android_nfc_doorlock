package com.rhrbg.doorlock;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcF;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;

public class LogActivity extends AppCompatActivity implements LogListViewAdapter.ListClickListener, NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    IntentFilter[] mIntentFilters;
    String[][] mNFCTechLists;
    String readResult;

    LogActivity m_log;

    ListView listView;
    LogListViewAdapter adapter;
    String strKeyData, strKeyData2;

    //전역 변수..................
    protected static String m_userid, m_year, m_month, m_date, m_lecroom;
    int m_type = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // Adapter 생성
        adapter = new LogListViewAdapter(LogActivity.this);

        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.list01);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        m_userid = intent.getStringExtra("ID");

        Log.d("아이디", m_userid);

        LogServer ls = new LogServer(m_Handler, m_userid, m_type);
        ls.start();


        // 아이템 추가.

        // NFC 객체생성

        // NFC 어댑터를 구한다
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent targetIntent = new Intent(this, LogActivity.class);
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
        m_log = this;
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {

        }
    }

    @Override
    public void onListClickListener(int position) {
        int pos = position;

//        Toast.makeText(getApplicationContext(), "리스트뷰 클릭했당", Toast.LENGTH_SHORT).show();

        LogListViewItem item = new LogListViewItem();
        item = (LogListViewItem) adapter.getItem(pos);

        m_year = item.getYear();
        m_month = item.getMonth();
        m_date = item.getDate();
        m_lecroom = item.getLecRoom();

        m_type = 1;

        String test = m_year + " " + m_month + " " + m_date + " " + m_lecroom;

        Log.d("보낼 내용", test);

        LogServer ls = new LogServer(m_Handler, m_userid, m_lecroom, m_type);
        ls.start();
    }

    Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LogServer.SERVER_SUCCES:
                    if (LogServer.THREAD_CHECK == 1) {

                        Bundle bundle = new Bundle();
                        bundle = msg.getData();

                        adapter.addItem(bundle);
                        adapter.notifyDataSetChanged();

                        Log.d("불러온 내용", bundle.toString());
                    } else {
                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(LogActivity.this);
                        alt_bld.setMessage("받아올 내용이 없거나 불러오기를 실패하였습니다 다시 시도해주세요").setCancelable(
                                false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        AlertDialog alert = alt_bld.create();
                        // Title for AlertDialog
                        alert.setTitle("A.N.D");
                        // Icon for AlertDialog
                        alert.setIcon(R.drawable.icon);
                        alert.show();
//                        Toast.makeText(getApplicationContext(), "받아올 내용이 없거나 불러오기를 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case LogServer.DoorState:
                    /**현재 문이 닫혀있습니까?*/
                    if ((int)msg.obj == LogServer.DoorClose) {
                        /**
                         * 닫혀있는 상태에서 문을 열려고 요청을 하면
                         * 라즈베리 파이에 기록되어있는 NFC Key값과 매칭을 시작한다
                         * 만약 NFC Key 값이 서로 맞다면 아래 다이얼로그를 실행시킨다
                         */
                        AlertDialog.Builder dialog = new AlertDialog.Builder(LogActivity.this);
                        dialog.setMessage("허가된 사용자 입니다. 문을 여시겠습니까?").setCancelable(
                                true).setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                m_type = 2;

                                LogServer ls = new LogServer(m_Handler, m_userid, m_lecroom, m_type);
                                ls.start();

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
                    } else if ((int)msg.obj == LogServer.DoorOpen) {
                        /**
                         * 열려있는 상태에서 문을 닫을려고 요청을 하면
                         * 라즈베리 파이에 기록되어있는 NFC Key값과 매칭을 시작한다
                         * 만약 NFC Key 값이 서로 맞다면 아래 다이얼로그를 실행시킨다
                         */
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(LogActivity.this);
                        dialog1.setMessage("허가된 사용자 입니다. 문을 닫으시겠습니까?").setCancelable(
                                true).setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        m_type = 4;

                                        LogServer ls = new LogServer(m_Handler, m_userid, m_lecroom, m_type);
                                        ls.start();

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
                case LogServer.DoorGetKey:
                    //문 열고 닫을 때 키받는 부분 (NFC 보내기 추가해야됨
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    //해당 도어락 비밀번호

                    String strTemp = bundle.getString("getAES");
                    String strTemp2 = bundle.getString("getHASH");

                    Log.d("Test", strTemp + "     " + strTemp2);
                    strKeyData = strTemp;
                    strKeyData2 = strTemp2;
                    mNfcAdapter.setNdefPushMessageCallback(m_log, m_log);
                    Toast.makeText(getApplicationContext(), "도어락에 태그하세요!", Toast.LENGTH_SHORT).show();

                    break;
                /**
                 * 정상적으로 모든 작업이 수행되면 아래 토스트알림을 띄워준다
                 * 3 - 문이 닫혀있으면 열렸다고 수행한다
                 * 5 - 문이 열려있으면 닫혔다고 수행한다
                 */
                case LogServer.DoorMoveSuccess:
                    if (msg.arg1 == 3) {
                        Toast.makeText(LogActivity.this, m_lecroom + "이 열렸습니다", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == 5) {
                        Toast.makeText(LogActivity.this, m_lecroom + "이 닫혔습니다", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }
    };

    /**
     * 여기 아래다가 NFC작동 소스들 넣어줘
     * Nfc Write, Nfc Read    우우우웅
     */

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
    /*
     *  이부분 태그에 쓰는 부분이라 꼭 필요할거야 알아서 잘보고 넣어 if문 첫번째는 입력안하면 안쓰는거고 else if는 써주는거
        *
        */
//        //EditText에 입력된 값을 가져옴
//        String s = writetext.getText().toString();
//
//        Tag detectedTag = intent.getParcelableExtra(nfcAdapter.EXTRA_TAG);
//        //아무것도 입력받지 않으면 태그에 쓰지않음
//        if (s.equals("")) {
//            Toast.makeText(getApplicationContext(), "내용 입력안하냐", Toast.LENGTH_SHORT).show();
//        } else {
//            NdefMessage message = createTagMessage(s, TYPE_TEXT);
//            writeTag(message, detectedTag);
//        }
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

//    @Override
//    public NdefMessage createNdefMessage(NfcEvent event) {
//
//        String text = writetext.getText().toString();  // 쓸 단어인데 edittext가 없어서 오류나는건데 알아서 고쳐
//
//        //        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//
//        return null;
//    }


    /**
     * NFC Write를 실행하기 위한 클래스
     * 위에 반드시 implements NfcAdapter.CreateNdefMessageCallback 가 선언 되어야 함
     * 서버에서 받아오는 Key값을 받아 Write한다
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.heart);
        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
        //String strTemp = "C4141414";

//        strKeyData2 += strKeyData;
        byte[] byteArray = strKeyData.getBytes(); //aes
//        byte[] byteArray2 = strKeyData2.getBytes(); //hash
//        byte[] byteArray3 = userID.getBytes();
        //byte[] byteArray = strTemp.getBytes();
        NdefMessage img = new NdefMessage(NdefRecord.createMime("AND", byteArray));

        return img;
    }

    // 감지된 태그에 NdefMessage를 쓰는 메소드
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }

                if (ndef.getMaxSize() < size) {
                    return false;
                }

                ndef.writeNdefMessage(message);
                Toast.makeText(getApplicationContext(), "쓰기 성공!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "포맷되지 않은 태그이므로 먼저 포맷하고 데이터를 씁니다.",
                        Toast.LENGTH_SHORT).show();

                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    try {
                        formatable.connect();
                        formatable.format(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
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
