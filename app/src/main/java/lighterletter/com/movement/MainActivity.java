package lighterletter.com.movement;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import lighterletter.com.movement.Model.User;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = BuildConfig.TWITTER_KEY;
    private static final String TWITTER_SECRET = BuildConfig.TWITTER_SECRET;
    private static final String TAG = ".MainActivity";

    private TextView title;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_main);

        initViews();

        String currentUserKey = SaveSharedPreference.getUserKey(getApplicationContext());

        Log.d("saveduser", currentUserKey);
        if (currentUserKey.isEmpty()) {
            // if no user is logged in go to login screen
            Log.d(TAG, "starting login activity userNameVal is: " + currentUserKey);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if(RealmUtil.getInstance().getUser(currentUserKey,realm) != null) {

            RealmQuery<User> query = realm.where(User.class);
            query.equalTo("email", currentUserKey);
            RealmResults<User> result = query.findAll();
            User currentUser = result.get(0);
            Log.d("databse", result.toString());
            String welcomeMessage = "Welcome " + currentUser.getUserName();
            title.setText(welcomeMessage);
            realm.close();
        }

        Button shareBtn = (Button) findViewById(R.id.test_share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createShareAction();
            }
        });

        final Button logOutBtn = (Button) findViewById(R.id.logout_button);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildLogOutDialog();
            }
        });
    }

    public Realm realmMigration(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        try {
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


    private void createShareAction(){
        String message = "Today I've taken { val } steps towards awesomeness with Movement for Android";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share your steps"));
    }

    private void initViews() {
        title = (TextView) findViewById(R.id.title);
    }

    @Override
    public void onBackPressed() {
        buildLogOutDialog();
    }

    private void buildLogOutDialog(){
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

    private void logOut(){
        Twitter.logOut();
        SaveSharedPreference.clearUserKey(getApplicationContext());
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

}
