package com.example.climbachiya.speedtestdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.bmartel.speedtest.ISpeedTestListener;
import fr.bmartel.speedtest.SpeedTestError;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private CustomGauge gauge3;
    String action = null;
    //http://10.1.1.205:8081/SpeedTest/TcpUploader

    /**
     * socket timeout used in ms.
     */
    private final static int SOCKET_TIMEOUT = 5000;

    /**
     * speed examples server host name.
     */
    private final static String SPEED_TEST_SERVER_HOST = "ping.online.net";
    //private final static String SPEED_TEST_SERVER_HOST = "10.1.1.205";

    /**
     * spedd examples server uri.
     */
    private static final String SPEED_TEST_SERVER_URI_UL = "/";
    //private static final String SPEED_TEST_SERVER_URI_UL = "/SpeedTest/TcpUploader";
    /**
     * upload 10Mo file size.
     */
    private static final int FILE_SIZE = 5000000; //10000000

    /**
     * http://ping.online.net/1Mo.dat
     * spedd examples server uri.
     */
    private final static String SPEED_TEST_SERVER_URI_DL = "/5Mo.dat"; //10Mo.dat

    /**
     * speed examples server port.
     */
    private final static int SPEED_TEST_SERVER_PORT = 80;
    //private final static int SPEED_TEST_SERVER_PORT = 8081;

    /**
     * speed examples server port.
     */
    //private final static int SPEED_TEST_SERVER_PORT = 8081;

   /* */
    /**
     * logger.
     *//*
    private final static Logger LOGGER = LogManager.getLogger(MainActivity.class.getName());*/

    long diffSecondsDownload; //store download response time
    long diffMinutesDownload;
    long diffSecondsUpload;// store upload response time
    long diffMinutesUpload;

    TextView txtResponseDownload, txtResponseUpload, txtProgress;
    ProgressBar progress;

    SpeedTestSocket speedTestSocket = null;
    // Animation
    Animation animBounce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_view);

        initUIControls();
        initClassObjects();
    }

    private void initClassObjects() {
        // load the animation
        animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        // instantiate speed examples
        speedTestSocket = new SpeedTestSocket();

        //set timeout for download
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);

        // add a listener to wait for speed examples completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onDownloadPacketsReceived(final long packetSize, final float transferRateBitPerSeconds, final
            float transferRateOctetPerSeconds) {

                //Log.v("onDownloadPacketsReceived :::: <packetSize> "+packetSize, "transferRateBitPerSeconds : "+transferRateBitPerSeconds);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                        //long fileSizeInKB = packetSize / 1024;
                        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                        //long fileSizeInMB = fileSizeInKB / 1024;

                        double downloadSpeed = ((double) transferRateBitPerSeconds / (1024 * 1024));
                        DecimalFormat df = new DecimalFormat("0.00");
                        String finalDownloadSpeed = df.format(downloadSpeed);

                        String downloadRes = "Download Test Result\n "
                                + "--------------------------------------- \n "
                                + "Packet size :- " + Html.fromHtml("<small>" + readableFileSize(packetSize) + "</small>") + " \n "
                                +//"Transfer rate :- "+Html.fromHtml("<small>"+transferRateBitPerSeconds+" bit/sec </small>")+"\n"+
                                "Download speed :- " + Html.fromHtml("<small>" + finalDownloadSpeed + " Mb/s" + "</small><br>"
                                + "Response time :- " + diffMinutesDownload + ":" + diffSecondsDownload + " sec");

                        txtResponseDownload.setVisibility(View.VISIBLE);
                        txtResponseDownload.startAnimation(animBounce);
                        txtResponseDownload.setText(downloadRes);
                        /*txtResponseDownload.setText(
                                "Download Test Result\n "+
                                "---------------------------------- \n "+
                                "Packet size :- "+Html.fromHtml("<small>"+readableFileSize(packetSize)+"</small>")+" \n "+
                                //"Transfer rate :- "+Html.fromHtml("<small>"+transferRateBitPerSeconds+" bit/sec </small>")+"\n"+
                                "Download speed :- "+Html.fromHtml("<small>"+finalDownloadSpeed+" Mb/s"+"</small>"+
                                "\nResponse time :- "+diffMinutesDownload+":"+diffSecondsDownload+" sec"));*/
                        //progress.setVisibility(View.INVISIBLE);
                        //progress.setProgress(0);
                        gauge3.setValue(0);

                        speedTestSocket = null;
                        animBounce = null;

                        initClassObjects();

                        if (action.equalsIgnoreCase("all")) { //after complete Download speed test call upload speed test
                            startUploadSpeed(null);
                        } else {
                            txtProgress.setText("0");
                            //progress.setVisibility(View.INVISIBLE);
                            gauge3.setValue(0);
                        }
                    }
                });
            }

            @Override
            public void onUploadPacketsReceived(final long packetSize, final float transferRateBitPerSeconds, float transferRateOps) {
                //Log.v("onUploadPacketsReceived :::: <packetSize> "+packetSize, "transferRateBitPerSeconds : "+transferRateBitPerSeconds);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        double uploadSpeed = ((double) transferRateBitPerSeconds / (1024 * 1024));
                        DecimalFormat df = new DecimalFormat("0.00");
                        String finalUploadSpeed = df.format(uploadSpeed);

                        String uploadRes = "Upload Test Result \n " +
                                "--------------------------------------- \n " +
                                "Packet size :- " + Html.fromHtml("<small>" + readableFileSize(packetSize) + "</small>") + " \n " +
                                //"Transfer rate :- "+Html.fromHtml("<small>"+transferRateBitPerSeconds+" bit/sec </small>")+"\n"+
                                "Upload speed :- " + Html.fromHtml("<small>" + finalUploadSpeed + " Mb/s" + "</small><br>" +
                                "Response time :- " + diffMinutesUpload + ":" + diffSecondsUpload + " sec");

                        txtResponseUpload.setVisibility(View.VISIBLE);
                        txtResponseUpload.startAnimation(animBounce);
                        txtResponseUpload.setText(uploadRes);
                        /*txtResponseUpload.setText(
                                "Upload Test Result \n "+
                                "---------------------------------- \n "+
                                "Packet size :- "+ Html.fromHtml("<small>"+readableFileSize(packetSize)+"</small>")+" \n "+
                                        //"Transfer rate :- "+Html.fromHtml("<small>"+transferRateBitPerSeconds+" bit/sec </small>")+"\n"+
                                        "Upload speed :- "+Html.fromHtml("<small>"+finalUploadSpeed+" Mb/s"+"</small>")); // returns 200.35);*/
                        //checking action flag value and then find it's status
                        //progress.setVisibility(View.INVISIBLE);

                        /*if (action.equalsIgnoreCase("all")) {
                            txtProgress.setText("");
                            progress.setVisibility(View.INVISIBLE);
                        } else {
                            txtProgress.setText("");
                            progress.setVisibility(View.INVISIBLE);
                        }*/
                        txtProgress.setText("");
                        //progress.setVisibility(View.INVISIBLE);
                        gauge3.setValue(0);

                        speedTestSocket = null;
                        animBounce = null;

                        initClassObjects();
                    }
                });
            }

            //download error handle here while scanning download speed
            @Override
            public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) {
                //Log.v("onDownloadError :::: <speedTestError> "+speedTestError, "errorMessage : "+errorMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtResponseDownload.startAnimation(animBounce);
                        txtResponseDownload.setText("Download Error : " + speedTestError);
                        //progress.setVisibility(View.INVISIBLE);
                        gauge3.setValue(0);

                        speedTestSocket = null;
                        animBounce = null;

                        initClassObjects();
                    }
                });
            }

            @Override
            public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtResponseUpload.startAnimation(animBounce);
                        txtResponseUpload.setText("Upload Error : " + speedTestError);
                        //progress.setVisibility(View.INVISIBLE);
                        gauge3.setValue(0);
                        speedTestSocket = null;
                        animBounce = null;

                        initClassObjects();
                    }
                });

                //Log.v("onUploadError :::: <speedTestError> "+speedTestError, "errorMessage : "+errorMessage);
            }

            @Override
            public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) {

                Date startdate = new Date(downloadReport.getStartTime());
                SimpleDateFormat startformatter = new SimpleDateFormat("hh:mm:ss");
                String startdateFormatted = startformatter.format(startdate);

                //Log.v("onDownloadProgress :::: <downloadReport> "+downloadReport, "percent : "+percent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        txtProgress.setText("" + (int) percent);// + " / 100%");
                        //progress.setProgress((int) percent);
                        gauge3.setValue((int) percent);
                    }
                });

                if ((int) percent >= 100) {
                    //Log.v("onDownloadProgress : ","StartTime :: "+downloadReport.getStartTime());
                    //Log.v("onDownloadProgress : ","ReportTime :: "+downloadReport.getReportTime());

                    Date enddate = new Date(downloadReport.getReportTime());
                    SimpleDateFormat endformatter = new SimpleDateFormat("hh:mm:ss");
                    String enddateFormatted = endformatter.format(enddate);

                    //Log.v("startdateFormatted : ",startdateFormatted);
                    //Log.v("enddateFormatted : ",enddateFormatted);
                    //estimatedTime = (startdateFormatted - enddateFormatted);

                    calculateTimeDifference(startdateFormatted, enddateFormatted, "downloadTime");
                }
            }

            @Override
            public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtProgress.setText("" + (int) percent);// + "/ 100%");
                        //progress.setProgress((int) percent);
                        gauge3.setValue((int) percent);

                        Date startdate = new Date(uploadReport.getStartTime());
                        SimpleDateFormat startformatter = new SimpleDateFormat("hh:mm:ss");
                        String startdateFormatted = startformatter.format(startdate);

                        if ((int) percent >= 100) {

                            Date enddate = new Date(uploadReport.getReportTime());
                            SimpleDateFormat endformatter = new SimpleDateFormat("hh:mm:ss");
                            String enddateFormatted = endformatter.format(enddate);

                            //Log.v("startdateFormatted : ",startdateFormatted);
                            //Log.v("enddateFormatted : ",enddateFormatted);
                            //estimatedTime = (startdateFormatted - enddateFormatted);

                            calculateTimeDifference(startdateFormatted, enddateFormatted, "uploadTime");

                            onUploadPacketsReceived(uploadReport.getTotalPacketSize(), uploadReport.getTransferRateBit(), uploadReport.getTransferRateOctet());

                        }
                    }
                });

                //Log.v("onUploadProgress :::: <uploadReport> "+uploadReport, "percent : "+percent);

            }
        });
    }

    /**
     * Calculate time difference for downloading/uploading speed
     *
     * @param startdateFormatted - startTime
     * @param enddateFormatted   - endTime
     */
    private void calculateTimeDifference(String startdateFormatted, String enddateFormatted, String operation) {
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(startdateFormatted);
            d2 = format.parse(enddateFormatted);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            if (operation.equalsIgnoreCase("downloadTime")) {
                diffSecondsDownload = diff / 1000 % 60;
                diffMinutesDownload = diff / (60 * 1000) % 60;
                //Log.v("diffMinutes : ",""+diffMinutesDownload);
                //Log.v("diffSeconds : ",""+diffSecondsDownload);
            } else if (operation.equalsIgnoreCase("uploadTime")) {
                diffSecondsUpload = diff / 1000 % 60;
                diffMinutesUpload = diff / (60 * 1000) % 60;
                //Log.v("diffMinutes : ",""+diffMinutesUpload);
                //Log.v("diffSeconds : ",""+diffSecondsUpload);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long bytesToMB(long bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }

    private String bytesIntoHumanReadable(long bytes) {
        long kilobyte = 1024;
        long megabyte = kilobyte * 1024;
        long gigabyte = megabyte * 1024;
        long terabyte = gigabyte * 1024;

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return (bytes / kilobyte) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return (bytes / megabyte) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return (bytes / gigabyte) + " GB";

        } else if (bytes >= terabyte) {
            return (bytes / terabyte) + " TB";

        } else {
            return bytes + " Bytes";
        }
    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void initUIControls() {
        txtResponseDownload = (TextView) findViewById(R.id.text_response_download);
        txtResponseUpload = (TextView) findViewById(R.id.text_response_upload);
        txtProgress = (TextView) findViewById(R.id.text_progress);
        //progress = (ProgressBar) findViewById(R.id.progress);
        gauge3 = (CustomGauge) findViewById(R.id.gauge3);

        //progress.setVisibility(View.INVISIBLE);
    }

   /* public void callSecond(View view){
        startActivity(new Intent(this, SpeedTestLauncher.class));
    }*/

    public void startSpeedTestAll(View view) {
        if(Utility.hasConnection(this)){
            new SpeedTestAsync("all").execute();
        }else{
            Toast.makeText(MainActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
        }

    }

    public void startUploadSpeed(View view) {
        if(Utility.hasConnection(this)){
            new SpeedTestAsync("upload").execute();
        }else{
            Toast.makeText(MainActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
        }

    }

    public void startDownloadSpeed(View view) {
        if(Utility.hasConnection(this)){
            new SpeedTestAsync("download").execute();
        }else{
            Toast.makeText(MainActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
        }

    }

    public class SpeedTestAsync extends AsyncTask<String, String, String> {

        public SpeedTestAsync(String operation) {
            action = operation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String label = null;
            if (action.equalsIgnoreCase("download")) {
                label = "Download test begin...";
            } else if (action.equalsIgnoreCase("upload")) {
                label = "Upload test begin...";
            } else {
                label = "Download/Upload test begin...";
            }
            Toast.makeText(MainActivity.this, label, Toast.LENGTH_SHORT).show();
            //progress.setVisibility(View.VISIBLE);
            //progress.setProgress(0);
            txtProgress.setText("0");
        }

        /*String action = null;
        public SpeedTestAsync(String operation) {
            this.action = operation;
        }*/

        @Override
        protected String doInBackground(String... params) {

            //Setting downloading uploading speed test
            if (action.equalsIgnoreCase("download")) {
                speedTestSocket.startDownload(SPEED_TEST_SERVER_HOST, SPEED_TEST_SERVER_PORT, SPEED_TEST_SERVER_URI_DL);
            } else if (action.equalsIgnoreCase("upload")) {
                speedTestSocket.startUpload(SPEED_TEST_SERVER_HOST, SPEED_TEST_SERVER_PORT, SPEED_TEST_SERVER_URI_UL,
                        FILE_SIZE);
            } else {
                speedTestSocket.startDownload(SPEED_TEST_SERVER_HOST, SPEED_TEST_SERVER_PORT, SPEED_TEST_SERVER_URI_DL);
            }
            return null;
        }
    }
}