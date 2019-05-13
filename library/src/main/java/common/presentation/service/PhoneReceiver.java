package common.presentation.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class PhoneReceiver extends BroadcastReceiver {
    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    private static String TAG = "PhoneReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()!=null) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i(TAG, "Screen went OFF");
//                BaseApp.instance.stopService();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i(TAG, "Screen went ON");
//                BaseApp.instance.startService();
            }
        }
    }


}
