package com.example.android.newsapp;

import android.graphics.Bitmap;

/**
 * {@NewsItem} represents a News item. It holds the details
 * of the news item such as title and author.
 */

public class NewsItem {
    private String mTitle;
    private String mSection;
    private String mAuthor;
    private String mDate;
    private String mTrailText;
    private String mUrl;
    private Bitmap mThumbnail;

    public NewsItem(String title, String section, String author, String date, String trailText, String url, Bitmap thumbnail) {
        mTitle = title;
        mSection = section;
        mAuthor = author;
        mDate = date;
        mTrailText = trailText;
        mUrl = url;
        mThumbnail = thumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getTrailText() {
        return mTrailText;
    }

    public String getUrl(){
        return mUrl;
    }

    public Bitmap getThumbnail(){
        return mThumbnail;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "mAuthor='" + mAuthor + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mSection='" + mSection + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mTrailText='" + mTrailText + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mThumbnail=" + mThumbnail +
                '}';
    }
}