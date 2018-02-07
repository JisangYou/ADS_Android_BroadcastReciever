package com.practice.broadcastreceiver;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    PermissionUtil pUtil;
    MyReceiver myReceiver;

    PermissionUtil.IPermissionGrant iGrant = new PermissionUtil.IPermissionGrant() {
        @Override
        public void run() {
            myReceiver = new MyReceiver();
        }

        @Override
        public void fail() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        pUtil = new PermissionUtil(100, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS});
        pUtil.check(this, iGrant);

    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(myReceiver, myReceiver.getIntentFilter());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);

        super.onDestroy();
    }
}
