package com.example.parstagram.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATED = "createdAt";
    public static final String KEY_LIKES = "likes";

    public Post(){}

    public Date getTimestamp(){ return getCreatedAt(); }

    public String getDescription(){ return getString(KEY_DESCRIPTION); }

    public String getId(){ return getObjectId(); }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public int getLikes(){ return (int) getNumber(KEY_LIKES); }

    public void addLike(){
        System.out.println(getId());
        put(KEY_LIKES, getLikes()+1);
        saveInBackground();
    }

    public void subtractLike(){
        put(KEY_LIKES, getLikes()-1);
        saveInBackground();
    }

    public String formatLikes(){
        int likes = getLikes();
        if(likes == 0){
            return null;
        }else if(likes == 1){
            return "1 like";
        }else{
            return likes + " likes";
        }
    }

}
