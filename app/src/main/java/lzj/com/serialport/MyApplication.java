package lzj.com.serialport;

import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import lzj.com.serialport.serialPort.SerialHelper;

/**
 * Created by yunniu on 2017/8/21.
 */

public class MyApplication  extends Application{

    private SerialHelper helper;
    private String TAG="MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        helper = SerialHelper.serialManager.getInstener();
        boolean isopen = helper.open("/dev/ttyS2");
        Log.i(TAG, "onCreate: isopen=" + isopen);
    }
}
