package fr.bmartel.wifiap.inter;

import java.util.Map;

import fr.bmartel.wifiap.enums.Security;
import fr.bmartel.wifiap.listener.IClientListener;

/**
 * Created by akinaru on 23/02/16.
 */
public interface IApWrapper {

    String getName();

    Security getSecurity();

    String getIpv4Addr();

    String getIpv6Addr();

    String getMacAddr();

    Map<String, String> getClientMap();

    void setClientListener(IClientListener listener);

    void restartRequestClient();
}
