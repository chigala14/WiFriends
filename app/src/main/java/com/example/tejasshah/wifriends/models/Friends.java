package com.example.tejasshah.wifriends.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tejas Shah on 5/5/2016.
 */
public class Friends implements Parcelable {

    private String friendName;
    private int img;
    private String username;



    public static final Parcelable.Creator<Friends> CREATOR = new Parcelable.Creator<Friends>(){
        @Override
        public Friends createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public Friends[] newArray(int size) {
            return new Friends[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(friendName);
        dest.writeInt(img);
        dest.writeString(username);
    }

    public Friends(Parcel p){
        friendName = p.readString();
        img = p.readInt();
        username = p.readString();
    }

    public Friends(String FriendName,int Imageresource ,String UserName){
        this.friendName= FriendName;
        this.img = Imageresource;
        this.username = UserName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
