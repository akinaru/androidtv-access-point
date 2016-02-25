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
package fr.bmartel.wifiap.inter;

import java.util.Map;

import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.listener.IClientListener;

/**
 * Interface used from fragment to manage AP in activity
 *
 * @author Bertrand Martel
 */
public interface IApWrapper {

    /**
     * get AP name
     *
     * @return
     */
    String getName();

    /**
     * get security level for AP (WPA2 PSK / WPA PSK / None)
     *
     * @return
     */
    Security getSecurity();

    /**
     * get Ipv4 address of AP
     *
     * @return
     */
    String getIpv4Addr();

    /**
     * get Ipv6 address of AP
     *
     * @return
     */
    String getIpv6Addr();

    /**
     * get Mac Address of AP
     *
     * @return
     */
    String getMacAddr();

    /**
     * get map of connected clients
     *
     * @return
     */
    Map<String, String> getClientMap();

    /**
     * set listener for connected clients
     *
     * @param listener
     */
    void setClientListener(IClientListener listener);

    /**
     * restart task listening to connected clients (to be notified at once)
     */
    void restartRequestClient();
}
