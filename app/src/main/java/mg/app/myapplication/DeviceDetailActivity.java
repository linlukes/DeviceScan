package mg.app.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DEVICE_NAME = "device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String EXTRA_MAC_ADDRESS = "mac_address";
    public static final String EXTRA_IP_ADDRESS = "ip_address";
    public static final String EXTRA_OS_INFO = "os_info";
    public static final String EXTRA_MODEL_HARDWARE = "model_hardware";
    public static final String ALIAS = " - ";
    public static final String UID = " - ";

    @SuppressLint({"StringFormatInvalid", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        // Get the device information from the intent
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME) == null ? " - " : intent.getStringExtra(EXTRA_DEVICE_NAME);
        String deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS) == null ? "Unknown" :intent.getStringExtra(EXTRA_DEVICE_ADDRESS);
        String macAddress = intent.getStringExtra(EXTRA_MAC_ADDRESS) == null ? "Unknown" :intent.getStringExtra(EXTRA_MAC_ADDRESS);
        String ipAddress = intent.getStringExtra(EXTRA_IP_ADDRESS) == null ? "Unknown" :intent.getStringExtra(EXTRA_IP_ADDRESS);
        String osInfo = intent.getStringExtra(EXTRA_OS_INFO) == null ? "Unknown" :intent.getStringExtra(EXTRA_OS_INFO);
        String modelHardware = intent.getStringExtra(EXTRA_MODEL_HARDWARE) == null ? "Unknown" : intent.getStringExtra(EXTRA_MODEL_HARDWARE);
        String uid = intent.getStringExtra(UID)== null ? " - " :intent.getStringExtra(UID);
        String alias = intent.getStringExtra(ALIAS) ==null ? " - " : intent.getStringExtra(ALIAS) ;

        // Set the information in your layout elements
        TextView deviceNameTextView = findViewById(R.id.deviceNameTextView);
        deviceNameTextView.setText(getString(R.string.device_name)+" "+deviceName);

        TextView deviceAddressTextView = findViewById(R.id.deviceAddressTextView);
        deviceAddressTextView.setText(getString(R.string.device_address)+" "+deviceAddress);

        TextView macAddressTextView = findViewById(R.id.macAddressTextView);
        macAddressTextView.setText(getString(R.string.mac_address)+" "+macAddress);

        TextView ipAddressTextView = findViewById(R.id.ipAddressTextView);
        ipAddressTextView.setText(getString(R.string.ip_address)+" "+ipAddress);

        TextView osInfoTextView = findViewById(R.id.osInfoTextView);
        osInfoTextView.setText(getString(R.string.os)+" "+osInfo);

        TextView modelHardwareTextView = findViewById(R.id.modelHardwareTextView);
        modelHardwareTextView.setText(getString(R.string.model_nhardware)+" "+modelHardware);

        TextView uidTextView = findViewById(R.id.uid);
        uidTextView.setText(getString(R.string.uid)+" "+uid);

        TextView aliasTextView = findViewById(R.id.alias);
        aliasTextView.setText(getString(R.string.alias)+" "+alias);

        // Handle the "Back" button click
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
