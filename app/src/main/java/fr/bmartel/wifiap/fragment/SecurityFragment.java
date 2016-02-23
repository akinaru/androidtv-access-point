package fr.bmartel.wifiap.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.List;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.model.StorageModel;

/**
 * Created by iLab on 11/12/2015
 */
public class SecurityFragment extends GuidedStepFragment {

    private static final String TAG = StatusFragment.class.getSimpleName();
    private static final int CHECK_SET_ID = 1;

    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences(StorageModel.PREFERENCES, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getResources().getString(R.string.fragment_security_title);
        Drawable icon = getActivity().getDrawable(R.drawable.tether);

        return new GuidanceStylist.Guidance(title, null, null, icon);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        addCheckedAction(actions, "WPA PSK", false);
        addCheckedAction(actions, "WPA2 PSK", false);
        addCheckedAction(actions, "NONE", false);

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

        IApWrapper wrapper = (IApWrapper) getActivity();

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
        editor.putInt(StorageModel.SECURITY, security);
        editor.commit();

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