package com.example.permissionexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button btnCameraPermission;
    private Button btnLocationPermission;
    private Button btnnMultiplePermission;

    private String[] permissions = {Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO};

    public static final int CAMERA_PERMISSION_CODE = 100;
    public static final int LOCATION_PERMISSION_CODE = 101;
    public static final int MULTIPLE_PERMISSIONS_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCameraPermission = findViewById(R.id.btn_permission_camera);
        btnLocationPermission = findViewById(R.id.btn_permission_location);
        btnnMultiplePermission = findViewById(R.id.btn_permission_multiple);

        btnCameraPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
            }
        });

        btnLocationPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
            }
        });

        btnnMultiplePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMultiplePermissions();
            }
        });

    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkMultiplePermissions() {
        List<String> listPermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissions.add(permission);
            }
        }

        if (!listPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissions.toArray(new String[listPermissions.size()]), MULTIPLE_PERMISSIONS_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == MULTIPLE_PERMISSIONS_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedPermissions = 0;

            for (int i = 0; i <  grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedPermissions++;
                }
            }

            if (deniedPermissions == 0) {
                Toast.makeText(this, "All permissions are Granted", Toast.LENGTH_SHORT).show();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String permissioName = entry.getKey();
                    int permissionResult = entry.getValue();

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissioName)) {
                        showMessage("This app needs permission to Contacts and Microphone to enable this feature",
                                "Yes, Grant Permission", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        checkMultiplePermissions();
                                    }
                                }, "No, exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, false);
                    }else {
                        showMessage("You have denied some permissions. Allow them form Seetting -> Permissions",
                                "Go to Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, "No, exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, false);
                    }

                }
            }
        }
    }

    private void showMessage(String message, String positiveLabel,
                             DialogInterface.OnClickListener positiveClick,
                             String negativeLabel,
                             DialogInterface.OnClickListener negativeClick,
                             boolean isCancelable) {

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(isCancelable)
                .setPositiveButton(positiveLabel, positiveClick)
                .setNegativeButton(negativeLabel, negativeClick)
                .create()
                .show();
    }
}
