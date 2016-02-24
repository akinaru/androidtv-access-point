package fr.bmartel.wifiap.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.fragment.SettingsFragment;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.model.Constants;

public class WifiApActivity extends Activity implements IApWrapper, IApCommon {

    private WifiManager mWifiManager;

    private WifiApControl mAPControl;

    private static final int REQUEST_WRITE_SETTINGS = 1;

    private SharedPreferences sharedpreferences;

    private final static String TAG = WifiApActivity.class.getSimpleName();

    private int schedulingCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            GuidedStepFragment.addAsRoot(this, new SettingsFragment(), android.R.id.content);
        }
        init();
    }

    public void init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("Allow reading/writing the system settings? Necessary to set up access points.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
                        }
                    }).show();
            return;
        }

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mAPControl = WifiApControl.getInstance(this);
    }

    @Override
    public boolean getState() {
        return WifiApControl.getInstance(this).isEnabled();
    }

    @Override
    public String getName() {
        return sharedpreferences.getString(Constants.SSID, Constants.DEFAULT_SSID);
    }

    @Override
    public Security getSecurity() {
        return Security.getSecurity(sharedpreferences.getInt(Constants.SECURITY, Constants.DEFAULT_SECURITY.ordinal()));
    }

    @Override
    public String getIpv4Addr() {
        Inet4Address adress = WifiApControl.getInstance(this).getInet4Address();
        if (adress != null)
            return adress.toString();
        return "";
    }

    @Override
    public String getIpv6Addr() {
        Inet6Address adress = WifiApControl.getInstance(this).getInet6Address();
        if (adress != null)
            return adress.toString();
        return "";
    }

    @Override
    public String getMacAddr() {
        if (mWifiManager != null && mWifiManager.getConnectionInfo() != null && mWifiManager.getConnectionInfo().getMacAddress() != null)
            return mWifiManager.getConnectionInfo().getMacAddress();
        return "";
    }

    @Override
    public List<WifiApControl.Client> getClientList() {
        if (WifiApControl.getInstance(this).getClients() != null)
            return WifiApControl.getInstance(this).getClients();
        else {

            Log.i(TAG,"null");
            return new ArrayList<>();
        }
    }

    @Override
    public void setState(boolean state) {

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
                }
                if (schedulingCounter == 6) {
                    timer.cancel();
                    return;
                }
            }
        }, 0, 500);
    }
}