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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApWrapper;

/**
 * Created by iLab on 11/12/2015
 */
public class ClientFragment extends GuidedStepFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private Map<String, String> mClientMap = new HashMap<>();

    private Map<String, GuidedAction> mKeyTable = new HashMap<>();

    private int count = 0;

    private ScheduledExecutorService scheduler;

    private ScheduledFuture<?> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        scheduler = Executors.newScheduledThreadPool(1);
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

        task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                final List<WifiApControl.Client> clientList = wrapper.getClientList();

                for (int i = 0; i < clientList.size(); i++) {

                    if (!mClientMap.containsKey(clientList.get(i).hwAddr)) {

                        mClientMap.put(clientList.get(i).hwAddr, clientList.get(i).ipAddr);

                        Log.i(TAG, "add client");
                        final WifiApControl.Client client = clientList.get(i);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count++;
                                GuidedAction action = addAction(actions, count, client.ipAddr, client.hwAddr);
                                mKeyTable.put(client.hwAddr, action);
                                setActions(actions);
                                getGuidedActionsStylist().getActionsGridView().getAdapter().notifyDataSetChanged();
                            }
                        });

                    }
                }

                Iterator it = mClientMap.entrySet().iterator();

                while (it.hasNext()) {
                    final Map.Entry<String, String> pair = (Map.Entry) it.next();

                    boolean found = false;

                    for (int i = 0; i < clientList.size(); i++) {
                        if (clientList.get(i).hwAddr.equals(pair.getKey()))
                            found = true;
                    }

                    if (!found) {
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
                        mClientMap.remove(pair.getKey());
                    }
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null) {
            task.cancel(true);
        }
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
