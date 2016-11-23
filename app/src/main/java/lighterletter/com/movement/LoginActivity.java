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

    private TwitterLoginButton twitterLoginButton;

    private Realm realm;
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

                    if (RealmUtil.getInstance().checkUserCreds(email, password,realm)) {

                        //save user login
                        SaveSharedPreference.setUserKey(getApplicationContext(), email);
                        showToast("User: " + RealmUtil.getInstance().getUser(email,realm).getEmail() + " logged in!");
                        realm.close();
                        goToMainActivity();

                    } else if (! RealmUtil.getInstance().isUser(email,realm)) {

                        //user found but password is not a match
                        showToast("Password for " + email + " incorrect");
                        realm.close();
                        mEditTextPassword.requestFocus();
                    } else {
                        showToast("User not found");
                    }
                }
            }

        });
    }


    private void initSignUp() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });
    }

    public void setUpTwitterLogin() {
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;

                String userKey = String.valueOf(session.getUserId());
                String userName = session.getUserName();

                RealmUtil.getInstance().storeUser(userName, userKey, userKey, userKey);
                SaveSharedPreference.setUserKey(getApplicationContext(), userKey);
                String msg = "@" + session.getUserName() + " logged in!";
                showToast(msg);
                goToMainActivity();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
                showToast("log in failed, check network or sign up locally");
                goToRegisterActivity();
            }
        });
    }

    private void goToMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private void goToRegisterActivity(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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