package fr.bmartel.wifiap.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.List;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApWrapper;

/**
 * Created by iLab on 11/12/2015
 */
public class InformationFragment extends GuidedStepFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onGuidedActionEdited(GuidedAction action) {
        super.onGuidedActionEdited(action);
    }

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

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
    }

    private static void addAction(List<GuidedAction> actions, long id, String title, String desc) {
        actions.add(new GuidedAction.Builder()
                .id(id)
                .title(title)
                .description(desc)
                .build());
    }
}
