package com.example.student_dynamic_resource_allocation.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class SmsService extends BroadcastReceiver {
    private static final String CHANNEL_ID = "sms_alert_channel";

    private static final String TAG = "CustomSmsService";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for (SmsMessage message : messages) {
                String sender = message.getDisplayOriginatingAddress();
                String messageBody = message.getMessageBody();
                if (messageBody.equals("CALL_BACK")) {

                    Log.d(TAG, "onReceive: " + sender + " " + messageBody);

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sender));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                }

            }
        }
    }
}
