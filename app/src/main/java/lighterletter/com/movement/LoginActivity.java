package lighterletter.com.movement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import io.realm.RealmResults;
import lighterletter.com.movement.Model.User;

/**
 * Created by john on 11/22/16.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Home";
    private EditText loginEmail, mEditTextPassword;
    private Button loginButton;
    private Button registerButton;
    private Realm realm;

    private TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        realm = Realm.getDefaultInstance();

        initViews();
        initLogIn();
        initSignUp();
        setUpTwitterogIn();
    }

    private void initViews() {
        loginEmail = (EditText) findViewById(R.id.login_email_edit_text);
        mEditTextPassword = (EditText) findViewById(R.id.login_password_edit_text);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.login_register_button);
    }

    private void initLogIn() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = String.valueOf(loginEmail.getText());
                String password = String.valueOf(mEditTextPassword.getText());

                if ((email.isEmpty())) {
                    showToast("Enter a valid e-mail address");
                    loginEmail.requestFocus();
                } else if (password.isEmpty()) {
                    showToast("Enter password");
                    mEditTextPassword.requestFocus();
                } else {
                    if (checkUser(email, password)) {
                        SaveSharedPreference.setUserName(getApplicationContext(),email);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }
            }

            private boolean checkUser(String email, String password) {
                RealmResults<User> users = realm.where(User.class).findAll();
                String userFound = "";

                for (User user : users) { //iterate through database

                    if (email.equals(user.getEmail())) {//if user is found

                        userFound = user.getEmail(); // set field for toast

                        if (email.equals(user.getEmail()) && password.equals(user.getPassword())) {

                            Log.e(TAG, user.getEmail());
                            return true;
                        }
                    }
                }

                if ( ! userFound.isEmpty() ) {
                    showToast("Password for " + userFound + " incorrect");
                } else {
                    showToast("User not found");
                }
                return false;
            }

            private void showToast(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void initSignUp() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public void setUpTwitterogIn() {
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()

                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID

                //use id as shared prefs name and e-mail field.
                //save username as name


                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
    }
}