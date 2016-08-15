package com.asiatravel.atdownload.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jsion on 16/8/11.
 */

public class FileInfo implements Parcelable {
    private int id;
    private String fileName;
    private String url;
    private int length;
    private int finished;

    public FileInfo() {
    }

    public FileInfo(int id, String fileName, String url, int length, int finished) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", url='" + url + '\'' +
                ", length=" + length +
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
        dest.writeString(this.fileName);
        dest.writeString(this.url);
        dest.writeInt(this.length);
        dest.writeInt(this.finished);
    }

    protected FileInfo(Parcel in) {
        this.id = in.readInt();
        this.fileName = in.readString();
        this.url = in.readString();
        this.length = in.readInt();
        this.finished = in.readInt();
    }

    public static final Parcelable.Creator<FileInfo> CREATOR = new Parcelable.Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
