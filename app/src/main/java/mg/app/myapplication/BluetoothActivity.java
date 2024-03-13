package mg.app.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> deviceListAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_SYSTEM_FILES = 1001; // Choose any unique value
    private EditText filterEditText;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        bluetoothDevices = new ArrayList<>();

        filterEditText = findViewById(R.id.searchEditText);

        // Set a TextWatcher on the EditText for filtering
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list based on the user's input
                deviceListAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });

        // Check if the device supports Bluetooth
        if (!isBluetoothSupported()) {
            showToast("Bluetooth not supported on this device");
            finish();
            return;
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check if Bluetooth is enabled, if not, request user to enable it


        // Initialize ListView and ArrayAdapter for displaying devices
        ListView deviceListView = findViewById(R.id.deviceListView);
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDeviceItemClick(position);
            }
        });

        // Register BroadcastReceiver to listen for discovered devices
        registerBluetoothReceiver();

        // Check and request location permission if needed
        checkPermission();
    }

    // Helper method to check if Bluetooth is supported
    private boolean isBluetoothSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    // Helper method to check if Bluetooth is enabled
    private boolean isBluetoothEnabled() {

        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    // Helper method to request Bluetooth enable
    @SuppressLint("MissingPermission")
    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    // Helper method to show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper method to register Bluetooth BroadcastReceiver
    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    // Helper method to check location permission
    private void checkPermission() {
        // Request ACCESS_FINE_LOCATION for Bluetooth discovery
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_PERMISSION);
            return; // Don't proceed until permission is granted
        }

        // Permissions granted, proceed with further actions
       // showToast("Permissions granted. Starting Bluetooth discovery.");
        if (!isBluetoothEnabled()) {
            showToast("Bluetooth is not enabled. Requesting user to enable.");
            requestBluetoothEnable();
        } else {
            showToast("Bluetooth is enabled. Starting discovery.");
            startBluetoothDiscovery();
        }
    }

    // Add this method to check if your app needs READ_SYSTEM_ALERT_WINDOW
    private boolean isSystemPropertyAccessRequired() {
        // Replace with your specific logic to determine if you need to access a restricted system property
        // Consider checking the property name against a list of restricted ones

        String restrictedProperty = "ro.vendor.df.effect.conflict"; // Replace with your specific property name
        List<String> restrictedPropertyList = Arrays.asList("ro.property1", "ro.property2"); // Add more properties if needed

        return restrictedPropertyList.contains(restrictedProperty);
    }

    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery() {

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        if (deviceListAdapter != null) {
            if (!deviceListAdapter.isEmpty()) {
                deviceListAdapter.clear();
                deviceListAdapter.notifyDataSetChanged();
            }
        }
        // Start Bluetooth discovery
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.startDiscovery();

            Log.d("BroadcastReceiver", "startBluetoothDiscovery: " + bluetoothAdapter.isDiscovering());
         //   Toast.makeText(this, "Starting Bluetooth discovery.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Bluetooth is not enabled. Cannot start discovery.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the permission is granted
        // Permission granted, you can now access the system property
        // Permission denied, handle accordingly

        if (requestCode == REQUEST_FINE_LOCATION_PERMISSION || requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission not granted. Exiting.", Toast.LENGTH_SHORT).show();
                finish();
            }  // Permission granted, start Bluetooth discovery
            //  Toast.makeText(this, "Location permission granted. Starting Bluetooth discovery.", Toast.LENGTH_SHORT).show();
            //startBluetoothDiscovery();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver when the activity is destroyed
        unregisterReceiver(receiver);
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Inside the BroadcastReceiver's onReceive method
            Log.d("BroadcastReceiver", "Received Bluetooth action: " + action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                // Add the name and address to the array adapter to show in the ListView
                assert device != null;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String deviceInfo = device.getName() + "\n" + device.getAddress();
                deviceListAdapter.add(deviceInfo);
                Toast.makeText(context, "Device found: " + deviceInfo, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onScanButtonClick(View view) {
        startBluetoothDiscovery();
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public void onDeviceItemClick(int position) {
        BluetoothDevice bluetoothDevice = bluetoothDevices.get(position);
        Toast.makeText(this, bluetoothDevice.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_NAME, bluetoothDevice.getName());
        intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_ADDRESS, bluetoothDevice.getAddress());
        intent.putExtra(DeviceDetailActivity.EXTRA_MAC_ADDRESS, bluetoothDevice.getAddress());
        intent.putExtra(DeviceDetailActivity.UID, Arrays.toString(bluetoothDevice.getUuids()));
        intent.putExtra(DeviceDetailActivity.ALIAS,bluetoothDevice.getAlias());
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    public void onStopButtonClick(View view) {
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
    }

}
