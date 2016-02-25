/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Bertrand Martel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.wifiap.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.util.Log;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.fragment.SettingsFragment;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.listener.IClientListener;
import fr.bmartel.wifiap.model.Constants;

/**
 * Main activity
 *
 * @author Bertrand Martel
 */
public class WifiApActivity extends Activity implements IApWrapper, IApCommon {

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

    private final static String TAG = WifiApActivity.class.getSimpleName();

    /**
     * counter for AP activation timeout control
     */
    private int mSchedulingCounter = 0;

    /**
     * map of clients connecte to AP
     */
    private Map<String, String> mClientMap = new HashMap<>();

    /**
     * listener used by fragments to listen for connected clients
     */
    private IClientListener mListener;

    /**
     * scheduled threadpool of size 1
     */
    private ScheduledExecutorService mScheduler;

    /**
     * task looking for connected clients
     */
    private ScheduledFuture<?> mTask;

    /**
     * task looking for AP activation state
     */
    private ScheduledFuture<?> mScheduledActivation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mScheduler = Executors.newScheduledThreadPool(1);
        mSharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            GuidedStepFragment.addAsRoot(this, new SettingsFragment(), android.R.id.content);
        }
        init();
    }

    /**
     * initialize AP manager & WIfi manager
     */
    public void init() {

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("Allow reading/writing the system settings? Necessary to set up access points.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.e(TAG, "error starting permission intent", e);
                            }
                        }
                    }).show();
            return;
        }
        */

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mAPControl = WifiApControl.getInstance(this);
    }

    /**
     * look for connected clients
     */
    private void starLookingForClient() {

        mTask = mScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mClientMap.clear();
                mAPControl.getReachableClients(Constants.REACHABLE_CLIENT_TIMEOUT,
                        new WifiApControl.ReachableClientListener() {
                            public void onReachableClient(final WifiApControl.Client client) {
                                mClientMap.put(client.hwAddr, client.ipAddr);
                            }

                            public void onComplete() {

                                mAPControl.getReachableClients(Constants.REACHABLE_CLIENT_TIMEOUT,
                                        new WifiApControl.ReachableClientListener() {
                                            public void onReachableClient(final WifiApControl.Client client) {
                                                mClientMap.put(client.hwAddr, client.ipAddr);
                                            }

                                            public void onComplete() {
                                                if (mListener != null) {
                                                    mListener.onListRequested(mClientMap);
                                                }
                                            }
                                        });
                            }
                        });
            }
        }, 0, Constants.GET_REACHABLE_CLIENT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean getState() {
        if (WifiApControl.getInstance(this) != null)
            return WifiApControl.getInstance(this).isEnabled();
        return false;
    }

    @Override
    public String getName() {
        if (mSharedpreferences == null)
            mSharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        return mSharedpreferences.getString(Constants.SSID, Constants.DEFAULT_SSID);
    }

    @Override
    public Security getSecurity() {
        if (mSharedpreferences == null)
            mSharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        return Security.getSecurity(mSharedpreferences.getInt(Constants.SECURITY, Constants.DEFAULT_SECURITY.ordinal()));
    }

    @Override
    public String getIpv4Addr() {
        if (WifiApControl.getInstance(this) != null) {
            Inet4Address adress = WifiApControl.getInstance(this).getInet4Address();
            if (adress != null)
                return adress.toString();
        }
        return "";
    }

    @Override
    public String getIpv6Addr() {
        if (WifiApControl.getInstance(this) != null) {
            Inet6Address adress = WifiApControl.getInstance(this).getInet6Address();
            if (adress != null)
                return adress.toString();
        }
        return "";
    }

    @Override
    public String getMacAddr() {
        if (mWifiManager != null && mWifiManager.getConnectionInfo() != null && mWifiManager.getConnectionInfo().getMacAddress() != null)
            return mWifiManager.getConnectionInfo().getMacAddress();
        return "";
    }

    @Override
    public Map<String, String> getClientMap() {
        return mClientMap;
    }

    @Override
    public void setClientListener(IClientListener listener) {
        mListener = listener;
    }

    @Override
    public void restartRequestClient() {
        if (mTask != null)
            mTask.cancel(true);
        starLookingForClient();
    }

    @Override
    public void setState(boolean state) {

        if (mSharedpreferences == null)
            mSharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (WifiApControl.getInstance(this) != null) {
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
                            Toast.makeText(WifiApActivity.this, getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
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
        mListener = null;
        if (mTask != null)
            mTask.cancel(true);
        if (mScheduledActivation != null)
            mScheduledActivation.cancel(true);
    }
}