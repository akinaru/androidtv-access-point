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
import java.util.Map;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.activity.PasswordActivity;
import fr.bmartel.wifiap.enums.ConfigurationParam;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.listener.IClientListener;
import fr.bmartel.wifiap.model.Constants;

/**
 * Main fragment featuring all AP subcategories
 *
 * @author Bertrand Martel
 */
public class SettingsFragment extends GuidedStepFragmentAbstr {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private SharedPreferences sharedpreferences;

    /**
     * store client map size to update adpater only when size change
     */
    private int mCurrentClientMapSize = 0;

    /**
     * store AP name to re-enable AP when user change its name
     */
    private String mCurrentApName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedpreferences = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
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

        if (!editTitle.toString().equals(mCurrentApName)) {

            mCurrentApName = editTitle.toString();

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
        addAction(actions, ConfigurationParam.CLIENTS.ordinal(), getResources().getString(R.string.settings_action_title_6), wrapper.getClientMap().size() + " " + getResources().getString(R.string.connected_clients));

        mCurrentClientMapSize = wrapper.getClientMap().size();
        mCurrentApName = wrapper.getName();
    }


    @Override
    public void onPause() {
        super.onPause();
        IApWrapper wrapper = (IApWrapper) getActivity();
        wrapper.setClientListener(null);
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

        if (accessPointWrapper.getState())
            state = getActivity().getResources().getString(R.string.access_point_active);

        getActions().get(0).setDescription(state);

        wrapper.setClientListener(new IClientListener() {
            @Override
            public void onListRequested(final Map<String, String> clientMap) {
                if (clientMap.size() != mCurrentClientMapSize) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActions().get(5).setDescription(clientMap.size() + " " + getResources().getString(R.string.connected_clients));
                            getGuidedActionsStylist().getActionsGridView().getAdapter().notifyDataSetChanged();
                            /*
                            if (!mFirstRequestList) {
                                if (mCurrentClientMapSize < clientMap.size())
                                    Toast.makeText(getActivity(), getResources().getString(R.string.new_client_connected_toast), Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity(), getResources().getString(R.string.client_disconnected_toast), Toast.LENGTH_SHORT).show();
                            } else {
                                mFirstRequestList = false;
                            }
                            */
                            mCurrentClientMapSize = clientMap.size();
                        }
                    });
                }
            }
        });
        wrapper.restartRequestClient();
    }
}