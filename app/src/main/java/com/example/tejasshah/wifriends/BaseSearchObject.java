package com.example.tejasshah.wifriends;


/**
 * Created by jay on 4/17/16.
 */
public class BaseSearchObject {

    private String friendName;
    private int img;
    private String  username;


    public BaseSearchObject(String friendName, int img,String userName){

        this.friendName= friendName;
        this.img = img;
        this.username = userName;

    }

    public String getFriendName(){
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
