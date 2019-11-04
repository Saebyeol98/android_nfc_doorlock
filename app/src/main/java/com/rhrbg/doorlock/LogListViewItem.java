package com.rhrbg.doorlock;

/**
 * Created by seabu on 2016-05-20.
 */
public class LogListViewItem {

    //받을 소스 선언(Private 선택자)
    private String userName;
    private String startTime;
    private String endTime;
    private String lecRoom;
    private String year;
    private String month;
    private String date;

    //set선언(public 선택자) > 매개변수 필요 반환타입은 void

    public void setUserName(String _userName) {
        userName = _userName;
    }

    public void setYear(String _year) {
        year = _year;
    }

    public void setMonth(String _month) {
        month = _month;
    }

    public void setDate(String _date) {
        date = _date;
    }

    public void setStartTime(String _startTime) {
        startTime = _startTime;
    }

    public void setEndTime(String _endTime) {
        endTime = _endTime;
    }

    public void setLecRoom(String _lecRoom) {
        lecRoom = _lecRoom;
    }

    //get선언(public 선택자) > 반환타입은 변수 선언한거랑 일치해야함

    public String getUserName() {
        return userName;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLecRoom() {
        return lecRoom;
    }
}
