package com.rhrbg.doorlock;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by seabu on 2016-05-20.
 */
public class LogListViewAdapter extends BaseAdapter implements View.OnClickListener {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<LogListViewItem> listViewItemList = new ArrayList<LogListViewItem>();
    private ListClickListener listClickListener;

    // ListViewAdapter의 생성자
    public LogListViewAdapter(ListClickListener clickListener) {
        this.listClickListener = clickListener;
    }

    // 메소드............
    public interface ListClickListener {
        void onListClickListener(int position);
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.log_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        final TextView MonthDate = (TextView) convertView.findViewById(R.id.tv_1);
        final TextView startTime = (TextView) convertView.findViewById(R.id.tv_2);
        final TextView endTime = (TextView) convertView.findViewById(R.id.tv_3);
        final TextView lecRoom = (TextView) convertView.findViewById(R.id.tv_4);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final LogListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        MonthDate.setText(listViewItem.getMonth() + "월" + " " + listViewItem.getDate() + "일");
        startTime.setText(listViewItem.getStartTime());
        endTime.setText(listViewItem.getEndTime());
        lecRoom.setText(listViewItem.getLecRoom());

        MonthDate.setTag(pos);
        startTime.setTag(pos);
        endTime.setTag(pos);
        lecRoom.setTag(pos);

        MonthDate.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        lecRoom.setOnClickListener(this);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Bundle _bundle) {
        LogListViewItem item = new LogListViewItem();


        item.setYear(_bundle.getString("getYear"));
        item.setMonth(_bundle.getString("getMonth"));
        item.setDate(_bundle.getString("getDate"));
        item.setStartTime(_bundle.getString("getS_time"));
        item.setEndTime(_bundle.getString("getE_time"));
        item.setLecRoom(_bundle.getString("getLecroom"));

        listViewItemList.add(item);
    }

    @Override
    public void onClick(View v) {
        if (this.listClickListener != null) {
            this.listClickListener.onListClickListener((int) v.getTag());
        }
    }
}
