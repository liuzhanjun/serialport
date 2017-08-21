package lzj.com.serialport.serialPort;

public interface ReciveSaoResutListener{
        public void ResutJF(String resut);//积分核销的二维码
        public void ResutYue(String resut);//余额的二维码
        public void ResutSAoMA(String resut);//扫描的二维码
    }