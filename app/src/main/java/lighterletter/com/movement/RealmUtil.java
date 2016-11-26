package lighterletter.com.movement;


import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import lighterletter.com.movement.Model.Entry;
import lighterletter.com.movement.Model.User;

import static io.fabric.sdk.android.Fabric.TAG;

/**
 * Created by john on 11/22/16.
 */

public class RealmUtil {

    private User user;
    private static RealmUtil instance;
    private int todaysSteps;
    private int totalSteps;

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

    public User findUser(String email, Realm database) {
        RealmResults<User> users = database.where(User.class).findAll();
        for (User user : users) { //iterate through database
            if (email.equals(user.getEmail())) {//if user is found
                return user;
            }
        }
        return null;
    }

    public void setUser(User currentUser) {
        this.user = currentUser;
    }

    public User getSavedUser() {
        if (user != null) {
            return user;
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

    public void addToOverallStepsForToday(final long timestamp, Context ctx) {
        Realm realm = Realm.getDefaultInstance();
        final String date = DateUtils.formatDateTime(ctx, timestamp, DateUtils.FORMAT_SHOW_DATE);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmList entryList;
                Entry entry;

                if (user.getEntryList() == null) {
                    entryList = new RealmList<Entry>();
                    user.setEntryList(entryList);
                }

                entryList = user.getEntryList();

                //If there is no entry for the date
                if (entryList.where().equalTo("date", date).findFirst() == null) {

                    entry = new Entry();
                    entry.setDate(date);
                    entry.setSteps(entry.getSteps() + 1);

                    user.getEntryList().add(entry);
                    realm.copyToRealmOrUpdate(user);

                    todaysSteps = entry.getSteps();

                    //if date exists
                } else {

                    entry = (Entry) entryList.where().equalTo("date", date).findFirst();
                    entry.setSteps(entry.getSteps() + 1);
                    realm.copyToRealmOrUpdate(user);

                    todaysSteps = entry.getSteps();
                }
            }
        });

    }

    public int getTodaysSteps(){
        return todaysSteps;
    }


}
