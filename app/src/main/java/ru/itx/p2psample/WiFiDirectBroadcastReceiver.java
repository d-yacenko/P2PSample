package ru.itx.p2psample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Collection;
import java.util.List;


//===================================================================================
// create by sample
// https://developer.android.com/guide/topics/connectivity/wifip2p.html
// https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html
//===================================================================================


/**
 * Created by d.yacenko on 19.03.17.
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private WifiP2pManager.PeerListListener myPeerListListener;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       final MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        myPeerListListener=new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                Collection<WifiP2pDevice> devices=wifiP2pDeviceList.getDeviceList();
                if(!activity.peers.equals(devices)) {
                    activity.peers.clear();
                    for (WifiP2pDevice d : devices) activity.peers.add(d);
                    Log.d("P2P", "onPeersAvailable (" + activity.peers.size() + ")");
                } else {
                    Log.d("P2P", "no devices found");
                }
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Toast.makeText(context, "Wifi P2P is enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Wi-Fi P2P is not enabled
                Toast.makeText(context, "Wifi P2P is not enabled", Toast.LENGTH_SHORT).show();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
             Toast.makeText(context, "WIFI_P2P_PEERS_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener);
                Log.d("P2P","set callback PeerListListener");
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
             Toast.makeText(context, "WIFI_P2P_CONNECTION_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
                Toast.makeText(context, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
        }else {
            // Unknown status
             Toast.makeText(context, "WIFI_P2P_UNKNOWN_MY_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
        }
    }

}

