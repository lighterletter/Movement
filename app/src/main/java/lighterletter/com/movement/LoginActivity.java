package lighterletter.com.movement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.realm.Realm;

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

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initViews();

        initLogIn();
        initSignUp();
        setUpTwitterLogin();
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

                realm = Realm.getDefaultInstance();
                email = String.valueOf(loginEmail.getText()).toLowerCase();
                password = String.valueOf(mEditTextPassword.getText());

                if (email.isEmpty()) {

                    showToast("Enter a valid e-mail address");
                    loginEmail.requestFocus();

                } else if (password.isEmpty() || password.length() < 4) {

                    showToast("Enter valid password (4 or more characters)");
                    mEditTextPassword.requestFocus();

                } else {

                    RealmUtil util = RealmUtil.getInstance();

                    if (util.checkUserCreds(email, password, realm)) {

                        //save user login
                        SaveSharedPreference.setUserName(getApplicationContext(),util.getUser(email,realm).getUserName());
                        realm.close();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    } else if ( ! RealmUtil.getInstance().foundUserEmail(email, realm).isEmpty() ) {
                        //user found but password is not a match
                        showToast("Password for " + email + " incorrect");
                        realm.close();
                    } else  {
                        showToast("User not found");
                        realm.close();
                    }
                }
            }

        });
    }


    private void initSignUp() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realm != null && ! realm.isClosed()){
                    realm.close();
                }
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public void setUpTwitterLogin() {
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                // The TwitterSession is also available through: Twitter.getInstance().core.getSessionManager().getActiveSession()
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

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
        //Empty to prevent backStack navigation
    }

}