package com.activityrecognitiontest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;




public class ActivityRecognitionService extends IntentService {

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            sendToMainActivity(ActivityRecognitionResult.extractResult(intent));
            // handleDetectedActivities(result.getProbableActivities());
        }
    }


    private void sendToMainActivity(ActivityRecognitionResult result) {
        List<DetectedActivity> probableActivities = result.getProbableActivities();
        ArrayList<Integer> typeList = new ArrayList<>();
        ArrayList<Integer> confidenceList = new ArrayList<>();

        for(DetectedActivity activity : probableActivities) {
            typeList.add(activity.getType());
            confidenceList.add(activity.getConfidence());
        }

        Intent intent = new Intent("activityRecognitionIntent");
        intent.putExtra("type", typeList);
        intent.putExtra("confidence", confidenceList);

        Toast.makeText(this, String.valueOf(result.getMostProbableActivity().getType()) + " (" + String.valueOf(result.getMostProbableActivity().getConfidence()) + "%)", Toast.LENGTH_LONG).show();
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
