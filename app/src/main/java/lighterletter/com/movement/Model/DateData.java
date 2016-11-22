package lighterletter.com.movement.Model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by john on 11/22/16.
 */

public class DateData extends RealmObject{

    @Required
    private String date;

    private int steps;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
