/*
 * Wifi Access Point configuration for Android TV
 *
 * Copyright (C) 2016 Bertrand Martel
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
package fr.bmartel.wifiap.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.bmartel.wifiap.R;
import fr.bmartel.wifiap.inter.IApCommon;
import fr.bmartel.wifiap.inter.IApPassword;
import fr.bmartel.wifiap.model.Constants;
import fr.bmartel.wifiap.singleton.ApSingleton;
import fr.bmartel.wifiap.util.RandomString;

/**
 * Password activity
 *
 * @author Bertrand Martel
 */
public class PasswordActivity extends FragmentActivity implements IApPassword, IApCommon {

    private final static String TAG = PasswordActivity.class.getSimpleName();

    private ApSingleton mSingleton;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        mSingleton = ApSingleton.getInstance(this);

        setContentView(R.layout.password_activity);
    }

    @Override
    public String getPassword() {
        if (mSingleton.getApControl().getWifiApConfiguration().preSharedKey != null && !mSingleton.getApControl().getWifiApConfiguration().preSharedKey.equals(""))
            return mSingleton.getApControl().getWifiApConfiguration().preSharedKey;
        else {
            String password = new RandomString(8).nextString();
            SharedPreferences.Editor editor = mSingleton.getSharedPref().edit();
            editor.putString(Constants.PASSWORD, password);
            editor.commit();
            return password;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSingleton.getScheduledActivation() != null)
            mSingleton.getScheduledActivation().cancel(true);
    }

    @Override
    public boolean getState() {
        return mSingleton.getState();
    }

    @Override
    public void setState(boolean state) {
        mSingleton.setState(state);
    }

    @Override
    public void waitForActivation(String message, Runnable task) {
        mSingleton.waitForActivation(message, task);
    }

}
