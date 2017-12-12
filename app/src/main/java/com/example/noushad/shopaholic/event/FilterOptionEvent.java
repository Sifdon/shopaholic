package com.example.noushad.shopaholic.event;

/**
 * Created by noushad on 12/11/17.
 */

public class FilterOptionEvent {
    private String mText;
    private String mTag;

    public FilterOptionEvent(String text, String tag) {
        mText = text;
        mTag = tag;
    }

    public String getText() {
        return mText;
    }

    public String getTag() {
        return mTag;
    }
}
