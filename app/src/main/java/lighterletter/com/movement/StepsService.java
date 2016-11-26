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
import android.text.format.DateUtils;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;

import io.realm.Realm;
import lighterletter.com.movement.Model.DateData;
import lighterletter.com.movement.Model.User;

/**
 * Created by john on 11/23/16.
 */

public class StepsService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private User user;

    private static StepsService instance;

    public static synchronized StepsService getInstance() {
        if (instance == null){
            instance = new StepsService();
            return instance;
        }
        return instance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int
            startId) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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
        long timestamp = event.timestamp;
        float value = event.values[0];
        // 1.0 is the value returned by the step detector when a step is detected
        if (value == 1.0f) {
            storeData(timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, int i) {
    }

    private void storeData(final long timestamp){
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                String date = DateUtils.formatDateTime(getApplicationContext(), timestamp, DateUtils.FORMAT_SHOW_DATE);
                user = RealmUtil.getInstance().getSavedUser();
                if (user.getData() == null) {
                    onErrorToLoginScreen("There was an error. Sign in again.");
                } else {

                    //add +1 to # of steps in data for user.

                }
            }
        });
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
