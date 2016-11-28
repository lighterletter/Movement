package lighterletter.com.movement;


import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

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

    private static User user;
    private static RealmUtil instance;
    private int todaysSteps;
    private int totalSteps;

    public static synchronized RealmUtil getInstance() {
        if (instance == null) {
            return new RealmUtil();
        }
        return instance;
    }

    public boolean checkUserCreds(String email, String password) {
        Realm database = Realm.getDefaultInstance();
        RealmResults<User> users = database.where(User.class).findAll();
        for (User user : users) { //iterate through database
            if (email.equals(user.getEmail()) && password.equals(user.getPassword())) {
                //returns true is user email/password match
                database.close();
                return true;
            }
        }
        database.close();
        //returns false otherwise
        return false;
    }

    public User findUser(String email) {
        Realm database = Realm.getDefaultInstance();
        RealmResults<User> users = database.where(User.class).findAll();
        for (User user : users) { //iterate through database
            if (email.equals(user.getEmail())) {//if user is found
                database.close();
                return user;
            }
        }
        database.close();
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
            RealmUtil.getInstance().setUser(user);
            realm.commitTransaction();
            realm.close();

        } catch (RealmPrimaryKeyConstraintException e) {
            e.printStackTrace();
        }
    }

    public void addToOverallStepsForToday(final long timestamp, Context ctx) {
        Log.d("userlist: ", "addto over all steps called");

        Realm realm = Realm.getDefaultInstance();
        final String date = getDate(timestamp);
        final User user = RealmUtil.getInstance().findUser(SaveSharedPreference.getUserKey(ctx));
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmList entryList;
                Entry entry;
                Log.d("user transaction: ", "user name: " + user.getUserName());

                if (user.getEntryList() == null) {
                    Log.d("user list: ", " user list not found");
                    entryList = new RealmList<Entry>();
                    user.setEntryList(entryList);
                } else {
                    Log.d("user list: ", " user list found");
                    entryList = user.getEntryList();
                }


                //If there is no entry for the date
                if (entryList.where().equalTo("date", date).findFirst() == null) {

                    Log.d("user date: ", "date not found");
                    Log.d("user date: ", "date: " + date);

                    entry = new Entry();
                    entry.setDate(date);
                    entry.setSteps(entry.getSteps() + 1);

                    user.getEntryList().add(entry);
                    realm.copyToRealmOrUpdate(user);

                    //if date exists
                } else {

                    Log.d("user date: ", " date found");
                    Log.d("user date: ", "date: " + date);

                    entry = (Entry) entryList.where().equalTo("date", date).findFirst();
                    entry.setSteps(entry.getSteps() + 1);
                    realm.copyToRealmOrUpdate(user);

                    todaysSteps = entry.getSteps();
                    Log.d("user steps: ", "steps: " + todaysSteps);

                }
            }
        });

    }

    public int getTodaysSteps(String userEmail, long time) {
        Realm realm = Realm.getDefaultInstance();
        User user = findUser(userEmail);
        RealmList<Entry> list = user.getEntryList();
        Entry today = (Entry) list.where().equalTo("date",getDate(time)).findFirst();
        if (today == null){
            return 1;
        }
        todaysSteps = today.getSteps();
        realm.close();
        return todaysSteps;
    }

    private String getDate(long time) {
        //TODO: This belongs somewhere else. (Nothing to do with Realm)
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

}
