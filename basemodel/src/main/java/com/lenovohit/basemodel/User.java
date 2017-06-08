package com.lenovohit.basemodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuzhijun on 2017/6/8.
 */

public class User implements Parcelable {
    private String userName;
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.passWord);
    }

    public User(String userName,String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    protected User(Parcel in) {
        this.userName = in.readString();
        this.passWord = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
