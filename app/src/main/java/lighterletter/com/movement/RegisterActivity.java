package lighterletter.com.movement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import lighterletter.com.movement.Model.User;

/**
 * Created by john on 11/22/16.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";
    EditText userNameET, userEmailET, userPwET, userConfirmPwET;
    Button registerButton;
    Realm realm;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initViews();
        registerNewUser();
    }

    private void initViews() {
        userNameET = (EditText) findViewById(R.id.register_name);
        userEmailET = (EditText) findViewById(R.id.register_email);
        userPwET = (EditText) findViewById(R.id.register_password);
        userConfirmPwET = (EditText) findViewById(R.id.register_confirm_password);
        registerButton = (Button) findViewById(R.id.register_submit_button);
    }


    private void registerNewUser() {
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String userName = String.valueOf(userNameET.getText());
                String email = String.valueOf(userEmailET.getText()).toLowerCase();
                String password = String.valueOf(userPwET.getText());
                String confirmPassword = String.valueOf(userConfirmPwET.getText());

                if (userName.length() == 0) {
                    showToast("Enter a userName");
                    userNameET.requestFocus();
                } else if (email.length() <= 6) {
                    showToast("Enter a valid email");
                    userEmailET.requestFocus();
                } else if (password.length() == 0) {
                    showToast("Enter a valid password");
                    userPwET.requestFocus();
                } else if (confirmPassword.length() == 0) {
                    showToast("Enter a valid Password");
                    userPwET.requestFocus();
                } else {

                    //check database to see if there is already a user with that e-mail address
                    if (realm.where(User.class).equalTo("email", email).count() == 0) {

                        if (password.equals(confirmPassword)) {

                            realm.close();
                            RealmUtil.getInstance().storeUser(userName, email, password, confirmPassword);
                            showToast("Save Success");
                            SaveSharedPreference.setUserKey(getApplicationContext(), email);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                        } else {
                            showToast("Registration failed, make sure passwords match");
                            realm.close();
                            userPwET.requestFocus();
                        }

                    } else {
                        showToast("E-mail already in use");
                    }

                }
            }

        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}