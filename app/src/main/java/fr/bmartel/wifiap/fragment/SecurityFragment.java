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
package fr.bmartel.wifiap.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

import java.util.List;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.model.Constants;

/**
 * Security Fragment featuring AP security level
 *
 * @author Bertrand Martel
 */
public class SecurityFragment extends GuidedStepFragmentAbstr {

    private static final String TAG = StatusFragment.class.getSimpleName();
    private static final int CHECK_SET_ID = 1;

    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getResources().getString(R.string.fragment_security_title);
        Drawable icon = getActivity().getDrawable(R.drawable.tether);

        return new GuidanceStylist.Guidance(title, null, null, icon);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        addCheckedAction(actions, "WPA PSK", false);
        addCheckedAction(actions, "WPA2 PSK", false);
        addCheckedAction(actions, getActivity().getResources().getString(R.string.security_none), false);

        refresh();
    }

    private void refresh() {
        IApWrapper wrapper = (IApWrapper) getActivity();

        switch (wrapper.getSecurity()) {
            case NONE:
                getActions().get(2).setChecked(true);
                break;
            case WPA2_PSK:
                getActions().get(1).setChecked(true);
                break;
            case WPA_PSK:
                getActions().get(0).setChecked(true);
                break;
        }
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        final IApCommon accessPointWrapper = (IApCommon) getActivity();

        int security = Security.NONE.ordinal();

        switch (getSelectedActionPosition()) {
            case 0:
                security = Security.WPA_PSK.ordinal();
                break;
            case 1:
                security = Security.WPA2_PSK.ordinal();
                break;
            case 2:
                security = Security.NONE.ordinal();
                break;
            default:
                break;
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(Constants.SECURITY, security);
        editor.commit();

        if (accessPointWrapper.getState()) {
            Log.i(TAG, "restarting AP");
            accessPointWrapper.setState(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    accessPointWrapper.setState(true);
                    accessPointWrapper.waitForActivation(getResources().getString(R.string.restarting_access_point), new Runnable() {
                        @Override
                        public void run() {
                            getFragmentManager().popBackStack();
                        }
                    });
                }
            }, Constants.TIMEOUT_AP_ACTIVATION);
        } else {
            getFragmentManager().popBackStack();
        }
    }
}