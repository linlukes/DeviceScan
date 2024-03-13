package mg.app.myapplication;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;

public class MdnsServiceDiscovery {

    private JmDNS jmDNS;
    private List<String> discoveredServices = new ArrayList<>();
    private ServiceListener serviceListener;

    public MdnsServiceDiscovery(InitializeListener initializeListener) {
        new InitializeTask(initializeListener).execute();
    }

    private class InitializeTask extends AsyncTask<Void, Void, Void> {
        private InitializeListener initializeListener;

        public InitializeTask(InitializeListener initializeListener) {
            this.initializeListener = initializeListener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                jmDNS = JmDNS.create(localhost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (initializeListener != null) {
                initializeListener.onInitializeComplete();
            }
        }
    }

    public void setServiceListener(ServiceListener listener) {
        this.serviceListener = listener;
    }

    public void discoverServices() {
        if (jmDNS != null) {
            jmDNS.addServiceListener("_http._tcp.local.", new javax.jmdns.ServiceListener() {
                @Override
                public void serviceAdded(ServiceEvent event) {
                    // Service added
                    Log.d("jmDNS", "serviceAdded: "+event.getName());
                }

                @Override
                public void serviceRemoved(ServiceEvent event) {
                    // Service removed
                    Log.d("jmDNS", "serviceRemoved: "+event.getName());
                }

                @Override
                public void serviceResolved(ServiceEvent event) {
                    // Service resolved
                    String serviceInfo = event.getInfo().getName() + " - " + event.getInfo().getHostAddress();
                    Log.d("jmDNS", "serviceResolved: "+serviceInfo);
                    if (!discoveredServices.contains(serviceInfo)) {
                        discoveredServices.add(serviceInfo);
                        notifyServiceDiscovered(serviceInfo);
                    }
                }
            });
        }
    }

    public void close() {
        if (jmDNS != null) {
            try {
                jmDNS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyServiceDiscovered(final String serviceInfo) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (serviceListener != null) {
                    serviceListener.onServiceDiscovered(serviceInfo);
                }
            }
        });
    }

    public interface ServiceListener {
        void onServiceDiscovered(String serviceInfo);

        void serviceAdded(ServiceEvent event);

        void serviceRemoved(ServiceEvent event);

        void serviceResolved(ServiceEvent event);
    }

    public interface InitializeListener {
        void onInitializeComplete();
    }
}
