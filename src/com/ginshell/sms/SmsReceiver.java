package com.ginshell.sms;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MaTianyu
 * @date 14-7-23
 */
public class SmsReceiver extends BroadcastReceiver {
    //public static final  String template = "【bong智能手环】\n请转发以下内容至用户手机:18626469115 内容如下:651583(bong验证码)
    // 填写入验证码框，继续下一步。退订回复TD";
    public static final  String pattern = "bong智能手环[\\s\\S]*请转发以下内容至用户手机:(\\d{11}) 内容如下:(.+)退订回复TD";
    //public static final  String pattern = "【.*】(\\d{11}),(.+)";
    //public static final  String sender   = "15395839740";
    private static final String TAG     = SmsReceiver.class.getSimpleName();
    private DataBase db;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.i(TAG, "收到广播：" + intent.getAction());
            if (db == null) db = LiteOrm.via(context.getApplicationContext());
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null) {
                String msgBody = "";
                for (Object obj : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                    msgBody += sms.getMessageBody();
                }
                Log.i(TAG, "短信内容：" + msgBody);
                Matcher matcher = Pattern.compile(pattern).matcher(msgBody);
                if (matcher.find()) {
                    Message msg = new Message();
                    String phone = matcher.group(1);
                    String body = matcher.group(2);
                    if (phone != null && body != null) {
                        phone = phone.trim();
                        body = body.trim();
                        msg.phone = phone;
                        msg.body = body;
                        msg.time = new Date();
                        sendMsgToPhone(phone, body);
                        notification(context, phone, "[bong]验证码已转发至：" + phone, null);
                        db.save(msg);
                        //saveMsgToSystem(context, phone, body);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMsgToPhone(String phone, String msg) {
        Log.i(TAG, "发送手机：" + phone + " ,内容： " + msg);
        SmsManager manager = SmsManager.getDefault();
        List<String> texts = manager.divideMessage(msg);
        for (String txt : texts) {
            manager.sendTextMessage(phone, null, txt, null, null);
        }

    }

    public static void saveMsgToSystem(Context context, String phone, String msg) {
        ContentValues values = new ContentValues();
        values.put("date", System.currentTimeMillis());
        //阅读状态 
        values.put("read", 0);
        //1为收 2为发  
        values.put("type", 2);
        values.put("address", phone);
        values.put("body", msg);
        context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notification(Context context, String title, String msg, Uri uri) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_logo);

        builder.setContentTitle(title);
        builder.setTicker(msg);
        builder.setContentText(msg);

        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setLights(0xFFFFFF00, 0, 2000);
        builder.setVibrate(new long[]{
                0, 100, 300
        });
        builder.setAutoCancel(true);
        Intent intent = new Intent(context, MainActivity.class);
        //Log.i(TAG, "notiry uri :" + uri);
        //intent.putExtra(NOTIFY_TITLE_KEY, title);
        //intent.putExtra(NOTIFY_MSG_KEY, msg);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent
        //        .FLAG_ACTIVITY_CLEAR_TOP);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        Notification baseNF = builder.build();
        //发出状态栏通知
        //The first parameter is the unique ID for the Notification
        // and the second is the Notification object.
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nm.notify(App.index++, baseNF);
    }

}
