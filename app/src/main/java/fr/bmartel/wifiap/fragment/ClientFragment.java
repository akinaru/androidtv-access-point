package fr.bmartel.wifiap.fragment;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
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
 * Created by iLab on 11/12/2015
 */
public class ClientFragment extends GuidedStepFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private Map<String, String> mClientMap = new HashMap<>();

    private Map<String, GuidedAction> mKeyTable = new HashMap<>();

    private int mCount = 0;

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

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
    }

    private GuidedAction addAction(List<GuidedAction> actions, long id, String title, String desc) {

        GuidedAction action = new GuidedAction.Builder()
                .id(id)
                .title(title)
                .description(desc)
                .build();

        actions.add(action);

        return action;
    }

}
