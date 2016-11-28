package lighterletter.com.movement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;

import java.util.Date;

import lighterletter.com.movement.Model.User;

/**
 * Created by john on 11/23/16.
 */

public class StepsService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private static StepsService instance;
    private Context context;

    public static StepsService startInstance() {
        if (instance == null){
            Log.d("user service: ", "startInstance is called");
            instance = new StepsService();

            return instance;
        }
        return instance;
    }

    public void setSensorManager(SensorManager sensorManager){
        this.sensorManager = sensorManager;
    }

    public void setContext(Context context){
        this.context = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int
            startId) {

        Log.d("user service: ", "service started");

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_GAME);

        } else {
            onErrorToLoginScreen("Your device does not contain the hardware necessary for this app");
        }

        return Service.START_STICKY;
    }

    private void onErrorToLoginScreen(String message) {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
        Twitter.logOut();
        SaveSharedPreference.clearUserKey(getApplicationContext());
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        long timeInMillis = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        float value = event.values[0];
        Log.d("user service event: ", "sensor event detected, value: " + value);

        // 1.0 is the value returned by the step detector when a step is detected
        if (value == 1.0f) {
            RealmUtil.getInstance().addToOverallStepsForToday(timeInMillis, context);
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, int i) {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
