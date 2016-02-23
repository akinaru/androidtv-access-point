package fr.bmartel.wifiap.model;

import fr.bmartel.wifiap.enums.Security;

/**
 * Created by akinaru on 23/02/16.
 */
public class StorageModel {

    public final static Security DEFAULT_SECURITY = Security.WPA2_PSK;
    public final static String DEFAULT_SSID = "AccessPoint";
    public final static String DEFAULT_PASSWORD = "changeme";

    public final static String PREFERENCES = "preferences";

    public final static String PASSWORD = "password";

    public final static String SSID = "ssid";

    public final static String SECURITY = "security";
}