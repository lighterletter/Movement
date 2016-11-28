package lighterletter.com.movement;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import lighterletter.com.movement.Model.User;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = BuildConfig.TWITTER_KEY;
    private static final String TWITTER_SECRET = BuildConfig.TWITTER_SECRET;
    private static final String TAG = ".MainActivity";

    private TextView title;
    private TextView totalStepsTV;
    private TextView weeklyStepsTV;
    private TextView dailyStepsTV;

    private Realm realm;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Realm.init(getApplicationContext());
        restartRealm(); //open db 'Realm' if null
        setContentView(R.layout.activity_main);
        initViews();

        String currentUserKey = SaveSharedPreference.getUserKey(getApplicationContext());

        // If no user is saved(logged) in, go to login screen
        if (currentUserKey.isEmpty()) {
            closeRealm();
            Log.d(TAG, "starting login activity userNameVal is: " + currentUserKey);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            //else make sure that the user Key saved  in currentUserKey exists in database
        } else if (RealmUtil.getInstance().findUser(currentUserKey) != null) {

            //Get and setContext global user object
            currentUser = RealmUtil.getInstance().findUser(currentUserKey);
            RealmUtil.getInstance().setUser(currentUser);

            //TODO: store values in db through util, even in background
            beginStepService();

            //Welcome!
            setWelcomeMessage();
            closeRealm();

            //Todo: working on this until it is finished.

            //TODO;getValues from util
            setViewsValues(currentUserKey);

            //setOfficeTimeListener();

        }

        Button shareBtn = (Button) findViewById(R.id.test_share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createShareAction();
            }
        });

        Button logOutBtn = (Button) findViewById(R.id.logout_button);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildLogOutDialog();
            }
        });

    }

    private void setViewsValues(String userKey) {
            dailyStepsTV.setText("Today: " + RealmUtil.getInstance().getTodaysSteps(userKey, System.currentTimeMillis()) + " steps");
    }

    private void initViews() {
        title = (TextView) findViewById(R.id.title);
        totalStepsTV = (TextView) findViewById(R.id.total_steps_text_view);
        weeklyStepsTV = (TextView) findViewById(R.id.this_week_steps_text_view);
        dailyStepsTV = (TextView) findViewById(R.id.today_steps_text_view);
    }

    private void setWelcomeMessage() {

        if (currentUser != null) {
            String welcomeMessage = "Welcome " + currentUser.getUserName() + " !";
            title.setText(welcomeMessage);
        } else {
            title.setText(R.string.welcome);
        }

    }

    private void beginStepService() {
        Log.d("user main activity: ", "beginStepservice Method called");

        StepsService stepService = StepsService.startInstance();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepService.setSensorManager(sensorManager);
        stepService.setContext(getApplicationContext());
        Intent service = new Intent(this, stepService.getClass());
        stepService.onStartCommand(service, Service.START_FLAG_RETRY, Service.START_STICKY);
        startService(service);
    }

    private void createShareAction() {
        String message = "Today I've taken { val } steps towards awesomeness with Movement for Android";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share your steps"));
    }

    private void buildLogOutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of this account?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void logOut() {
        Twitter.logOut();
        SaveSharedPreference.clearUserKey(getApplicationContext());
        closeRealm();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void restartRealm() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
    }

    private void closeRealm() {
        if (realm != null) {
            realm.close();
        }
    }


    //------- Lifecycle methods-------//
    @Override
    protected void onResume() {
        super.onResume();
        restartRealm();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restartRealm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeRealm();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeRealm();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeRealm();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public Realm deleteRestartRealm(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        try {
            deleteRealm();
            return Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                return Realm.getInstance(realmConfiguration);
            } catch (Exception ex){
                throw ex;
                //No Realm file to remove.
            }
        }
    }

    public void deleteRealm(){
        try {
            realm.close();
            Realm.deleteRealm(realm.getConfiguration());
            //Realm file has been deleted.
        } catch (Exception ex){
            ex.printStackTrace();
            //No Realm file to remove.
        }
    }

}
