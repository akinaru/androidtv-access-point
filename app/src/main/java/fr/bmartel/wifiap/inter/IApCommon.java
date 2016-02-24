package fr.bmartel.wifiap.inter;

/**
 * Created by akinaru on 24/02/16.
 */
public interface IApCommon {

    boolean getState();

    void setState(boolean state);

    void waitForActivation(String message, final Runnable task);
}
