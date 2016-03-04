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
 * Enum for all security actions in security fragment
 *
 * @author Bertrand Martel
 */
public enum Security {
    WPA_PSK(0),
    WPA2_PSK(1),
    NONE(2);

    private int value = 0;

    private Security(int value) {
        this.value = value;
    }

    public static Security getSecurity(int value) {

        switch (value) {
            case 0:
                return WPA_PSK;
            case 1:
                return WPA2_PSK;
            case 2:
                return NONE;
        }
        return NONE;
    }
}
