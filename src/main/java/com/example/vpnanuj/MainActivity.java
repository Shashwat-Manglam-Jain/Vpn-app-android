package com.example.vpnanuj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vpnanuj.Adapter.UserAdapter;
import com.example.vpnanuj.Modals.Vpndata;
import com.example.vpnanuj.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnVpnClickListener {
    private UserAdapter userAdapter;
    private List<Vpndata> vpnList;
    private ActivityMainBinding binding;
    private static final int VPN_REQUEST_CODE = 1;
    private TextView connectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize RecyclerView and VPN list
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        connectionStatus = findViewById(R.id.cnn);



        // Initialize VPN list with sample data
        vpnList = new ArrayList<>();
        vpnList.add(new Vpndata("U.S.A", "https://www.vpngate.net/images/flags/US.png", "75.142.5.100", 22, 1384, "vpn408309503.opengw.net"));
        vpnList.add(new Vpndata("Japan", "https://www.vpngate.net/images/flags/JP.png", "219.100.37.57", 8, 443, "public-vpn-100.opengw.net"));
        vpnList.add(new Vpndata("Japan", "https://www.vpngate.net/images/flags/JP.png", "219.100.37.109", 10, 1195, "public-vpn-153.opengw.net"));
        vpnList.add(new Vpndata("S.Korea", "https://www.vpngate.net/images/flags/KR.png", "211.184.253.125", 31, 1418, "vpn257994583.opengw.net:1357"));
        vpnList.add(new Vpndata("Taiwan", "https://www.vpngate.net/images/flags/TW.png", "123.195.133.10", 32, 1194, "vpn0925204913.opengw.net"));
        vpnList.add(new Vpndata("Romania", "https://www.vpngate.net/images/flags/RO.png", "217.138.212.58", 8, 1195, "opengw.opengw.net"));
        vpnList.add(new Vpndata("Spain", "https://www.vpngate.net/images/flags/ES.png", "46.27.142.86", 16, 1194, "vpn259772607.opengw.net"));

        // Set up the adapter with the listener
        userAdapter = new UserAdapter(vpnList, this, this);
        binding.recyclerView.setAdapter(userAdapter);

        binding.ssss.setOnClickListener(v -> establishVpnConnection());
        binding.button.setOnClickListener(v -> {
            establishVpnConnection();
           
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(vpnConnectedReceiver,
                new IntentFilter(MyVpnService.ACTION_VPN_CONNECTED));
    }

    private void establishVpnConnection() {
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null) {
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        } else {
            startVpnServiceWithIp("219.100.37.123", 1195); // Default IP and port
        }
    }

    private void startVpnServiceWithIp(String ip, int port) {
        Intent vpnIntent = new Intent(this, MyVpnService.class);
        vpnIntent.putExtra("vpnIp", ip);
        vpnIntent.putExtra("vpnPort", port); // Ensure this matches with MyVpnService
        startService(vpnIntent);
    }


    private final BroadcastReceiver vpnConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MyVpnService.ACTION_VPN_CONNECTED.equals(action)) {
                String status = intent.getStringExtra("vpn_status");
                if ("disconnected".equals(status)) {
                    connectionStatus.setText("Disconnected");
                    connectionStatus.setTextColor(Color.parseColor("#ffffff"));
                    binding.ssss.setCardBackgroundColor(Color.parseColor("#ffffff"));
                } else {
                    connectionStatus.setText("Connected");
                    connectionStatus.setTextColor(Color.parseColor("#f4a526"));
                    binding.ssss.setCardBackgroundColor(Color.parseColor("#f4a526"));
                }
            }
        }
    };


    @Override
    public void onVpnClick(String ip, int port, String vpnProviderName, String countryName) {

        // Check if VPN service is already running
        boolean isVpnServiceRunning = MyVpnService.instance != null && MyVpnService.instance.isRunning();

        if (isVpnServiceRunning) {
            Toast.makeText(this, "VPN Already Connected", Toast.LENGTH_LONG).show();

                connectionStatus.setText("Connected");
                connectionStatus.setTextColor(Color.parseColor("#f4a526"));
                binding.ssss.setCardBackgroundColor(Color.parseColor("#f4a526"));

        } else {
            binding.Provider.setText(vpnProviderName);
            binding.country.setText(countryName);
            binding.textView11.setText(ip);
            startVpnServiceWithIp(ip, port);
            connectionStatus.setText("Connecting...");
            connectionStatus.setTextColor(Color.parseColor("#f4a526"));
            binding.ssss.setCardBackgroundColor(Color.parseColor("#f4a526"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(vpnConnectedReceiver);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startVpnServiceWithIp("131.147.203.90", 1911);
            } else {
                // VPN permission was denied
                connectionStatus.setText("VPN Permission Denied");
            }
        }
    }
}
