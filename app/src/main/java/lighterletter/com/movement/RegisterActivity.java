package lighterletter.com.movement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = String.valueOf(userNameET.getText());
                String email = String.valueOf(userEmailET.getText());
                String password = String.valueOf(userPwET.getText());
                String confirmPassword = String.valueOf(userConfirmPwET.getText());

                if (userName.length() == 0) {
                    showSnackBar("Enter FirstName");
                    userNameET.requestFocus();
                } else if (email.length() == 0) {
                    showSnackBar("Enter a valid email");
                    userEmailET.requestFocus();
                } else if (password.length() == 0) {
                    showSnackBar("Enter a valid password");
                    userPwET.requestFocus();
                } else if (confirmPassword.length() == 0) {
                    showSnackBar("Enter a valid Password");
                    userPwET.requestFocus();
                } else {

                    if(password.equals(confirmPassword)){
                        try {
                            realm.beginTransaction();
                            user = realm.createObject(User.class);
                            user.setUserName(userName);
                            user.setEmail(email);
                            user.setPassword(password);

                            if (userPwET.getText().toString().equals(userConfirmPwET.getText().toString())) {
                                realm.commitTransaction();
                                showSnackBar("Save Success");
                                SaveSharedPreference.setUserName(getApplicationContext(),userName);
                                startActivity(new Intent(RegisterActivity.this , MainActivity.class));
                            } else {
                                showSnackBar("save failed, make sure passwords match");
                                realm.cancelTransaction();
                                onClick(v);
                            }

                        } catch (RealmPrimaryKeyConstraintException e) {
                            e.printStackTrace();
                            showSnackBar("User found on db.");
                        }
                    }
                }
            }
        });

    }

    private void initViews(){
        userNameET = (EditText) findViewById(R.id.register_name);
        userEmailET = (EditText) findViewById(R.id.register_email);
        userPwET = (EditText) findViewById(R.id.register_password);
        userConfirmPwET = (EditText) findViewById(R.id.register_confirm_password);
        registerButton = (Button) findViewById(R.id.register_submit_button);
    }

    private void showSnackBar(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}