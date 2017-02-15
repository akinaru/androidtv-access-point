/*
 * Wifi Access Point configuration for Android TV
 *
 * Copyright (C) 2016 Bertrand Martel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.bmartel.wifiap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.fragment.SettingsFragment;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.listener.IClientListener;
import fr.bmartel.wifiap.model.Constants;
import fr.bmartel.wifiap.singleton.ApSingleton;

/**
 * Main activity
 *
 * @author Bertrand Martel
 */
public class WifiApActivity extends Activity implements IApWrapper, IApCommon {

    private final static String TAG = WifiApActivity.class.getSimpleName();

    /**
     * map of clients connecte to AP
     */
    private Map<String, String> mClientMap = new HashMap<>();

    /**
     * listener used by fragments to listen for connected clients
     */
    private IClientListener mListener;

    /**
     * task looking for connected clients
     */
    private ScheduledFuture<?> mTask;

    private ApSingleton mSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mSingleton = ApSingleton.getInstance(this);

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
    }

    /**
     * look for connected clients
     */
    private void starLookingForClient() {

        mTask = mSingleton.getScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mClientMap.clear();
                mSingleton.getApControl().getReachableClients(Constants.REACHABLE_CLIENT_TIMEOUT,
                        new WifiApControl.ReachableClientListener() {
                            public void onReachableClient(final WifiApControl.Client client) {
                                mClientMap.put(client.hwAddr, client.ipAddr);
                            }

                            public void onComplete() {

                                mSingleton.getApControl().getReachableClients(Constants.REACHABLE_CLIENT_TIMEOUT,
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
    public String getName() {
        return mSingleton.getSharedPref().getString(Constants.SSID, Constants.DEFAULT_SSID);
    }

    @Override
    public Security getSecurity() {
        return Security.getSecurity(mSingleton.getSharedPref().getInt(Constants.SECURITY, Constants.DEFAULT_SECURITY.ordinal()));
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
        if (mSingleton.getWifiManager() != null &&
                mSingleton.getWifiManager().getConnectionInfo() != null &&
                mSingleton.getWifiManager().getConnectionInfo().getMacAddress() != null)
            return mSingleton.getWifiManager().getConnectionInfo().getMacAddress();
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListener = null;
        if (mTask != null)
            mTask.cancel(true);
        if (mSingleton.getScheduledActivation() != null)
            mSingleton.getScheduledActivation().cancel(true);
    }

    @Override
    public boolean getState() {
        return mSingleton.getState();
    }

    @Override
    public void setState(boolean state) {
        mSingleton.setState(state);
    }

    @Override
    public void waitForActivation(String message, Runnable task) {
        mSingleton.waitForActivation(message, task);
    }
}