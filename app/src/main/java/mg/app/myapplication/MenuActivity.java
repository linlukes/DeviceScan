package mg.app.myapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;


import java.util.List;

import mg.app.myapplication.utils.LocationUtil;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
// getPermissions
        getPermissions();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for Button 1
                if (!LocationUtil.isLocationEnabled(MenuActivity.this)) {
                    // If not enabled, request the user to enable it
                    LocationUtil.showLocationDialog(MenuActivity.this);
                } else {
                    startActivity(new Intent(MenuActivity.this, BluetoothActivity.class));
                }


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for Button 2
                startActivity(new Intent(MenuActivity.this, SsdpActivity.class));
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for Button 3
                if (!LocationUtil.isLocationEnabled(MenuActivity.this)) {
                    // If not enabled, request the user to enable it
                    LocationUtil.showLocationDialog(MenuActivity.this);
                } else {
                    startActivity(new Intent(MenuActivity.this, WifiDirectActivity.class));
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click for Button 4
                startActivity(new Intent(MenuActivity.this, MdnsActivity.class));
            }
        });
    }

    private void getPermissions() {
        PermissionX.init(this)
                .permissions(Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "Core fundamentals are based on these permissions", "OK", "Cancel");

                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (!allGranted) {

                        Toast.makeText(MenuActivity.this, "These permissions are denied: " + deniedList, Toast.LENGTH_LONG).show();
                        getPermissions();
                    }
                    }
                });
    }

}
