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
package fr.bmartel.wifiap.model;

import fr.bmartel.wifiap.enums.Security;

/**
 * Constants used for storage or timing
 *
 * @author Bertrand Martel
 */
public class Constants {

    /**
     * default AP security level used
     */
    public final static Security DEFAULT_SECURITY = Security.WPA2_PSK;

    /**
     * interval between each reachable client request
     */
    public final static int GET_REACHABLE_CLIENT_INTERVAL = 5000;

    /**
     * timeout for reach request for each client connected to AP
     */
    public final static int REACHABLE_CLIENT_TIMEOUT = 1000;

    /**
     * timeout for displaying error toast and considering AP activation failed
     */
    public final static int TIMEOUT_AP_ACTIVATION = 1000;

    /**
     * shared preferences ssid key
     */
    public final static String DEFAULT_SSID = "AccessPoint";

    /**
     * shared preferences preference name
     */
    public final static String PREFERENCES = "preferences";

    /**
     * shared preferences password key
     */
    public final static String PASSWORD = "password";

    /**
     * shared preference ssid key
     */
    public final static String SSID = "ssid";

    /**
     * shared preferences security key
     */
    public final static String SECURITY = "security";
}
