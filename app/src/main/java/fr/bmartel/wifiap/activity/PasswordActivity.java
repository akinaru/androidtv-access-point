package fr.bmartel.wifiap.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApPassword;
import fr.bmartel.wifiap.model.Constants;
import fr.bmartel.wifiap.util.RandomString;

/**
 * Created by akinaru on 23/02/16.
 */
public class PasswordActivity extends FragmentActivity implements IApPassword, IApCommon {

    private final static String TAG = PasswordActivity.class.getSimpleName();

    private WifiApControl mAPControl;

    private WifiManager mWifiManager;

    private SharedPreferences sharedpreferences;

    private int schedulingCounter = 0;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        mAPControl = WifiApControl.getInstance(this);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        setContentView(R.layout.password_activity);
    }

    @Override
    public String getPassword() {

        if (sharedpreferences == null)
            sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (mAPControl.getWifiApConfiguration().preSharedKey != null && !mAPControl.getWifiApConfiguration().preSharedKey.equals(""))
            return mAPControl.getWifiApConfiguration().preSharedKey;
        else {
            String password = new RandomString(8).nextString();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.PASSWORD, password);
            editor.commit();
            return password;
        }
    }

    @Override
    public boolean getState() {
        return mAPControl.isEnabled();
    }

    @Override
    public void setState(boolean state) {

        if (sharedpreferences == null)
            sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (state) {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = sharedpreferences.getString(Constants.SSID, Constants.DEFAULT_SSID);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            String defaultKey = "";
            if (WifiApControl.getInstance(this).getWifiApConfiguration().preSharedKey != null)
                defaultKey = WifiApControl.getInstance(this).getWifiApConfiguration().preSharedKey;

            config.preSharedKey = sharedpreferences.getString(Constants.PASSWORD, defaultKey);
            config.hiddenSSID = false;

            switch (Security.getSecurity(sharedpreferences.getInt(Constants.SECURITY, Constants.DEFAULT_SECURITY.ordinal()))) {
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
            WifiApControl.getInstance(this).setEnabled(config, true);
        } else {
            Log.i(TAG, "disabling Wifi AP");
            WifiApControl.getInstance(this).disable();
        }
    }

    @Override
    public void waitForActivation(String message, final Runnable task) {

        final ProgressDialog progress = ProgressDialog.show(this, getResources().getString(R.string.dialog_access_point_activation),
                message + "...", true);

        final Timer timer = new Timer();

        schedulingCounter = 0;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                schedulingCounter++;
                if (getState()) {
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });
                    if (task != null) {
                        task.run();
                    }
                    //getFragmentManager().popBackStack();
                }
                if (schedulingCounter == 6) {
                    timer.cancel();
                    return;
                }
            }
        }, 0, 500);
    }
}
