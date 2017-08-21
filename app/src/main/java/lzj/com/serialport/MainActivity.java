package lzj.com.serialport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import lzj.com.serialport.serialPort.SerialHelper;
import lzj.com.serialport.utils.SerialPortFinder;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SerialPortFinder finder = new SerialPortFinder();
        String[] devices = finder.getAllDevices();
        for (String device : devices) {
            Log.i(TAG, "onCreate: device=============" + device + "==========" + device);
        }




    }

    public void toWeiguang(View view) {
        Intent intent = new Intent(this, WeiGuangActivity.class);
        startActivity(intent);
    }
}
