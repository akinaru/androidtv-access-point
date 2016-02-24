package fr.bmartel.wifiap.fragment;

/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApPassword;
import fr.bmartel.wifiap.model.Constants;

/**
 * Displays a UI for text input in the "wizard" style.
 * TODO: Merge with EditTextFragment
 */
public class PasswordInputFragment extends Fragment {

    public interface Listener {
        /**
         * Called when text input is complete.
         *
         * @param text the text that was input.
         * @return true if the text is acceptable; false if not.
         */
        boolean onPasswordInputComplete(String text);
    }

    private static final String TAG = PasswordInputFragment.class.getSimpleName();

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_DESCRIPTION = "description";
    private static final String EXTRA_INPUT_TYPE = "input_type";
    private static final String EXTRA_PREFILL = "prefill";
    private static final String EXTRA_EDIT_SUFFIX = "edit_suffix";

    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
    }

    public static PasswordInputFragment newInstance(
            String title, String description, int inputType, String prefill) {
        return newInstance(title, description, inputType, prefill, null);
    }

    //TODO: Add a boolean parameter that controls whether the default is show or hide
    private static PasswordInputFragment newInstance(
            String title, String description, int inputType, String prefill, String editSuffix) {
        PasswordInputFragment fragment = new PasswordInputFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_DESCRIPTION, description);
        args.putInt(EXTRA_INPUT_TYPE, inputType);
        args.putString(EXTRA_PREFILL, prefill);
        args.putString(EXTRA_EDIT_SUFFIX, editSuffix);
        fragment.setArguments(args);
        return fragment;
    }

    private Handler mHandler;
    private EditText mTextInput;
    private CheckBox textObsufactionToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        mHandler = new Handler();

        final View view = inflater.inflate(R.layout.account_content_area, container, false);

        final ViewGroup descriptionArea = (ViewGroup) view.findViewById(R.id.description);
        final View content = inflater.inflate(R.layout.wifi_content, descriptionArea, false);
        descriptionArea.addView(content);

        final ViewGroup actionArea = (ViewGroup) view.findViewById(R.id.action);

        final View action = inflater.inflate(R.layout.password_text_input, container, false);
        actionArea.addView(action);

        TextView titleText = (TextView) content.findViewById(R.id.title_text);
        TextView descriptionText = (TextView) content.findViewById(R.id.description_text);

        mTextInput = (EditText) action.findViewById(R.id.text_input);
        textObsufactionToggle = (CheckBox) action.findViewById(R.id.text_obfuscation_toggle);

        TextView editSuffixText = (TextView) action.findViewById(R.id.edit_suffix_text);

        String title = getResources().getString(R.string.password_type);
        String description = getResources().getString(R.string.password_type2);
        int inputType = 1;
        String prefill = "";
        String editSuffix = "";


        if (title != null) {
            titleText.setText(title);
            titleText.setVisibility(View.VISIBLE);
        } else {
            titleText.setVisibility(View.GONE);
        }

        if (description != null) {
            descriptionText.setText(description);
            descriptionText.setVisibility(View.VISIBLE);
        } else {
            descriptionText.setVisibility(View.GONE);
        }


        if (editSuffix != null) {
            ViewGroup.LayoutParams params = mTextInput.getLayoutParams();
            params.width = getActivity()
                    .getResources().getDimensionPixelSize(R.dimen.edit_text_width_small);
            editSuffixText.setText(editSuffix);
            editSuffixText.setVisibility(View.VISIBLE);
        } else {
            editSuffixText.setVisibility(View.GONE);
        }

        textObsufactionToggle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean hidePassword = textObsufactionToggle.isChecked();
                if (hidePassword) {
                    mTextInput.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    mTextInput.setTransformationMethod(null);
                }
            }
        });

        textObsufactionToggle.setChecked(true);

        if (textObsufactionToggle.isChecked()) {
            mTextInput.setTransformationMethod(new PasswordTransformationMethod());
        }

        mTextInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                InputType.TYPE_CLASS_TEXT);

        if (prefill != null) {
            mTextInput.setText(prefill);
            mTextInput.setSelection(mTextInput.getText().length(), mTextInput.getText().length());
        }

        final IApPassword passwordWrapper = (IApPassword) getActivity();
        final IApCommon accessPointWrapper = (IApCommon) getActivity();

        mTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Constants.PASSWORD, mTextInput.getText().toString());
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
                                        getActivity().finish();
                                    }
                                });
                            }
                        }, Constants.TIMEOUT_AP_ACTIVATION);
                    } else {
                        getActivity().finish();
                    }
                    return false;
                }
                return true;  // If we don't return true on ACTION_DOWN, we don't get the ACTION_UP.
            }
        });

        mTextInput.setText(passwordWrapper.getPassword());
        mTextInput.setSelection(mTextInput.getText().length());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Activity a = getActivity();
                if (a != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) a.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(mTextInput, 0);
                    mTextInput.requestFocus();
                }
            }
        });
    }

    @Override
    public void onPause() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextInput.getWindowToken(), 0);
        super.onPause();
    }
}
