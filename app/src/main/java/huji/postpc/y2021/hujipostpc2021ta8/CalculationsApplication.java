package huji.postpc.y2021.hujipostpc2021ta8;

import android.app.Application;
import java.io.Serializable;

public class CalculationsApplication extends Application implements Serializable {

    private CalculationsDB database;

    public CalculationsDB getDatabase() {
        return database;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = new CalculationsDB(this);
    }

    private static CalculationsApplication instance = null;

    public static CalculationsApplication getInstance() {
        return instance;
    }
}