package fr.bmartel.wifiap.enums;

/**
 * Created by akinaru on 23/02/16.
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
