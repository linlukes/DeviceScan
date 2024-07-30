package mg.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.ServiceEvent;

import tej.androidnetworktools.lib.Device;
import tej.androidnetworktools.lib.scanner.NetworkScanner;
import tej.androidnetworktools.lib.scanner.OnNetworkScanListener;
import tej.wifitoolslib.models.DeviceItem;

public class MdnsActivity extends AppCompatActivity implements MdnsServiceDiscovery.ServiceListener {

    private MdnsServiceDiscovery mdnsServiceDiscovery;
    private ArrayAdapter<String> discoveredDevicesAdapter;

    private List<Device> discoveredServicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdns_activity);

        // Initialize and set up the ListView
        discoveredServicesList = new ArrayList<>();
        ListView discoveredDevicesListView = findViewById(R.id.deviceListView);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesListView.setAdapter(discoveredDevicesAdapter);
        NetworkScanner.init(this);
        discoveredDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDeviceItemClick(position);
            }
        });
        NetworkScanner.scan(new OnNetworkScanListener() {
            @Override
            public void onComplete(List<Device> devices) {
                discoveredServicesList.clear();
                discoveredServicesList.addAll(devices);
                for (Device device : devices) {
                    String name = device.vendorName +"\n"+device.macAddress;
                    discoveredDevicesAdapter.add(name);
                    Log.d("device", device.hostname + "\n" + device.vendorName + "\n" + device.macAddress);
                }
            }

            @Override
            public void onFailed() {

            }
        });
        // Initialize MdnsServiceDiscovery with asynchronous initialization
       /* mdnsServiceDiscovery = new MdnsServiceDiscovery(new MdnsServiceDiscovery.InitializeListener() {
            @Override
            public void onInitializeComplete() {
                showToast("Initialization complete");
                // Start discovering services after initialization is complete
                mdnsServiceDiscovery.discoverServices();
            }
        });
        mdnsServiceDiscovery.setServiceListener(this);*/

        // Set up Start Button
        Button startButton = findViewById(R.id.scanButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Start Scanning clicked");
                discoveredDevicesAdapter.clear();
                NetworkScanner.scan(new OnNetworkScanListener() {
                    @Override
                    public void onComplete(List<Device> devices) {
                        discoveredServicesList.clear();
                        discoveredServicesList.addAll(devices);
                        for (Device device : devices) {
                            String name = device.vendorName +"\n"+device.macAddress;
                            discoveredDevicesAdapter.add(name);
                            Log.d("device", device.hostname + "\n" + device.vendorName + "\n" + device.macAddress);
                        }
                    }

                    @Override
                    public void onFailed() {

                    }
                });
                //mdnsServiceDiscovery.discoverServices();
            }
        });

        // Set up Stop Button
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Stop Scanning clicked");
               // mdnsServiceDiscovery.close();
                discoveredDevicesAdapter.clear();
            }
        });
    }

    private void onDeviceItemClick(int position) {
        Device device = discoveredServicesList.get(position);
        Toast.makeText(this, device.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_IP_ADDRESS,device.ipAddress);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_NAME, device.hostname);
        intent.putExtra(DeviceDetailActivity.EXTRA_MODEL_HARDWARE, device.vendorName);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_ADDRESS, device.macAddress);
        intent.putExtra(DeviceDetailActivity.EXTRA_MAC_ADDRESS, device.macAddress);
        intent.putExtra(DeviceDetailActivity.UID, device.vendorName);
        intent.putExtra(DeviceDetailActivity.ALIAS,device.vendorName);

        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mdnsServiceDiscovery != null) {
            mdnsServiceDiscovery.close();
            showToast("MdnsServiceDiscovery closed");
        }
    }

    @Override
    public void onServiceDiscovered(String serviceInfo) {
        showToast("Service Discovered: " + serviceInfo);
        // Update the UI with the discovered device
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                discoveredDevicesAdapter.add(serviceInfo);
            }
        });
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        // Handle service added event if needed
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        // Handle service removed event if needed
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        // Handle service resolved event if needed
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
