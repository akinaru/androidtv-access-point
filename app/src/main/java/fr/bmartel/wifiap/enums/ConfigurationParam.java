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
