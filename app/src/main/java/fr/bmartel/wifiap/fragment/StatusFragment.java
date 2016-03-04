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
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.List;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApCommon;

/**
 * Fragment used to get/set Access Point state
 *
 * @author Bertrand Martel
 */
public class StatusFragment extends GuidedStepFragmentAbstr {

    private static final String TAG = StatusFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getResources().getString(R.string.fragment_status_title);
        Drawable icon = getActivity().getDrawable(R.drawable.tether);
        return new GuidanceStylist.Guidance(title, null, null, icon);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        addCheckedAction(actions, getActivity().getResources().getString(R.string.access_point_active), false);
        addCheckedAction(actions, getActivity().getResources().getString(R.string.access_point_inactive), false);
        setCheck();
    }

    private void setCheck() {
        IApCommon accessPointWrapper = (IApCommon) getActivity();

        if (accessPointWrapper.getState())
            getActions().get(0).setChecked(true);
        else
            getActions().get(1).setChecked(true);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        IApCommon accessPointWrapper = (IApCommon) getActivity();

        if (getSelectedActionPosition() == 0) {
            accessPointWrapper.setState(true);
            accessPointWrapper.waitForActivation(getActivity().getResources().getString(R.string.activating_access_point), new Runnable() {
                @Override
                public void run() {
                    getFragmentManager().popBackStack();
                }
            });
        } else {
            accessPointWrapper.setState(false);
            getFragmentManager().popBackStack();
        }
    }

}