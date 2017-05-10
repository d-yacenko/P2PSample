package ru.itx.p2psample;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers;
    boolean running;
    TextView peerList;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        peers = new LinkedList<WifiP2pDevice>();
        // register your application with the Wi-Fi P2P framework
        mManager = (WifiP2pManager) getSystemService(this.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        //Create an intent filter and add the same intents that our broadcast receiver check for
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        peerList = (TextView) findViewById(R.id.peerList);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);
        running = true;
        new MyAsyncTask().execute();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        running = false;
    }


    class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            while (running) {
                if (peers != null) {
                    String ls = "";
                    for (WifiP2pDevice s : peers) ls += s.deviceName + "\n";
                    final String finalLs = ls;
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                peerList.setText(finalLs);
//                                Log.d("P2P","TextView peerList set text: "+finalLs);
                            }catch (Exception e) {
                                Log.d("P2P","TextView peerList set text error"+e.getMessage());
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    // принудительный поиск
    public void clickFind(View view) {
        if (mManager == null) {Toast.makeText(MainActivity.this, "mManager is NULL!", Toast.LENGTH_SHORT).show();return;}
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "request P2P Find success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "request P2P Find fail", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
