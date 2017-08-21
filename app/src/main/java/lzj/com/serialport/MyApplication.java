package lzj.com.serialport;

import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import lzj.com.serialport.serialPort.SerialHelper;

/**
 * Created by yunniu on 2017/8/21.
 */

public class MyApplication  extends Application{

    private String TAG="MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        SerialHelper.serialManager.getInstener();

    }
}
