package fr.bmartel.wifiap.fragment;

import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

import java.util.List;

import fr.bmartel.android.multievent.MultiEvent;
import fr.bmartel.android.multievent.listener.IConnectivityListener;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApWrapper;

/**
 * Created by iLab on 11/12/2015
 */
public class StatusFragment extends GuidedStepFragment {

    private static final String TAG = StatusFragment.class.getSimpleName();
    private static final int CHECK_SET_ID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiEvent eventManager = new MultiEvent(getActivity());
        eventManager.addConnectivityChangeListener(new IConnectivityListener() {

            @Override
            public void onWifiStateChange(NetworkInfo.State formerState, NetworkInfo.State newState) {
                Log.i(TAG,"[WIFI STATE CHANGE] from state " + formerState + " to " + newState);
            }

            @Override
            public void onEthernetStateChange(NetworkInfo.State formerState, NetworkInfo.State newState) {
                Log.i(TAG,"[ETHERNET STATE CHANGE] from state " + formerState + " to " + newState);
            }
        });

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
        addCheckedAction(actions, "Activé", false);
        addCheckedAction(actions, "Désactivé", false);
        refresh();
    }

    private void refresh() {
        IApWrapper wrapper = (IApWrapper) getActivity();

        if (wrapper.getState())
            getActions().get(0).setChecked(true);
        else
            getActions().get(1).setChecked(true);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        IApWrapper wrapper = (IApWrapper) getActivity();

        if (getSelectedActionPosition() == 0)
            wrapper.setState(true);
        else
            wrapper.setState(false);

        getFragmentManager().popBackStack();
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