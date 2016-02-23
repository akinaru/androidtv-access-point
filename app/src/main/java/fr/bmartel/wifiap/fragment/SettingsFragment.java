package fr.bmartel.wifiap.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionEditText;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.activity.PasswordActivity;
import fr.bmartel.wifiap.enums.ConfigurationParam;
import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.inter.IApWrapper;
import fr.bmartel.wifiap.model.StorageModel;

/**
 * Created by iLab on 11/12/2015
 */
public class SettingsFragment extends GuidedStepFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private WifiApControl apControl;

    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences(StorageModel.PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void onGuidedActionEdited(GuidedAction action) {
        super.onGuidedActionEdited(action);
        Log.i(TAG, "onGuidedActionEdited");
        CharSequence title = action.getTitle();
        CharSequence editTitle = action.getEditTitle();
        CharSequence description = action.getDescription();

        View v = getActionItemView(getSelectedActionPosition());
        GuidedActionEditText editText = (GuidedActionEditText) v.findViewById(R.id.guidedactions_item_title);
        TextView descriptionGa = (TextView) v.findViewById(R.id.guidedactions_item_description);
        descriptionGa.setText(editTitle);
        getActions().get(1).setDescription(editTitle);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(StorageModel.SSID, editTitle.toString());
        editor.commit();

        /*
        CharSequence textFromView = editText.getText();

        String s = "Editable action: " +
                "\ntitle: " + title +
                "\neditTitle: " + editTitle +
                "\ndescription: " + description +
                "\ntextFromView: " + textFromView;

        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        */
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getResources().getString(R.string.settings_title);
        String description = getResources().getString(R.string.settings_description);
        Drawable icon = getActivity().getDrawable(R.drawable.tether);
        return new GuidanceStylist.Guidance(title, description, null, icon);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {

        IApWrapper wrapper = (IApWrapper) getActivity();

        String state = "Inactive";

        if (wrapper.getState())
            state = "Active";

        addAction(actions, ConfigurationParam.ACTIVATION.ordinal(), getResources().getString(R.string.settings_action_title_1), state);
        addEditableAction(actions, ConfigurationParam.AP_NAME.ordinal(), getResources().getString(R.string.settings_action_title_2), wrapper.getName());
        addAction(actions, ConfigurationParam.SECURITY.ordinal(), getResources().getString(R.string.settings_action_title_3), wrapper.getSecurity().toString());
        addAction(actions, ConfigurationParam.PASSWORD.ordinal(), getResources().getString(R.string.settings_action_title_4), getResources().getString(R.string.settings_action_description_4));
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        Log.i(TAG, "click");

        final IApWrapper wrapper = (IApWrapper) getActivity();

        FragmentManager fm = getFragmentManager();

        ConfigurationParam id = ConfigurationParam.getConfig((int) action.getId());

        switch (id) {
            case ACTIVATION:
                GuidedStepFragment.add(fm, new StatusFragment());
                break;
            case AP_NAME:
                /*
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Access Point Name");
                alertDialog.setMessage("");
                alertDialog.setIcon(R.drawable.tethering);
                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(wrapper.getName());
                alertDialog.setView(input);
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            Log.i(TAG, "set name to " + v.getText().toString());
                            wrapper.setName(v.getText().toString());
                            getActions().get(1).setTitle(v.getText().toString());

                            alertDialog.dismiss();
                        }
                        return false;
                    }
                });
                alertDialog.show();
                */
                break;
            case SECURITY:
                GuidedStepFragment.add(fm, new SecurityFragment());
                break;
            case PASSWORD:

                Intent intent = new Intent(getActivity(), PasswordActivity.class);
                startActivity(intent);
                /*
                final AlertDialog alertDialogPass = new AlertDialog.Builder(getActivity()).create();
                alertDialogPass.setTitle("Access Point Password");
                alertDialogPass.setMessage("");
                alertDialogPass.setIcon(R.drawable.tethering);
                final EditText inputPass = new EditText(getActivity());
                LinearLayout.LayoutParams lpPass = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputPass.setLayoutParams(lpPass);
                inputPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                inputPass.setText(wrapper.getName());
                alertDialogPass.setView(inputPass);
                inputPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            wrapper.setPassword(v.getText().toString());
                            getActions().get(3).setDescription(v.getText().toString());
                            alertDialogPass.dismiss();
                        }
                        return false;
                    }
                });
                alertDialogPass.show();
                */
                break;
            case BACK:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    private static void addAction(List<GuidedAction> actions, long id, String title, String desc) {
        actions.add(new GuidedAction.Builder()
                .id(id)
                .title(title)
                .description(desc)
                .build());
    }

    private static void addEditableAction(List<GuidedAction> actions, long id, String title, String desc) {
        actions.add(new GuidedAction.Builder()
                .id(id)
                .title(title)
                .editable(true)
                .editTitle(desc)
                .description(desc)
                .build());
    }

    @Override
    public void onResume() {
        super.onResume();
        IApWrapper wrapper = (IApWrapper) getActivity();

        int security = sharedpreferences.getInt(StorageModel.SECURITY, StorageModel.DEFAULT_SECURITY.ordinal());
        String name = sharedpreferences.getString(StorageModel.SSID, StorageModel.DEFAULT_SSID);

        getActions().get(2).setDescription(Security.getSecurity(security).toString());
        getActions().get(1).setDescription(name);
        String state = "Inactive";
        if (wrapper.getState())
            state = "Active";
        getActions().get(0).setDescription(state);
    }
}