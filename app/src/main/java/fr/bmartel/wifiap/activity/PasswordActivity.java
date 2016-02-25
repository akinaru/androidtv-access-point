/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.wifiap.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApPassword;
import fr.bmartel.wifiap.model.Constants;
import fr.bmartel.wifiap.util.RandomString;

/**
 * Password activity
 *
 * @author Bertrand Martel
 */
public class PasswordActivity extends FragmentActivity implements IApPassword, IApCommon {

    private final static String TAG = PasswordActivity.class.getSimpleName();

    /**
     * Wifi manager
     */
    private WifiManager mWifiManager;

    /**
     * Wifi Acces point manager
     */
    private WifiApControl mAPControl;

    /**
     * shared preferences instance
     */
    private SharedPreferences mSharedpreferences;

    /**
     * counter for AP activation timeout control
     */
    private int mSchedulingCounter = 0;

    /**
     * task looking for AP activation state
     */
    private ScheduledFuture<?> mScheduledActivation;

    /**
     * scheduled threadpool of size 1
     */
    private ScheduledExecutorService mScheduler;

    protected void onCreate(Bundle savedInstance) {

        mScheduler = Executors.newScheduledThreadPool(1);
        mSharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        super.onCreate(savedInstance);

        mAPControl = WifiApControl.getInstance(this);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        setContentView(R.layout.password_activity);
    }

    @Override
    public String getPassword() {

        if (mSharedpreferences == null)
            mSharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (mAPControl.getWifiApConfiguration().preSharedKey != null && !mAPControl.getWifiApConfiguration().preSharedKey.equals(""))
            return mAPControl.getWifiApConfiguration().preSharedKey;
        else {
            String password = new RandomString(8).nextString();
            SharedPreferences.Editor editor = mSharedpreferences.edit();
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

        if (mSharedpreferences == null)
            mSharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (state) {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = mSharedpreferences.getString(Constants.SSID, Constants.DEFAULT_SSID);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            String defaultKey = "";
            if (WifiApControl.getInstance(this).getWifiApConfiguration().preSharedKey != null)
                defaultKey = WifiApControl.getInstance(this).getWifiApConfiguration().preSharedKey;

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

        mSchedulingCounter = 0;


        mScheduledActivation = mScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                mSchedulingCounter++;
                if (getState()) {
                    runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(PasswordActivity.this, getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (mScheduledActivation != null)
                        mScheduledActivation.cancel(true);
                    return;
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScheduledActivation != null)
            mScheduledActivation.cancel(true);
    }
}
