package com.ridehome.castsink;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ridehome.castsink.vlc.MyControlVlcVideoView;

public class VlcPlayerActivity extends AppCompatActivity {
    public static String mPath01 = "rtsp://172.16.54.241:12345";
    private RelativeLayout rootView;
    private MyControlVlcVideoView mPlayerView;
    private View.OnTouchListener onTouchVideoListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlc);
        //设置沉浸式观影模式体验
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //永远不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
    }

    private void initView() {
        mPlayerView = findViewById(R.id.player);
        //触摸控制亮度和声音,是否可触摸开关
        rootView = mPlayerView.getRootView();
        onTouchVideoListener = mPlayerView.getOnTouchVideoListener();
        rootView.setLongClickable(true);  //手势需要--能触摸
        rootView.setOnTouchListener(onTouchVideoListener);

        mPlayerView.setPlayerTitle("你好，我是手动设置传入的标题");

        mPlayerView.setVlcControllerLayoutListener(new MyControlVlcVideoView.onVlcControllerLayoutListener() {
            @Override
            public void finishActivity() {
                finish();
            }
        });

        mPlayerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //开始播放
                mPlayerView.setStartLive(mPath01);
            }
        },500);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayerView.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();

    }
}
