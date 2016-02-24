package fr.bmartel.wifiap.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionEditText;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.activity.PasswordActivity;
import fr.bmartel.wifiap.enums.ConfigurationParam;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.model.Constants;

/**
 * Created by iLab on 11/12/2015
 */
public class SettingsFragment extends GuidedStepFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private WifiApControl apControl;

    private SharedPreferences sharedpreferences;

    private ScheduledExecutorService scheduler;

    private ScheduledFuture<?> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedpreferences = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        scheduler = Executors.newScheduledThreadPool(1);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onGuidedActionEdited(GuidedAction action) {
        super.onGuidedActionEdited(action);
        Log.i(TAG, "onGuidedActionEdited");
        CharSequence title = action.getTitle();
        CharSequence editTitle = action.getEditTitle();
        CharSequence description = action.getDescription();

        View v = getActionItemView(getSelectedActionPosition());
        GuidedActionEditText editText = (GuidedActionEditText) v.findViewById(R.id.guidedactions_item_title);
        TextView descriptionGa = (TextView) v.findViewById(R.id.guidedactions_item_description);
        descriptionGa.setText(editTitle);
        getActions().get(1).setDescription(editTitle);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.SSID, editTitle.toString());
        editor.commit();

        final IApCommon accessPointWrapper = (IApCommon) getActivity();

        if (accessPointWrapper.getState()) {
            Log.i(TAG, "restarting AP");
            accessPointWrapper.setState(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    accessPointWrapper.setState(true);
                    accessPointWrapper.waitForActivation(getResources().getString(R.string.restarting_access_point), null);
                }
            }, Constants.TIMEOUT_AP_ACTIVATION);
        }
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getResources().getString(R.string.settings_title);
        String description = getResources().getString(R.string.settings_description);
        Drawable icon = getActivity().getDrawable(R.drawable.tether);
        return new GuidanceStylist.Guidance(title, description, null, icon);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {

        final IApWrapper wrapper = (IApWrapper) getActivity();
        IApCommon accessPointWrapper = (IApCommon) getActivity();

        String state = getActivity().getResources().getString(R.string.access_point_inactive);

        if (accessPointWrapper.getState())
            state = getActivity().getResources().getString(R.string.access_point_active);

        addAction(actions, ConfigurationParam.STATE.ordinal(), getResources().getString(R.string.settings_action_title_1), state);
        addEditableAction(actions, ConfigurationParam.AP_NAME.ordinal(), getResources().getString(R.string.settings_action_title_2), wrapper.getName());

        String securityStr = getActivity().getResources().getString(R.string.security_none);
        switch (wrapper.getSecurity()) {
            case WPA2_PSK:
                securityStr = "WPA2 PSK";
                break;
            case WPA_PSK:
                securityStr = "WPA PSK";
                break;
        }
        addAction(actions, ConfigurationParam.SECURITY.ordinal(), getResources().getString(R.string.settings_action_title_3), securityStr);
        addAction(actions, ConfigurationParam.PASSWORD.ordinal(), getResources().getString(R.string.settings_action_title_4), getResources().getString(R.string.settings_action_description_4));
        addAction(actions, ConfigurationParam.INFORMATION.ordinal(), getResources().getString(R.string.settings_action_title_5), getResources().getString(R.string.settings_action_description_5));
        addAction(actions, ConfigurationParam.CLIENTS.ordinal(), getResources().getString(R.string.settings_action_title_6), wrapper.getClientList().size() + " " + getResources().getString(R.string.connected_clients));

        task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG,"iterate");
                        getActions().get(5).setDescription(wrapper.getClientList().size() + " " + getResources().getString(R.string.connected_clients));
                        getGuidedActionsStylist().getActionsGridView().getAdapter().notifyDataSetChanged();
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null) {
            task.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        FragmentManager fm = getFragmentManager();

        ConfigurationParam id = ConfigurationParam.getConfig((int) action.getId());

        switch (id) {
            case STATE:
                GuidedStepFragment.add(fm, new StatusFragment());
                break;
            case AP_NAME:
                break;
            case SECURITY:
                GuidedStepFragment.add(fm, new SecurityFragment());
                break;
            case PASSWORD:

                Intent intent = new Intent(getActivity(), PasswordActivity.class);
                startActivity(intent);
                break;
            case INFORMATION:
                GuidedStepFragment.add(fm, new InformationFragment());
                break;
            case CLIENTS:
                GuidedStepFragment.add(fm, new ClientFragment());
                break;
            default:
                break;
        }
    }

    private static void addAction(List<GuidedAction> actions, long id, String title, String desc) {
        actions.add(new GuidedAction.Builder()
                .id(id)
                .title(title)
                .description(desc)
                .build());
    }

    private static void addEditableAction(List<GuidedAction> actions, long id, String title, String desc) {
        actions.add(new GuidedAction.Builder()
                .id(id)
                .title(title)
                .editable(true)
                .editTitle(desc)
                .description(desc)
                .build());
    }

    @Override
    public void onResume() {
        super.onResume();

        IApCommon accessPointWrapper = (IApCommon) getActivity();
        IApWrapper wrapper = (IApWrapper) getActivity();

        String name = sharedpreferences.getString(Constants.SSID, Constants.DEFAULT_SSID);

        String securityStr = getActivity().getResources().getString(R.string.security_none);
        switch (wrapper.getSecurity()) {
            case WPA2_PSK:
                securityStr = "WPA2 PSK";
                break;
            case WPA_PSK:
                securityStr = "WPA PSK";
                break;
        }

        getActions().get(2).setDescription(securityStr);
        getActions().get(1).setDescription(name);
        String state = getActivity().getResources().getString(R.string.access_point_inactive);
        ;
        if (accessPointWrapper.getState())
            state = getActivity().getResources().getString(R.string.access_point_active);
        ;
        getActions().get(0).setDescription(state);
    }
}