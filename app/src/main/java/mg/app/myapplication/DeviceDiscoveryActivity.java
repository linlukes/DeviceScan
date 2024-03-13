package mg.app.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;

public class DeviceDiscoveryActivity extends AppCompatActivity {

    private SsdpClient ssdpClient;
    private List<SsdpService> discoveredDevices;
    private ArrayAdapter<SsdpService> deviceAdapter;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_activity);

        // Initialize SSDP client
        ssdpClient = SsdpClient.create();

        // Initialize ListView and adapter
        discoveredDevices = new ArrayList<>();
        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, discoveredDevices);
        ListView deviceListView = findViewById(R.id.deviceListView);
        deviceListView.setAdapter(deviceAdapter);

        // Set up Search EditText
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterDevices(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set up Scan and Stop buttons
        Button scanButton = findViewById(R.id.scanButton);
        Button stopButton = findViewById(R.id.stopButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDiscovery();
            }
        });
    }

    private void startDiscovery() {
        DiscoveryRequest all = DiscoveryRequest.discoverAll();
        ssdpClient.discoverServices(all, new DiscoveryListener() {
            @Override
            public void onServiceDiscovered(SsdpService service) {
                runOnUiThread(() -> {
                    String deviceName = service.getRemoteIp().getHostName(); // or any other information you need
                    Log.d("DeviceDiscovery", "Discovered device: " + deviceName);
                    // Add device to the list
                    discoveredDevices.add(service);
                    deviceAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                // Handle service announcements if needed
            }

            @Override
            public void onFailed(Exception ex) {

            }

            @Override
            public void onFailedAndIgnored(Exception ex) {
                DiscoveryListener.super.onFailedAndIgnored(ex);
            }
        });
    }

    private void stopDiscovery() {
        // Stop discovery using the provided client
        if (ssdpClient != null) {
            ssdpClient.stopDiscovery();
        }
    }

    private void filterDevices(String searchText) {
        List<SsdpService> filteredList = new ArrayList<>();
        for (SsdpService device : discoveredDevices) {
            if (device.getRemoteIp().getHostName().contains(searchText.toLowerCase())) {
                filteredList.add(device);
            }
        }
        deviceAdapter.clear();
        deviceAdapter.addAll(filteredList);
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop SSDP client when the activity is destroyed
        stopDiscovery();
    }
}
