package com.thesis.alphidcar;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.services.LocalAndroidServer;
import com.thesis.alphidcar.databinding.ActivityFullSizeBinding;
import com.thesis.alphidcar.services.LocalServer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class AlphidCarFullSizeActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice robotCarDevice;
    OutputStream outputStream;
    ActivityFullSizeBinding binding;
    ProgressDialog progressDialog;
    private static final UUID UUID_SERIAL_PORT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String DEVICE_NAME = "HC-06";  // Replace with your module's Bluetooth name
    private boolean toggleTurnCam = false;
    private boolean toggleCam = false;
    private boolean turnOnPump = false;
    private LocalServer server;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullSizeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
            finish();
        }
        setListeners();
        try {
            server = new LocalServer(8080, new DefaultBaseListener() {
                @Override
                public <T> void onSuccess(T any) {
                    runOnUiThread(() ->
                            Toast.makeText(AlphidCarFullSizeActivity.this, "Alphid Detected", Toast.LENGTH_SHORT).show()
                    );
                    if (turnOnPump) {
                        sendCommand("%U#");
                        turnOnPump = false;

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {

                        }
                    }

                    sendCommand("%U#");
                    turnOnPump = false;

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                }

                @Override
                public void onError(Error error) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(AlphidCarFullSizeActivity.this, "Failed To Establish Server", Toast.LENGTH_SHORT).show();
        }
    }


    private void setListeners() {
        binding.btnTurnCam.setOnClickListener(v -> {
            String videoUrl = "http://192.168.1.9:5000/video"; // Change this to your Python server's IP

            if (toggleTurnCam) {
                sendCommand("%V#");
                toggleTurnCam = false;
            } else {
                sendCommand("%X#");
                toggleTurnCam = true;
            }
        });

        binding.btnConnect.setOnClickListener(v -> connectToRobotCar());

        binding.btnUp.setOnClickListener(v -> sendCommand("%A#"));
        binding.btnDown.setOnClickListener(v -> sendCommand("%B#"));
        binding.btnLeft.setOnClickListener(v -> sendCommand("%E#"));
        binding.btnRight.setOnClickListener(v -> sendCommand("%F#"));
        binding.btnPump.setOnClickListener(v -> sendCommand("%U#"));

        binding.btnActType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleCam) {
                    toggleCam = false;
                    Toast.makeText(AlphidCarFullSizeActivity.this, "Car is set to manual", Toast.LENGTH_SHORT).show();
                    binding.btnTurnCam.setEnabled(true);
                    binding.btnPump.setEnabled(true);
                    turnOnPump =false;
                    sendCommand("%S#");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                } else {
                    binding.btnTurnCam.setEnabled(false);
                    binding.btnPump.setEnabled(false);
                    toggleCam = true;

                    Toast.makeText(AlphidCarFullSizeActivity.this, "Car is set to automatic", Toast.LENGTH_SHORT).show();
                    autoCar();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.stop();
    }

    private void connectToRobotCar() {
        progressDialog = ProgressDialog.show(AlphidCarFullSizeActivity.this, "Connecting", "Please wait...", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(AlphidCarFullSizeActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Android 12 and above require runtime permission check for Bluetooth
                        ActivityCompat.requestPermissions(AlphidCarFullSizeActivity.this,
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
                        Toast.makeText(AlphidCarFullSizeActivity.this, "Connected to Robot Car", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(AlphidCarFullSizeActivity.this, "Failed to connect to Robot Car", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AlphidCarFullSizeActivity.this, "Robot Car not found. Make sure it is paired.", Toast.LENGTH_SHORT).show();
                }
                sendCommand("%Z#");
                toggleTurnCam = false;
                toggleCam = false;
                progressDialog.dismiss();
            }
        }, 2000);  // Delay for 2 seconds to simulate connection time
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());
//                Toast.makeText(this, "Command sent: " + command, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
//                Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to robot car", Toast.LENGTH_SHORT).show();
        }
    }

    private void autoCar() {
        new Thread(() -> {
            while (!toggleCam) {
                // Move forward 1 step
                sendCommand("%A#");
                try {
                    Thread.sleep(500); // Adjust delay as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Turn camera left

                sendCommand("%V#");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Turn camera right
                sendCommand("%X#");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                sendCommand("%Z#");


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }
}
