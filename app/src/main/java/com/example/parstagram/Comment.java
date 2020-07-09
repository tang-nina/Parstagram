package com.example.parstagram;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_COMMENT = "comment";
    public static final String KEY_COMMENTER = "commenter";
   public static final String KEY_CREATED = "createdAt";
    public static final String KEY_POST = "post";
    //public static final String KEY_ID = "objectId";

    public Comment(){}

    public Date getTimestamp(){ return getCreatedAt(); }

    public String getId(){ return getObjectId(); }

    public String getComment(){ return getString(KEY_COMMENT); }

    public void setComment(String comment){
        put(KEY_COMMENT, comment);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_COMMENTER);
    }

    public void setUser(ParseUser user){
        put(KEY_COMMENTER, user);
    }

    public Post getPost(){
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post){
        put(KEY_POST, post);
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



}
