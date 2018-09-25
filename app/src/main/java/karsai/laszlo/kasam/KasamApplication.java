package karsai.laszlo.kasam;

import android.app.Application;
import android.content.Context;

public class KasamApplication extends Application {

    private Context mContext;

    @Override
    public void onCreate() {
        this.mContext = getApplicationContext();
        super.onCreate();
    }

    public Context getContext() {
        return mContext;
    }
}
