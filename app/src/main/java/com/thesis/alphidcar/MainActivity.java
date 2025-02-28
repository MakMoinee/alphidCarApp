package com.thesis.alphidcar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.thesis.alphidcar.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice robotCarDevice;
    OutputStream outputStream;
    ActivityMainBinding binding;
    ProgressDialog progressDialog;
    private static final UUID UUID_SERIAL_PORT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String DEVICE_NAME = "HC-06";  // Replace with your module's Bluetooth name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        setListeners();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void setListeners() {
        binding.btnConnect.setOnClickListener(v -> connectToRobotCar());

        binding.btnForward.setOnClickListener(v -> sendCommand("%A#"));
        binding.btnBackward.setOnClickListener(v -> sendCommand("%B#"));
        binding.btnLeft.setOnClickListener(v -> sendCommand("%E#"));
        binding.btnRight.setOnClickListener(v -> sendCommand("%F#"));
        binding.btnStop.setOnClickListener(v -> sendCommand("%S#"));
        binding.btnPump.setOnClickListener(v -> sendCommand("%U#"));
        binding.btnTurnCam.setOnClickListener(v -> sendCommand("%V#"));
    }

    private void connectToRobotCar() {
        progressDialog = ProgressDialog.show(MainActivity.this, "Connecting", "Please wait...", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Android 12 and above require runtime permission check for Bluetooth
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                                1);
                    }
                } else {
                }
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (DEVICE_NAME.equals(device.getName())) {
                            robotCarDevice = device;
                            break;
                        }
                    }
                }

                if (robotCarDevice != null) {
                    try {
                        bluetoothSocket = robotCarDevice.createRfcommSocketToServiceRecord(UUID_SERIAL_PORT);
                        bluetoothSocket.connect();
                        outputStream = bluetoothSocket.getOutputStream();
                        Toast.makeText(MainActivity.this, "Connected to Robot Car", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Failed to connect to Robot Car", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Robot Car not found. Make sure it is paired.", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }
        }, 2000);  // Delay for 2 seconds to simulate connection time
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Bluetooth operations

            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Bluetooth permission denied. Cannot connect to Bluetooth devices.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());
                Toast.makeText(this, "Command sent: " + command, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to robot car", Toast.LENGTH_SHORT).show();
        }
    }
}