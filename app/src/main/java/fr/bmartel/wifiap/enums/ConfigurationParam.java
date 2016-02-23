package fr.bmartel.wifiap.enums;

/**
 * Created by akinaru on 22/02/16.
 */
public enum ConfigurationParam {

    ACTIVATION(0),
    AP_NAME(1),
    SECURITY(2),
    PASSWORD(3),
    BACK(4);

    private int value = 0;

    private ConfigurationParam(int value) {
        this.value = value;
    }

    public static ConfigurationParam getConfig(int value) {

        switch (value) {
            case 0:
                return ACTIVATION;
            case 1:
                return AP_NAME;
            case 2:
                return SECURITY;
            case 3:
                return PASSWORD;
            case 4:
                return BACK;
        }
        return ACTIVATION;
    }
}
