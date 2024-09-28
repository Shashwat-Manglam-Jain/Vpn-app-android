//package com.example.vpnanuj;
//
//import android.content.Intent;
//import android.net.VpnService;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.ParcelFileDescriptor;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.Socket;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class MyVpnService extends VpnService {
//    private static final  String TAG="My Free VPN";
//    private final AtomicBoolean isRunning=new AtomicBoolean(false);
//    private ParcelFileDescriptor VpnInterface;
//    private String ServerIp;
//    private int ServerPortNumber;
//    private Handler handler=new Handler(Looper.myLooper());
//    public static final  String  ACTION_VPN_CONNECTED="com.example.vpnanuj";
//public static  MyVpnService instance;
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        instance=this;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(intent!=null){
//            //get server ip address and port number
//
//ServerIp=intent.getStringExtra("vpnIp");
//ServerPortNumber=intent.getIntExtra("ServerPort",0);
//
//
//
//
//            // start vpn connection in another thread
//            Thread VpnThread=new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    MyVpnService.this.runVpnConnection();
//                }
//            });
//            VpnThread.start();
//
//        }
//        return  START_STICKY;
//    }
//
//    private void runVpnConnection() {
//        try{
//            if(establishedVpnConnection()){
//                readFromVpnInterface();
//            }
//        } catch (Exception e) {
//
//            Log.e("vpnsmj","Error during vpn connection: ->"+e.getMessage());
//
//        }finally {
//            StopVpnConnection();
//        }
//    }
//
//    private void StopVpnConnection() {
//    }
//    public ParcelFileDescriptor getVpnInterface(){
//        return VpnInterface;
//    }
//    private boolean establishedVpnConnection() throws IOException {
//        if(VpnInterface!=null){
//            Builder builder=new Builder();
//            builder.addAddress(ServerIp,32);
//            builder.addRoute("0.0.0.0",0);
//            VpnInterface=builder.setSession(getString(R.string.app_name))
//                    .setConfigureIntent(null)
//                    .establish();
//            return VpnInterface!=null;
//        }
//        else{
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    onVpnConnectionSuccess();
//                    Toast.makeText(MyVpnService.this, "Vpn Connection Alraeady Established", Toast.LENGTH_SHORT).show();
//                }
//            });
//            } return true;
//
//
//        }
//
//
//    //now read from vpn interface and write to network
//    private void readFromVpnInterface() throws IOException{
//        isRunning.set(true);
//        ByteBuffer buffer=ByteBuffer.allocate(32767);
//        while (isRunning.get()){
//            try{
//                FileInputStream inputStream=new FileInputStream(VpnInterface.getFileDescriptor());
//                int length=inputStream.read(buffer.array());
//                if(length>0){
//                    String recievedData=new String(buffer.array(),0,length);
//
//                    //now send recieved data to the main Activity by local broadcast reciever
//                    Intent intent=new Intent("recieved_data_from_vpn");
//                    intent.putExtra(  "data" ,recievedData);
//                   LocalBroadcastManager.getInstance(this).sendBroadcast(intent) ;
//// now create a method to write the processed data to the network
//                    writeToNetwork(buffer , length) ;
//                }
//            } catch (Exception e) {
//                Log.e("vpnsmj","Error reading data from interface: ->"+e.getMessage());
//            }
//        }
//        }
//
//    private void writeToNetwork(ByteBuffer buffer, int length) {
//        String processData=new String(buffer.array(),0,length);
//        try{
//            Socket socket=new Socket(ServerIp,ServerPortNumber);
//            OutputStream outputStream=socket.getOutputStream();
//            // convert the process data into bytes and write to the server
//            byte[] dataBytes=processData.getBytes(StandardCharsets.UTF_8);
//            outputStream.write(dataBytes);
//            outputStream.close();
//
//            // Closeed and the socket after sending the data
//            outputStream.close() ;
//            socket.close();
//
//        } catch (Exception e) {
//            Log.e("vpnsmj","Error sending data to server: ->"+e.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        //create new vpn to stop vpn connection
//        stopVpnConnection();
//    }
//    private void stopVpnConnection() {
//        isRunning.set(false);
//        if (VpnInterface != null) {
//try{
//    VpnInterface.close();
//} catch (Exception e) {
//    Log.e("vpnsmj","Error closing vpn Interface: ->"+e.getMessage());
//}
//        }
//
//    }
//
//
//
//    private void  onVpnConnectionSuccess(){
//        Intent intent=new Intent(ACTION_VPN_CONNECTED);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }
//}
package com.example.vpnanuj;

import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyVpnService extends VpnService {
    private static final String TAG = "My Free VPN";
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private ParcelFileDescriptor vpnInterface;
    private String serverIp;
    private int serverPortNumber;
    private Handler handler = new Handler(Looper.getMainLooper());
    public static final String ACTION_VPN_CONNECTED = "com.example.vpnanuj.VPN_CONNECTED";
    public static MyVpnService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            serverIp = intent.getStringExtra("vpnIp");
            serverPortNumber = intent.getIntExtra("vpnPort", 0);

            if (serverIp != null && serverPortNumber > 0) {
                new Thread(this::runVpnConnection).start();
            } else {
                Log.e(TAG, "Server IP address or port number is not provided.");
            }
        }
        return START_STICKY;
    }

    private void runVpnConnection() {
        try {
            if (establishVpnConnection()) {
                readFromVpnInterface();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during VPN connection: " + e.getMessage());
        } finally {
            stopVpnConnection();
        }
    }

    private boolean establishVpnConnection() throws IOException {
        if (vpnInterface == null) {
            Builder builder = new Builder();
            builder.addAddress("10.0.0.2", 24); // Example local address, adjust as needed
            builder.addRoute("0.0.0.0", 0);
            builder.addDnsServer("8.8.8.8");
            builder.addDnsServer("1.1.1.1");

            vpnInterface = builder
                    .setSession(getString(R.string.app_name))
                    .setConfigureIntent(null)
                    .establish();

            if (vpnInterface != null) {
                onVpnConnectionSuccess();
                return true;
            } else {
                Log.e(TAG, "Failed to establish VPN interface.");
            }
        } else {
            handler.post(() -> Toast.makeText(MyVpnService.this, "VPN connection already established", Toast.LENGTH_SHORT).show());
        }
        return false;
    }

    private void readFromVpnInterface() throws IOException {
        isRunning.set(true);
        ByteBuffer buffer = ByteBuffer.allocate(32767);
        try (FileInputStream inputStream = new FileInputStream(vpnInterface.getFileDescriptor())) {
            while (isRunning.get()) {
                int length = inputStream.read(buffer.array());
                if (length > 0) {
                    String receivedData = new String(buffer.array(), 0, length);
                    Intent intent = new Intent("received_data_from_vpn");
                    intent.putExtra("data", receivedData);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    buffer.clear(); // Clear the buffer for the next read
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading data from VPN interface: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVpnConnection();
    }

    private void stopVpnConnection() {
        isRunning.set(false);
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing VPN interface: " + e.getMessage());
            } finally {
                vpnInterface = null;
            }
        }
        // Broadcast VPN disconnection status
        Intent intent = new Intent(ACTION_VPN_CONNECTED);
        intent.putExtra("vpn_status", "disconnected");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void onVpnConnectionSuccess() {
        Intent intent = new Intent(ACTION_VPN_CONNECTED);
        intent.putExtra("vpn_status", "connected");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        handler.post(() -> Toast.makeText(MyVpnService.this, "VPN connected", Toast.LENGTH_SHORT).show());
    }
}
