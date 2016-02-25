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