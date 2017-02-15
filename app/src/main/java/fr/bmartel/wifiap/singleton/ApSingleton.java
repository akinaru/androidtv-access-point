package fr.bmartel.wifiap.singleton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.activity.FragmentActivityAbstr;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.model.Constants;

/**
 * Created by akinaru on 15/02/17.
 */

public class ApSingleton {

    private static ApSingleton mInstance;

    private final static String TAG = FragmentActivityAbstr.class.getSimpleName();

    private Handler mHandler;

    /**
     * task looking for AP activation state
     */
    private ScheduledFuture<?> mScheduledActivation;

    /**
     * scheduled threadpool of size 1
     */
    private ScheduledExecutorService mScheduler;

    /**
     * Wifi Acces point manager
     */
    private WifiApControl mAPControl;

    /**
     * shared preferences instance
     */
    private SharedPreferences mSharedpreferences;

    /**
     * Wifi manager
     */
    private WifiManager mWifiManager;

    /**
     * counter for AP activation timeout control
     */
    private int mSchedulingCounter = 0;

    private Context mContext;

    public static ApSingleton getInstance(Context context) {
        if (mInstance == null) {
            return new ApSingleton(context);
        }
        return mInstance;
    }

    public ApSingleton(Context context) {
        mContext = context;
        mHandler = new Handler();
        mScheduler = Executors.newScheduledThreadPool(1);
        mSharedpreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mAPControl = WifiApControl.getInstance(context);
    }

    public boolean getState() {
        return mAPControl.isEnabled();
    }

    public void setState(boolean state) {

        if (mSharedpreferences == null)
            mSharedpreferences = mContext.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if ((WifiApControl.getInstance(mContext) != null) && state) {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = mSharedpreferences.getString(Constants.SSID, Constants.DEFAULT_SSID);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            String defaultKey = "";
            if (WifiApControl.getInstance(mContext).getWifiApConfiguration().preSharedKey != null)
                defaultKey = WifiApControl.getInstance(mContext).getWifiApConfiguration().preSharedKey;

            config.preSharedKey = mSharedpreferences.getString(Constants.PASSWORD, defaultKey);
            config.hiddenSSID = false;

            switch (Security.getSecurity(mSharedpreferences.getInt(Constants.SECURITY, Constants.DEFAULT_SECURITY.ordinal()))) {
                case WPA_PSK:
                    config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    break;
                case WPA2_PSK:
                    config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    config.allowedKeyManagement.set(4);
                    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    break;
            }

            Log.i(TAG, "enabling Wifi AP");
            mWifiManager.setWifiEnabled(false);
            WifiApControl.getInstance(mContext).setEnabled(config, true);
        } else {
            Log.i(TAG, "disabling Wifi AP");
            WifiApControl.getInstance(mContext).disable();
        }
    }

    public void waitForActivation(String message, final Runnable task) {

        final ProgressDialog progress = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.dialog_access_point_activation),
                message + "...", true);

        mSchedulingCounter = 0;


        mScheduledActivation = mScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                mSchedulingCounter++;
                if (getState()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });
                    if (task != null) {
                        task.run();
                    }
                    if (mScheduledActivation != null)
                        mScheduledActivation.cancel(true);
                    return;
                }
                if (mSchedulingCounter == 6) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (task != null) {
                        task.run();
                    }
                    if (mScheduledActivation != null)
                        mScheduledActivation.cancel(true);
                    return;
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public ScheduledExecutorService getScheduler() {
        return mScheduler;
    }

    public WifiApControl getApControl() {
        return mAPControl;
    }

    public SharedPreferences getSharedPref() {
        return mSharedpreferences;
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    public ScheduledFuture<?> getScheduledActivation() {
        return mScheduledActivation;
    }
}
