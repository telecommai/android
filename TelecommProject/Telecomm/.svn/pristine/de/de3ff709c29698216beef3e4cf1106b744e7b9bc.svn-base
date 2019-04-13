package com;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.efounder.mobilecomps.contacts.User;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class WeChatDBManagerTest {
    private static final String TAG = "WeChatDBManagerTest";

    @Test
    public void testinsertOrUpdateUser() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        WeChatDBManager weChatDBManager = new WeChatDBManager(appContext);

        User user = weChatDBManager.getOneUserById(192205);
        System.out.println(user.getNickName()+ "" +user.getAvatar());
    }
}