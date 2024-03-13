package mg.app.myapplication;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WifiDirectActivity extends AppCompatActivity {
    String TAG = "WifiDirectActivity";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private ArrayAdapter<String> deviceListAdapter;
    String groupOwnerAddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);

        ListView deviceListView = findViewById(R.id.deviceListView);
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(deviceListAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDeviceItemClick(position);
            }
        });

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        receiver = new WiFiDirectBroadcastReceiver();
        registerReceiver(receiver,intentFilter);

        discoverPeers();

        Button scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                discoverPeers();
            }
        });
    }
    public void onDeviceItemClick(int position) {
        WifiP2pDevice device = deviceList.get(position);
        Toast.makeText(this, device.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_NAME, device.deviceName);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_ADDRESS, device.deviceAddress);
        intent.putExtra(DeviceDetailActivity.EXTRA_MAC_ADDRESS, device.deviceAddress);
        intent.putExtra(DeviceDetailActivity.EXTRA_IP_ADDRESS, groupOwnerAddress);
        intent.putExtra(DeviceDetailActivity.UID, device.deviceName);
        intent.putExtra(DeviceDetailActivity.ALIAS,device.deviceName);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @SuppressLint("MissingPermission")
    private void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Discovery has started successfully
                Toast.makeText(WifiDirectActivity.this, "Discovery started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                // Discovery failed
                Log.d(TAG, "onFailure: "+reasonCode);
                Toast.makeText(WifiDirectActivity.this, "Discovery failed: " + reasonCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Handle changes in the list of available peers

                    manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            deviceList.clear();
                            deviceList.addAll(peers.getDeviceList());
                            updateDeviceList();
                        }
                    });


            }
            if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Connection state has changed
                if (manager != null) {
                    WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                    if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                        // This device is the group owner (server)
                        groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                        Log.d(TAG, "Group Owner IP Address: " + groupOwnerAddress);
                    } else if (wifiP2pInfo.groupFormed) {
                        // This device is a client
                        WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                        String deviceMacAddress = device.deviceAddress;
                        Log.d(TAG, "Client MAC Address: " + deviceMacAddress);
                    }
                }
            }
        }
    }

    private void updateDeviceList() {
        deviceListAdapter.clear();
        for (WifiP2pDevice device : deviceList) {
            deviceListAdapter.add(device.deviceName + "\n" + device.deviceAddress);
        }
        deviceListAdapter.notifyDataSetChanged();
    }

    // Add other necessary lifecycle methods, such as onPause, onDestroy, etc.
}
