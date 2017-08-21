package lzj.com.serialport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import lzj.com.serialport.serialPort.ReciveSaoResutListener;
import lzj.com.serialport.serialPort.SerialHelper;

public class PrintActivity extends AppCompatActivity {

    public static final String TAG="PrintActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        SerialHelper.serialManager.open("/dev/ttyS3");
        SerialHelper.serialManager.setSaoMaoListenre(new ReciveSaoResutListener() {
            @Override
            public void ResutJF(String resut) {

            }

            @Override
            public void ResutYue(String resut) {

            }

            @Override
            public void ResutSAoMA(String resut) {

            }

        });
    }


    public void print(View view){
        Log.i(TAG, "print: =================================");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SerialHelper.serialManager.sendMessageBig("bsss");
                try {
                    SerialHelper.serialManager.PrintFeedCutpaper(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
