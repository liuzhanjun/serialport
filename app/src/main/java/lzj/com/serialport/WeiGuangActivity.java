package lzj.com.serialport;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import lzj.com.serialport.serialPort.ReciveSaoResutListener;
import lzj.com.serialport.serialPort.SerialHelper;

/**
 * 微光设备操作
 */
public class WeiGuangActivity extends AppCompatActivity implements View.OnClickListener{

    private Button openLight,closeLight;
    private TextView saoResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_guang);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        openLight= (Button) findViewById(R.id.openLight);
        openLight.setOnClickListener(this);
        closeLight= (Button) findViewById(R.id.closeLight);
        closeLight.setOnClickListener(this);
        saoResult= (TextView) findViewById(R.id.saoResult);
        SerialHelper.serialManager.open("/dev/ttyS2");

        SerialHelper.serialManager.setSaoMaoListenre(new ReciveSaoResutListener() {
            @Override
            public void ResutJF(String resut) {

            }

            @Override
            public void ResutYue(String resut) {

            }

            @Override
            public void ResutSAoMA(final String resut) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        saoResult.setText("扫码内容："+resut);
                        Toast.makeText(WeiGuangActivity.this,"扫码内容："+resut,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        SerialHelper.serialManager.setSaoMaoListenre(null);
        SerialHelper.serialManager.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.openLight:
                SerialHelper.serialManager.kaideng("微光c100");
                SerialHelper.serialManager.kaiqisaoma("微光c100");
                break;
            case R.id.closeLight:
                SerialHelper.serialManager.guandeng("微光c100");
                SerialHelper.serialManager.closesaoma("微光c100");
                break;
        }
    }

    public void back(View view){
        finish();
    }
}
