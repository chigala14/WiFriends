package com.example.tejasshah.wifriends.models;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tejas Shah on 4/16/2016.
 */
public class Networks implements Parcelable{

    private String ssid,pass,bssid,capability,ownerName;
    //private String bssid;
    //private String capability;
    private Integer level;



    /**
     * Parcelable creator. Do not modify this function.
     */
    public static final Parcelable.Creator<Networks> CREATOR = new Parcelable.Creator<Networks>() {
        public Networks createFromParcel(Parcel p) {
            return new Networks(p);
        }

        public Networks[] newArray(int size) {
            return new Networks[size];
        }
    };

    public Networks(Parcel p){
        ssid=p.readString();
        bssid=p.readString();
        capability=p.readString();
        level=p.readInt();
    }


    public Networks(String SSID, String BSSID,String Capability,Integer Level,String pass,String ownerName){
        this.ssid = SSID;
        this.bssid = BSSID;
        this.capability = Capability;
        this.level = Level;
        this.pass = pass;
        this.ownerName = ownerName;
    }

    public Networks(String SSID, String BSSID,String Capability,Integer Level){
        this.ssid = SSID;
        this.bssid = BSSID;
        this.capability = Capability;
        this.level = Level;

    }

    public Networks(String SSID,String pass,String userName){
        this.ssid = SSID;
        this.pass = pass;
        this.ownerName = userName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ssid);
        dest.writeString(bssid);
        dest.writeString(capability);
        dest.writeInt(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getSsid() {return ssid;}
    public String getBssid() {return bssid;}
    public String getCapability() {return capability;}
    public Integer getLevel(){ return level;}
    public String getPass() {
        return pass;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
