package com.practice.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyReceiver extends BroadcastReceiver {
    private IntentFilter intentFilter;

    public MyReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "android.provider.Telephony.SMS_RECEIVED":{
                Bundle bundle = intent.getExtras();

                Object[] objs = (Object[]) bundle.get("pdus");

                List<SmsMessage> messages = new ArrayList<>();
                for(Object obj : objs){
                    messages.add(SmsMessage.createFromPdu((byte[])obj));
                }

                for(SmsMessage message : messages) Log.d("onReceive", message.getMessageBody());
            }
        }
    }

    public IntentFilter getIntentFilter(){
        return intentFilter;
    }
}