/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
