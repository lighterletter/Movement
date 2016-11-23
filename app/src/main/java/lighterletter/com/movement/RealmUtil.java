package lighterletter.com.movement;


import android.content.Intent;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import lighterletter.com.movement.Model.DateData;
import lighterletter.com.movement.Model.User;

import static io.fabric.sdk.android.Fabric.TAG;

/**
 * Created by john on 11/22/16.
 */

public class RealmUtil {

    private static RealmUtil instance;

    public static synchronized RealmUtil getInstance() {
        if (instance == null) {
            return new RealmUtil();
        }
        return instance;
    }

    public boolean checkUserCreds(String email, String password, Realm database) {
        RealmResults<User> users = database.where(User.class).findAll();
        for (User user : users) { //iterate through database
            if (email.equals(user.getEmail()) && password.equals(user.getPassword())) {
                //returns true is user email/password match
                return true;
            }
        }
        //returns false otherwise
        return false;
    }

    public User getUser(String email, Realm database) {
        RealmResults<User> users = database.where(User.class).findAll();
        for (User user : users) { //iterate through database
            if (email.equals(user.getEmail())) {//if user is found
                return user;
            }
        }
        return null;
    }

    public boolean isUser(String email, Realm database) {
        RealmResults<User> users = database.where(User.class).findAll();
        for (User user : users) { //iterate through database
            if (email.equals(user.getEmail())) {//if user is found
                Log.e(TAG, user.getEmail());
                return true;
            }
        }
        return false;
    }

    public void storeUser(String userName, String email, String password, String confirmPassword) {
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            User user = new User();
            user.setUserName(userName);
            user.setEmail(email);
            user.setPassword(password);
            realm.copyToRealm(user);
            realm.commitTransaction();
            realm.close();

        } catch (RealmPrimaryKeyConstraintException e) {
            e.printStackTrace();
        }
    }

}
