package lighterletter.com.movement.Model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by john on 11/22/16.
 */

public class User extends RealmObject {


    @Required
    private String userName;

    @PrimaryKey
    private String email;

    @Required
    private String password;

    private RealmList<Entry> entryList;

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

    public RealmList<Entry> getEntryList() {
        return entryList;
    }

    public void setEntryList(RealmList<Entry> entry) {
        this.entryList = entry;
    }
}
