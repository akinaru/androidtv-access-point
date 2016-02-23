package fr.bmartel.wifiap.activity;

import android.app.Activity;
import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.fragment.SettingsFragment;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.model.StorageModel;

public class WifiApActivity extends Activity implements IApWrapper {

    private WifiManager mWifiManager;

    private WifiApControl mAPControl;

    private static final int REQUEST_WRITE_SETTINGS = 1;

    private List<WifiApControl.Client> mClientList = new ArrayList<>();

    private SharedPreferences sharedpreferences;

    private final static String TAG = WifiApActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(StorageModel.PREFERENCES, Context.MODE_PRIVATE);

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
        startControl();
    }

    public void enable() {
        mWifiManager.setWifiEnabled(false);
        mAPControl.enable();
        refresh();
    }

    private void startControl() {

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mAPControl = WifiApControl.getInstance(this);

        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    // ignored
                }
            }
        }.start();
    }

    private void refresh() {
        updateText();
        listClients(300);
    }

    private void updateText() {
        /*
        StringBuilder sb = new StringBuilder();

        if (!WifiApControl.isSupported()) {
            sb.append("Warning: Wifi AP mode not supported!\n");
            sb.append("You should get unknown or zero values below.\n");
            sb.append("If you don't, isSupported() is probably buggy!\n");
        }

        if (apControl == null) {
            sb.append("Something went wrong while trying to get AP control!\n");
            sb.append("Make sure to grant the app the WRITE_SETTINGS permission.");
            TextView tv = (TextView) findViewById(R.id.apinfo);
            tv.setText(sb.toString());
            return;
        }

        int state = apControl.getState();
        sb.append("State: ").append(stateString(state)).append('\n');

        boolean enabled = apControl.isEnabled();
        sb.append("Enabled: ").append(enabled ? "YES" : "NO").append('\n');

        WifiConfiguration config = apControl.getConfiguration();
        sb.append("WifiConfiguration:");
        if (config == null) {
            sb.append(" null\n");
        } else {
            sb.append("\n");
            sb.append("   SSID: \"").append(config.SSID).append("\"\n");
            sb.append("   preSharedKey: \"").append(config.preSharedKey).append("\"\n");
        }

        Inet4Address addr4 = apControl.getInet4Address();
        sb.append("Inet4Address: ");
        sb.append(addr4 == null ? "null" : addr4.toString()).append('\n');

        Inet6Address addr6 = apControl.getInet6Address();
        sb.append("Inet6Address: ");
        sb.append(addr6 == null ? "null" : addr6.toString()).append('\n');

        sb.append("MAC: ");
        sb.append(wifiManager.getConnectionInfo().getMacAddress()).append('\n');

        TextView tv = (TextView) findViewById(R.id.apinfo);
        tv.setText(sb.toString());
        */
    }

    private void listClients(int timeout) {
        if (mAPControl == null) {
            return;
        }
        /*
        List<WifiApControl.Client> clients = apControl.getReachableClients(timeout,
                new WifiApControl.ReachableClientListener() {
                    public void onReachableClient(final WifiApControl.Client client) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setReachable(client);
                            }
                        });
                    }

                    public void onComplete() {
                    }
                });

        clientList = clients;
        */
    }

    private static String stateString(int state) {
        switch (state) {
            case WifiApControl.STATE_FAILED:
                return "FAILED";
            case WifiApControl.STATE_DISABLED:
                return "DISABLED";
            case WifiApControl.STATE_DISABLING:
                return "DISABLING";
            case WifiApControl.STATE_ENABLED:
                return "ENABLED";
            case WifiApControl.STATE_ENABLING:
                return "ENABLING";
            default:
                return "UNKNOWN!";
        }
    }

    @Override
    public boolean getState() {
        return mAPControl.isEnabled();
    }

    @Override
    public String getName() {
        return sharedpreferences.getString(StorageModel.SSID, StorageModel.DEFAULT_SSID);
    }

    @Override
    public Security getSecurity() {
        return Security.getSecurity(sharedpreferences.getInt(StorageModel.SECURITY, StorageModel.DEFAULT_SECURITY.ordinal()));
    }

    @Override
    public void setState(boolean state) {

        if (state) {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = sharedpreferences.getString(StorageModel.SSID, StorageModel.DEFAULT_SSID);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.preSharedKey = sharedpreferences.getString(StorageModel.PASSWORD, StorageModel.DEFAULT_PASSWORD);
            config.hiddenSSID = false;

            switch (Security.getSecurity(sharedpreferences.getInt(StorageModel.SECURITY, StorageModel.DEFAULT_SECURITY.ordinal()))) {
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
            mAPControl.setEnabled(config, true);
        } else {
            Log.i(TAG, "disabling Wifi AP");
            mAPControl.disable();
        }

    }
}