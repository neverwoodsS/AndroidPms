package com.aot.pms;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aot.pms.abs.IExitListener;

public class MainActivity extends AppCompatActivity {

    private PermissionRequest permissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionRequest = new PermissionRequest.Builder()
                .setActivity(this)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, null)
                .addPermission(Manifest.permission.CAMERA, null, false)
                .addPermission(Manifest.permission.CALL_PHONE, null)
                .addPermission(Manifest.permission.READ_PHONE_STATE, null)
                .addPermission(Manifest.permission.READ_PHONE_STATE, null)
                .addPermission(Manifest.permission.READ_SMS, null)
                //添加退出的回调
                .setExitListener(new IExitListener() {
                    @Override
                    public void exit() {
                    }
                })
                .build();

        permissionRequest.requestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean b = PermissionUtil.getInstance().enterSettingPage();
        Log.i("onResume", "onResume = " + b);
        if (b) {
            PermissionUtil.getInstance().requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PermissionUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
