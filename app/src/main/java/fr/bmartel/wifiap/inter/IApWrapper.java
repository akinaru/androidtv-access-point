package fr.bmartel.wifiap.inter;

import java.util.List;

import cc.mvdan.accesspoint.WifiApControl;
import fr.bmartel.wifiap.enums.Security;

/**
 * Created by akinaru on 23/02/16.
 */
public interface IApWrapper {

    String getName();

    Security getSecurity();

    String getIpv4Addr();

    String getIpv6Addr();

    String getMacAddr();

    List<WifiApControl.Client> getClientList();
}
