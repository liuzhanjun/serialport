package lzj.com.serialport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import lzj.com.serialport.escpos.EscPosBuilder;
import lzj.com.serialport.escpos.command.Align;
import lzj.com.serialport.escpos.command.Cut;
import lzj.com.serialport.escpos.command.Font;
import lzj.com.serialport.serialPort.ReciveSaoResutListener;
import lzj.com.serialport.serialPort.SerialHelper;
import lzj.com.serialport.utils.EscPos;

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
                EscPosBuilder escPos = new EscPosBuilder();
                byte[] data;
                try {
                     data = escPos.initialize()
                             .RowSpac(100)
                            .font(Font.REGULAR)
                            .align(Align.CENTER)
                            .text("龙龙食府-花城店")
                            .nextLine()
                            .font(Font.REGULAR)
                            .align(Align.CENTER)
                            .text("losebbcde")
                             .nextLine()

                            .font(Font.DWDH)
                            .align(Align.CENTER)
                            .text("456789红")
                             .nextLine()
                             .font(Font.REGULAR)
                             .align(Align.CENTER)
                             .text("22222222222222")
                             .feed(15)
                            .cut(Cut.FULL)
                            .getBytes();

                    SerialHelper.serialManager.send(data);
                    escPos.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                SerialHelper.serialManager.sendMessageBig("123456789123456789红");
//                try {
//                    SerialHelper.serialManager.PrintFeedCutpaper(5);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


            }
        }).start();

    }
}
