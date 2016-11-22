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
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
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
        setContentView(R.layout.activity_main);

        initViews();
        String currentUserName = SaveSharedPreference.getUserName(getApplicationContext());
        realm = Realm.getDefaultInstance();

        Log.d("saveduser", currentUserName);
        if (currentUserName.isEmpty()) {
            // if no user is logged in go to login screen
            Log.d(TAG, "starting login activity userNameVal is: " + currentUserName);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {

            RealmQuery<User> query = realm.where(User.class);
            query.equalTo("userName", currentUserName);
            RealmResults<User> result = query.findAll();
            User currentUser = result.get(0);
            Log.d("databse", result.toString());
            title.setText("Welcome " + currentUser.getUserName());
        }

        Button shareBtn = (Button) findViewById(R.id.test_share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Today I've taken { val } steps towards awesomeness with Movement for Android";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "Share your steps"));
            }
        });

        final Button logOutBtn = (Button) findViewById(R.id.logout_button);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void initViews() {
        title = (TextView) findViewById(R.id.title);
    }

    @Override
    public void onBackPressed() {
        buildLogOutDialog();
    }

    private void logOut(){
        SaveSharedPreference.clearUserName(getApplicationContext());
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

}
