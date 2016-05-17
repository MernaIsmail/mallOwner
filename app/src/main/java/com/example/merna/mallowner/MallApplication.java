package com.example.merna.mallowner;

import com.firebase.client.Firebase;

/**
 * Created by Merna on 4/25/2016.
 */
public class MallApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
           /* Initialize Firebase */
        Firebase.setAndroidContext(this);
    }
}

