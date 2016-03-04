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

import java.util.List;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApWrapper;

/**
 * Information Fragment featuring IP/Mac address of the Access Point
 *
 * @author Bertrand Martel
 */
public class InformationFragment extends GuidedStepFragmentAbstr {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getResources().getString(R.string.information_title);
        String description = getResources().getString(R.string.information_description);
        Drawable icon = getActivity().getDrawable(R.drawable.tether);
        return new GuidanceStylist.Guidance(title, description, null, icon);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {

        IApWrapper wrapper = (IApWrapper) getActivity();

        addAction(actions, 0, getResources().getString(R.string.access_point_ipv4), wrapper.getIpv4Addr().replaceAll("/", ""));
        addAction(actions, 1, getResources().getString(R.string.access_point_mac), wrapper.getMacAddr());
    }
}
