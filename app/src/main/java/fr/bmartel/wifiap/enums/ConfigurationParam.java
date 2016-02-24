package fr.bmartel.wifiap.enums;

/**
 * Created by akinaru on 22/02/16.
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
