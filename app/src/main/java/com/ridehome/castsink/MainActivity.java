package com.ridehome.castsink;

import androidx.annotation.NonNull;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ridehome.castsink.vlc.MyControlVlcVideoView;
import com.ridehome.castsink.wifip2p.activity.BaseActivity;
import com.ridehome.castsink.wifip2p.service.Wifip2pService;
import com.ridehome.castsink.wifip2p.socket.ReceiveSocket;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
public class MainActivity extends BaseActivity implements ReceiveSocket.ProgressReceiveListener,EasyPermissions.PermissionCallbacks, View.OnClickListener  {
    public static final String TAG = "MainActivity";

    private Wifip2pService.MyBinder mBinder;
    private Intent mIntent;

    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;
    private String mRtspUrl;

    public static String mPath01 = "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AAOEcdM.img";
    private RelativeLayout rootView;
    private MyControlVlcVideoView mPlayerView;
    private View.OnTouchListener onTouchVideoListener;

    private static MainActivity mInstance;
    public static MainActivity getInstance(){
        return mInstance;
    }

    public void StartPlay(String strUrl){
        mRtspUrl = strUrl;
        runOnUiThread(() -> {
            EditText urlText = (EditText) findViewById(R.id.rtspURL);
            urlText.setText(strUrl);
            // ??????RTSP
            mPlayerView.setStartLive(strUrl);
        });
    }

    private void initView() {
        mPlayerView = findViewById(R.id.player);
        //???????????????????????????,?????????????????????
        rootView = mPlayerView.getRootView();
        onTouchVideoListener = mPlayerView.getOnTouchVideoListener();
        rootView.setLongClickable(true);  //????????????--?????????
        rootView.setOnTouchListener(onTouchVideoListener);

      //  mPlayerView.setPlayerTitle("P2P????????????");

        mPlayerView.setVlcControllerLayoutListener(new MyControlVlcVideoView.onVlcControllerLayoutListener() {
            @Override
            public void finishActivity() {
                finish();
            }
        });

//        mPlayerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //????????????
//                mPlayerView.setStartLive(mPath01);
//            }
//        },500);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //???????????????????????????????????????
            mBinder = (Wifip2pService.MyBinder) service;
            mBinder.initListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //????????????????????????
            bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_main);

        mInstance = this;

        requireSomePermission();

        Button btnCreate = (Button) findViewById(R.id.btn_create);
        Button btnRemove = (Button) findViewById(R.id.btn_remove);
        btnCreate.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        Button btnPlayRTSPP = (Button) findViewById(R.id.btn_playrtsp);
        Button btnStopRTSP = (Button) findViewById(R.id.btn_stoprtsp);
        btnPlayRTSPP.setOnClickListener(this);
        btnStopRTSP.setOnClickListener(this);

        initView();
    }

    @AfterPermissionGranted(1000)
    private void requireSomePermission() {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            //?????????
        } else {
            //?????????
            EasyPermissions.requestPermissions(this, "????????????????????????",
                    1000, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG,"???????????????");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG,"??????????????????");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSatrt() {

    }

    @Override
    public void onProgressChanged(File file, int progress) {

    }

    @Override
    public void onFinished(File file) {

    }

    @Override
    public void onFaliure(File file) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                createGroup();
                break;
            case R.id.btn_remove:
                removeGroup();
                break;
            case R.id.btn_playrtsp: {
                EditText urlText = (EditText) findViewById(R.id.rtspURL);
                String strUrl = urlText.getText().toString();
                //mStreamUI.Start(strUrl);
            }
                break;
            case R.id.btn_stoprtsp:
                //mStreamUI.Stop();
                break;
        }
    }

    /**
     * ???????????????????????????
     */
    public void createGroup() {

        mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "??????????????????");

                mIntent = new Intent(MainActivity.this, Wifip2pService.class);
                //startService(mIntent);
                bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "??????????????????: " + reason);                Toast.makeText(MainActivity.this, "??????????????????,??????????????????????????????????????????WIFI??????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ????????????
     */
    public void removeGroup() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "??????????????????");
                Toast.makeText(MainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "??????????????????");
                Toast.makeText(MainActivity.this, "??????????????????,?????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         clear();
    }

    /**
     * ????????????
     */
    private void clear() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        if (mIntent != null) {
            stopService(mIntent);
        }
    }
}