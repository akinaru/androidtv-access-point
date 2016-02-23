package fr.bmartel.wifiap.inter;

import fr.bmartel.wifiap.enums.Security;

/**
 * Created by akinaru on 23/02/16.
 */
public interface IApWrapper {

    boolean getState();

    String getName();

    Security getSecurity();

    void setState(boolean state);
}
