package com.example.noushad.shopaholic.event;

import com.example.noushad.shopaholic.model.UserInfo;

/**
 * Created by noushad on 11/29/17.
 */

public class SignInEvent {
    UserInfo mUserInfo;
    public SignInEvent(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
