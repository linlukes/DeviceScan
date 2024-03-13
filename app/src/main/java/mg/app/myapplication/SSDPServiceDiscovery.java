package mg.app.myapplication;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SSDPServiceDiscovery {

    private final Context context;
    private final DiscoveryCallback discoveryCallback;
    private final NsdManager nsdManager;
    private final List<String> discoveredServices;

    NsdManager.DiscoveryListener discoveryListener;

    public SSDPServiceDiscovery(Context context, DiscoveryCallback discoveryCallback) {
        this.context = context;
        this.discoveryCallback = discoveryCallback;
        this.nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        this.discoveredServices = new ArrayList<>();
    }

    public void discoverServices() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d("SSDPServiceDiscovery", "Discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d("SSDPServiceDiscovery", "Service found: " + serviceInfo.getServiceName());
                // Add the service name to the list
                discoveredServices.add(serviceInfo.getServiceName());
                // Notify the callback
                discoveryCallback.onServiceDiscovered(discoveredServices);
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d("SSDPServiceDiscovery", "Service lost: " + serviceInfo.getServiceName());
                // Handle service lost event
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d("SSDPServiceDiscovery", "Discovery stopped");
                discoveryCallback.onDiscoveryStopped();
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("SSDPServiceDiscovery", "Discovery start failed: " + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("SSDPServiceDiscovery", "Discovery stop failed: " + errorCode);
            }
        };

        // Start service discovery
        nsdManager.discoverServices("_services._dns-sd._udp", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void stopDiscovery() {
        // Stop service discovery
        if (discoveryListener !=null){
            nsdManager.stopServiceDiscovery(discoveryListener);
            Log.d("SSDPServiceDiscovery", "Discovery stopped");
        }

    }

    public interface DiscoveryCallback {
        void onServiceDiscovered(List<String> services);
        void onDiscoveryStopped();
    }
}
