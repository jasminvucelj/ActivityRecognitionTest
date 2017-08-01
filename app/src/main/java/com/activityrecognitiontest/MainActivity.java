package com.activityrecognitiontest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    final int CONFIDENCE_THRESHOLD = 75;
    final String FILENAME = "log.txt";

    TextView tv, tv2;
    Button button;

    Vibrator v;

    String output = "";

    GoogleApiClient mApiClient;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Integer> typeList = intent.getIntegerArrayListExtra("type");
            ArrayList<Integer> confidenceList = intent.getIntegerArrayListExtra("confidence");

            if (typeList != null && !typeList.isEmpty() && confidenceList != null && !confidenceList.isEmpty()) {
                /*if (v != null && v.hasVibrator()) {
                    v.vibrate(500);
                }*/

                output = activitiesAsString(typeList, confidenceList);
                writeToFile(FILENAME, output, MainActivity.this);
            }
        }
    };


    String activitiesAsString(ArrayList<Integer> typeList, ArrayList<Integer> confidenceList) {
        StringBuilder sb = new StringBuilder();

        Time currentTime = new Time();
        currentTime.setToNow();

        sb.append(currentTime.format2445());
        sb.append(":\n");

        int size = typeList.size();
        for (int i = 0; i < size; i++) {
            sb.append(String.valueOf(typeList.get(i)));
            sb.append(" (");
            sb.append(String.valueOf(confidenceList.get(i)));
            sb.append("%)\n\n");
        }

        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        tv2 = (TextView) findViewById(R.id.tv2);
        button = (Button) findViewById(R.id.button);

        clearFile(FILENAME);

        tv2.setText("\nIN_VEHICLE: 0\nON_BICYCLE: 1\nON_FOOT: 2\nRUNNING: 8\nSTILL: 3\nTILTING: 5\nWALKING: 7\nUNKNOWN: 4");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(readFromFile(FILENAME, MainActivity.this));
            }
        });

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                activityReceiver, new IntentFilter("activityRecognitionIntent"));

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

    }


    @Override
    protected void onDestroy() {
        v.cancel();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityReceiver);
        super.onDestroy();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient,
                6 * 1000,
                pendingIntent);
        Log.e("MainActivity","Connected");
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity","Connection suspended");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity","Connection failed");
    }


    private void clearFile(String filename) {
        File file = new File(getFilesDir(), filename);
        boolean b = file.delete();
    }


    private void writeToFile(String filename, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(
                            filename,
                            Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String readFromFile(String filename, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("IOException", "Can not read file: " + e.toString());
        }

        return ret;
    }



}
