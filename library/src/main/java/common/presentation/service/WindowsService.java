package common.presentation.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by ray on 2017/4/13.
 */
public class WindowsService extends Service {
    private final String TAG = "WindowsService";
    private static final int GRAY_SERVICE_ID = 1;
    public static final String RELAODING_SERVICE="android.intent.relodingservice";
    public static final String RESTART_SERVICE="android.intent.restartservice";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        /**升级服务为隐形前台进程
         * 灰色保护
         * */
        Intent innerIntent = new Intent(this, GrayInnerService.class);
        startService(innerIntent);
        startForeground(GRAY_SERVICE_ID, getNotifacation());
//        FloatWindowManager.getIntance(this).init();
    }

    private Notification getNotifacation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "foreground";
            @SuppressLint("WrongConstant") NotificationChannel adChannel = new NotificationChannel(channelId, "前台消息", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(adChannel);
            }
            android.support.v4.app.NotificationCompat.Builder builder=new android.support.v4.app.NotificationCompat.Builder(this,channelId);
            return builder.build();
        }else {
            return new Notification();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        startForeground(GRAY_SERVICE_ID, getNotifacation());
        return Service.START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//        FloatWindowManager.getIntance(this).onDestory();
        Log.i(TAG, "onDestroy");
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class GrayInnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
