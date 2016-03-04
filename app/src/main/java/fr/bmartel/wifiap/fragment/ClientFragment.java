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


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.listener.IClientListener;

/**
 * Client Fragment featuring client list and listening to change in reacheable client list
 *
 * @author Bertrand Martel
 */
public class ClientFragment extends GuidedStepFragmentAbstr {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    /**
     * map of connected clients with MacAddress as key / Ip Address as value
     */
    private Map<String, String> mClientMap = new HashMap<>();

    /**
     * map of Guided Action (values) associated with MacAddress (keys)
     */
    private Map<String, GuidedAction> mKeyTable = new HashMap<>();

    /**
     * counter for GuidedAction id
     */
    private int mCount = 0;

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getResources().getString(R.string.client_title);
        String description = getResources().getString(R.string.client_description);
        Drawable icon = getActivity().getDrawable(R.drawable.tether);
        return new GuidanceStylist.Guidance(title, description, null, icon);
    }

    @Override
    public void onCreateActions(@NonNull final List<GuidedAction> actions, Bundle savedInstanceState) {

        final IApWrapper wrapper = (IApWrapper) getActivity();

        wrapper.setClientListener(new IClientListener() {
            @Override
            public void onListRequested(Map<String, String> clientMap) {

                Iterator it = clientMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pair = (Map.Entry) it.next();

                    if (!mClientMap.containsKey(pair.getKey())) {

                        mClientMap.put(pair.getKey(), pair.getValue());

                        Log.i(TAG, "add client");
                        final String hwAddr = pair.getKey();
                        final String ipAddr = pair.getValue();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCount++;
                                GuidedAction action = addAction(actions, mCount, ipAddr, hwAddr);
                                mKeyTable.put(hwAddr, action);
                                setActions(actions);
                                getGuidedActionsStylist().getActionsGridView().getAdapter().notifyDataSetChanged();
                            }
                        });

                    }

                }

                Iterator it2 = mClientMap.entrySet().iterator();

                while (it2.hasNext()) {

                    final Map.Entry<String, String> pair = (Map.Entry) it2.next();

                    if (!clientMap.containsKey(pair.getKey())) {
                        Log.i(TAG, "removing client");
                        final GuidedAction action = mKeyTable.get(pair.getKey());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actions.remove(action);
                                setActions(actions);
                                getGuidedActionsStylist().getActionsGridView().getAdapter().notifyDataSetChanged();
                            }
                        });
                        it2.remove();
                    }
                }

            }
        });

        wrapper.restartRequestClient();
    }

    @Override
    public void onPause() {
        super.onPause();
        IApWrapper wrapper = (IApWrapper) getActivity();
        wrapper.setClientListener(null);
    }

}
