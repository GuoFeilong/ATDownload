package com.asiatravel.atdownload.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jsion on 16/8/11.
 */

public class ThreadInfo implements Parcelable {
    private int id;
    private String url;
    private int start;
    private int end;
    private int finished;

    public ThreadInfo(int id, String url, int start, int end, int finished) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public ThreadInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", finished=" + finished +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeInt(this.start);
        dest.writeInt(this.end);
        dest.writeInt(this.finished);
    }

    protected ThreadInfo(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.start = in.readInt();
        this.end = in.readInt();
        this.finished = in.readInt();
    }

    public static final Parcelable.Creator<ThreadInfo> CREATOR = new Parcelable.Creator<ThreadInfo>() {
        @Override
        public ThreadInfo createFromParcel(Parcel source) {
            return new ThreadInfo(source);
        }

        @Override
        public ThreadInfo[] newArray(int size) {
            return new ThreadInfo[size];
        }
    };
}
