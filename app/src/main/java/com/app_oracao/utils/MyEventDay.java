package com.app_oracao.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

public class MyEventDay extends EventDay implements Parcelable {

    private String mNote;
    private Calendar day;


    public MyEventDay(Calendar day, int imageResource, String mNotge){
        super(day, imageResource);
        this.mNote = mNotge;
    }

    public String getmNote() {
        return mNote;
    }

    public Calendar getDay() {
        return day;
    }

    protected MyEventDay(Parcel in) {
        super((Calendar) in.readSerializable(), in.readInt());
    }

    public static final Creator<MyEventDay> CREATOR = new Creator<MyEventDay>() {
        @Override
        public MyEventDay createFromParcel(Parcel in) {
            return new MyEventDay(in);
        }

        @Override
        public MyEventDay[] newArray(int size) {
            return new MyEventDay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(getCalendar());
        dest.writeInt(getImageResource());
        dest.writeString(mNote);
    }
}
