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
 * Created by iLab on 11/12/2015
 */
public class StatusFragment extends GuidedStepFragment {

    private static final String TAG = StatusFragment.class.getSimpleName();
    private static final int CHECK_SET_ID = 1;

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

    private static void addCheckedAction(List<GuidedAction> actions, String title, boolean checked) {
        GuidedAction guidedAction = new GuidedAction.Builder()
                .title(title)
                .checkSetId(CHECK_SET_ID)
                .build();
        guidedAction.setChecked(checked);
        actions.add(guidedAction);
    }
}