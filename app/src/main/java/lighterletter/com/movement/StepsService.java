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

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import lighterletter.com.movement.Model.DateData;
import lighterletter.com.movement.Model.User;

/**
 * Created by john on 11/23/16.
 */

public class StepsService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Realm realm;
    private User user;


    public StepsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int
            startId) {
        user = intent.getParcelableExtra("user");
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());;


                if (user.getData() == null) {

                    RealmList<DateData> newUserList = new RealmList<DateData>();
                    DateData entry = new DateData(date, 1);
                    newUserList.add(entry);
                    user.setData(newUserList);
                    realm.copyToRealmOrUpdate(user);

                } else {
                     DateData dateData = realm.where(DateData.class)
                            .equalTo("data.date", date).findFirst();
                    dateData.setSteps(dateData.getSteps() +1);

                }
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
