package fr.bmartel.wifiap.listener;

import java.util.Map;

import cc.mvdan.accesspoint.WifiApControl;

/**
 * Created by akinaru on 25/02/16.
 */
public interface IClientListener {

    void onListRequested(Map<String, String> clientMap);

}
