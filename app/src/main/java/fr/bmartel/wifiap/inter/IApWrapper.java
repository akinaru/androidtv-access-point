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
