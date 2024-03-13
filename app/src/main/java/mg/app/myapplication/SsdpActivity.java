package mg.app.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mg.app.myapplication.SSDPServiceDiscovery;
import tej.wifitoolslib.DevicesFinder;
import tej.wifitoolslib.interfaces.OnDeviceFindListener;
import tej.wifitoolslib.models.DeviceItem;
import tej.wifitoolslib.vendors.VendorInfo;

public class SsdpActivity extends AppCompatActivity {

    private SSDPServiceDiscovery ssdpServiceDiscovery;
    private List<DeviceItem> discoveredServicesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ssdp_activity);

        // Initialize the list and adapter
        discoveredServicesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Set up the ListView
        ListView serviceListView = findViewById(R.id.deviceListView);
        serviceListView.setAdapter(adapter);

        // Initialize the SSDPServiceDiscovery with a callback
       /* ssdpServiceDiscovery = new SSDPServiceDiscovery(this, new SSDPServiceDiscovery.DiscoveryCallback() {
            @Override
            public void onServiceDiscovered(List<String> services) {
                // Update the list and notify the adapter when services are discovered
                updateDiscoveredServicesList(services);
            }

            @Override
            public void onDiscoveryStopped() {
                // Handle any actions when service discovery is stopped
            }
        });
*/
        Button startDiscoveryButton = findViewById(R.id.scanButton);
        Button stopDiscoveryButton = findViewById(R.id.stopButton);
        VendorInfo.init(this);
        DevicesFinder devicesFinder = new DevicesFinder(this, new OnDeviceFindListener() {
            @Override
            public void onStart() {
                Log.d("SsdpActivity", "onStart: ");
            }

            @Override
            public void onDeviceFound(DeviceItem deviceItem) {
                Log.d("SsdpActivity", "onDeviceFound: "+deviceItem);
            }

            @Override
            public void onComplete(List<DeviceItem> deviceItems) {
                discoveredServicesList.clear();
                discoveredServicesList.addAll(deviceItems);
                for (DeviceItem device: deviceItems){

                    String vendorName = device.getVendorName()+"\n"+device.getMacAddress();
                    // Populate the list with service names

                    Log.d("SsdpActivity", "onComplete: "+vendorName);
                    // Notify the adapter that the data set has changed
                    adapter.add(vendorName);
                }

            }

            @Override
            public void onFailed(int errorCode) {
                Log.d("SsdpActivity", "onFailed: "+errorCode);
            }
        });

        startDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start service discovery
               // ssdpServiceDiscovery.discoverServices();
                devicesFinder.setTimeout(3000).start();
            }
        });

        stopDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop service discovery
              //  ssdpServiceDiscovery.stopDiscovery();

            }
        });

        devicesFinder.setTimeout(5000).start();

        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDeviceItemClick(position);
            }
        });
    }

    private void onDeviceItemClick(int position) {
        DeviceItem device = discoveredServicesList.get(position);
        Toast.makeText(this, device.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_IP_ADDRESS,device.getIpAddress());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_NAME, device.getVendorName());
        intent.putExtra(DeviceDetailActivity.EXTRA_OS_INFO, device.getVendorName());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_ADDRESS, device.getMacAddress());
        intent.putExtra(DeviceDetailActivity.EXTRA_MAC_ADDRESS, device.getMacAddress());
        intent.putExtra(DeviceDetailActivity.UID, device.getVendorName());
        intent.putExtra(DeviceDetailActivity.ALIAS,device.getVendorName());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop service discovery when the activity is destroyed
       // ssdpServiceDiscovery.stopDiscovery();
    }

    private void updateDiscoveredServicesList(final List<String> services) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Clear the current list
                discoveredServicesList.clear();

                // Populate the list with service names
             //   discoveredServicesList.addAll(services);

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            }
        });
    }

}
