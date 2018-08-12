package com.chano.chanosoundmeter.activity;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.chano.chanosoundmeter.R;
import com.chano.chanosoundmeter.decibel.DecibelManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    AdView adViewBanner;
    DecibelManager decibelManager;
    private TextView tvDecibel;
    private ImageView ivIndicator;
    private boolean isRecording = false;
    private float decibel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-5008090649014762~6109743821");
        setView();
        setPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecording = false;
    }

    private void setPermission(){
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
//                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
//                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.RECORD_AUDIO)
                .check();
    }

    private void setView(){
        tvDecibel = (TextView) findViewById(R.id.tv_decibel);
        ivIndicator = (ImageView) findViewById(R.id.iv_indicator);
        adViewBanner = (AdView) findViewById(R.id.adView_banner);
        adViewBanner.setAdListener(adListener);

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
    }

    private class StartRecodingTask extends AsyncTask<Void, Integer, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(isRecording){
                decibel = decibelManager.soundDb(decibelManager.getAmplitude());
                publishProgress();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tvDecibel.setText(String.valueOf(decibelManager.setFormatDB(decibel)) + " dB");
            ivIndicator.setRotation(decibelManager.setFormatDB(decibel));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private final AdListener adListener = new AdListener(){
        @Override
        public void onAdClosed() {
            super.onAdClosed();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            String message = "Unknown";
            super.onAdFailedToLoad(errorCode);
            switch (errorCode) {
                // 광고 서버에서 잘못된 응답과 같은 내부 에러
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    message = "ERROR_CODE_INTERNAL_ERROR";
                    break;
                // 잘못된 광고 id를 넣었을 때와 같이 광고 요청 에러
                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    message = "ERROR_CODE_INVALID_REQUEST";
                    break;
                //광고 요청시 네트워크상 에러
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    message = "ERROR_CODE_NETWORK_ERROR";
                    break;
                //광고 요청은 성공했지만 광고가 부족할 때
                case AdRequest.ERROR_CODE_NO_FILL:
                    message = "ERROR_CODE_NO_FILL";
                    break;
            }
            Log.e(TAG, message);
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
        }
    };

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.d(TAG,"PermissionGranted");
            decibelManager = new DecibelManager();
            isRecording = decibelManager.setRecorder();
            new StartRecodingTask().execute();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Log.d(TAG,"PermissionDenied : " + deniedPermissions.toString());
        }
    };

}
