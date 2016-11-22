package lighterletter.com.movement.Model;

import android.support.annotation.Nullable;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by john on 11/22/16.
 */

public class User extends RealmObject {

    @Required
    private String userId;

    @Required
    private String userName;

    @Required
    private String email;

    @Required
    private String password;

    private RealmList<DateData> data;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<DateData> getData() {
        return data;
    }

    public void setData(RealmList<DateData> data) {
        this.data = data;
    }
}
