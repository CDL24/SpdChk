package com.example.climbachiya.speedtestdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import fr.bmartel.speedtest.ISpeedTestListener;
import fr.bmartel.speedtest.SpeedTestError;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;

/**
 * Created by C.limbachiya on 5/25/2016.
 */
public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startUploadSpeed(View view){
        new SpeedTestTask().execute();
    }

    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

               /* @Override
                public void onDownloadPacketsReceived(int packetSize,
                                                      float transferRateBitPerSeconds,
                                                      float transferRateOctetPerSeconds) {
                    Log.i("speed-test-app","download transfer rate  : " + transferRateOctetPerSeconds + "Bps");
                }*/

                @Override
                public void onDownloadError(SpeedTestError errorCode, String message) {
                    Log.i("speed-test-app","Download error " + errorCode + " occured with message : " + message);
                }

                @Override
                public void onUploadPacketsReceived(long packetSize, float transferRateBitPerSeconds, float transferRateOctetPerSeconds) {
                    Log.i("speed-test-app","download transfer rate  : " + transferRateOctetPerSeconds + "Bps");
                }

               /* @Override
                public void onUploadPacketsReceived(int packetSize,
                                                    float transferRateBitPerSeconds,
                                                    float transferRateOctetPerSeconds) {
                    Log.i("speed-test-app","download transfer rate  : " + transferRateOctetPerSeconds + "Bps");
                }*/

                @Override
                public void onUploadError(SpeedTestError errorCode, String message) {
                    Log.i("speed-test-app","Upload error " + errorCode + " occured with message : " + message);
                }

                @Override
                public void onDownloadPacketsReceived(long packetSize, float transferRateBitPerSeconds, float transferRateOctetPerSeconds) {
                    Log.i("speed-test-app","download transfer rate  : " + transferRateBitPerSeconds + "Bps");
                }

                @Override
                public void onDownloadProgress(float percent,SpeedTestReport downloadReport) {
                    Log.v("onDownloadProgress " + downloadReport, "percent : "+percent);
                }

                @Override
                public void onUploadProgress(float percent,SpeedTestReport uploadReport) {
                    Log.v("onUploadProgress :: ","uploadReport > "+uploadReport+" percent > "+percent);
                }

            });

            speedTestSocket.startUpload("1.testdebit.info",
                    80, "/", 10000000); //will block until upload is finished

            return null;
        }
    }
}
