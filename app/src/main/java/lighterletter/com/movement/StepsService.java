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
import io.realm.RealmQuery;
import io.realm.RealmResults;
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

    private static StepsService instance;

    public static synchronized StepsService getInstance() {
        if (instance == null){
            instance = new StepsService();
            return instance;
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int
            startId) {

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //Todo: might be better to save and get user from util rather than query using key stored in sharedpref.

                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String savedUserKey = SaveSharedPreference.currentUser;

                RealmQuery<User> query = realm.where(User.class);
                query.equalTo("email", savedUserKey);
                RealmResults<User> result = query.findAll();
                user = result.get(0);

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


}
