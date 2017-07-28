package com.activityrecognitiontest;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            sendToMainActivity(mostProbableActivity.getType(),
                    mostProbableActivity.getConfidence());
            // handleDetectedActivities(result.getProbableActivities());
        }
    }


    private void sendToMainActivity(int type, int confidence) {
        Intent intent = new Intent("activityRecognitionIntent");
        intent.putExtra("type", type);
        intent.putExtra("confidence", confidence);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for(DetectedActivity activity : probableActivities) {
            switch(activity.getType()) {
                case DetectedActivity.IN_VEHICLE: { // 0
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_BICYCLE: { // 1
                    Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_FOOT: { // 2
                    Log.e("ActivityRecogition", "On Foot: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.RUNNING: { // 8
                    Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.STILL: { // 3
                    Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.TILTING: { // 5
                    Log.e("ActivityRecogition", "Tilting: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.WALKING: { // 7
                    Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.UNKNOWN: { // 4
                    Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
                    break;
                }
            }
        }
    }
}
