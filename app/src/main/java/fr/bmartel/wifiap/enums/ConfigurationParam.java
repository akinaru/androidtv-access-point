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
package fr.bmartel.wifiap.enums;


/**
 * Enum for all main actions in setting fragment
 *
 * @author Bertrand Martel
 */
public enum ConfigurationParam {

    STATE(0),
    AP_NAME(1),
    SECURITY(2),
    PASSWORD(3),
    INFORMATION(4),
    CLIENTS(5);

    private int value = 0;

    private ConfigurationParam(int value) {
        this.value = value;
    }

    public static ConfigurationParam getConfig(int value) {

        switch (value) {
            case 0:
                return STATE;
            case 1:
                return AP_NAME;
            case 2:
                return SECURITY;
            case 3:
                return PASSWORD;
            case 4:
                return INFORMATION;
            case 5:
                return CLIENTS;
        }
        return STATE;
    }
}
