package lzj.com.serialport.serialPort;

import android.util.Log;

import com.printsdk.cmd.PrintCmd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lzj.com.serialport.utils.MyFunc;
import lzj.com.serialport.utils.QRUtils;

/**
 * Created by yunniu on 2017/8/21.
 */

public enum  SerialHelper {


    serialManager(){
        @Override
        public SerialHelper getInstener() {

            return this;
        }
    };
    public static final String TAG="SerialHelper";
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private boolean _isOpen;
    private  ReciveSaoResutListener saoMaoListenre;
    private ReadThread read;


    /**
     * 扫码监听
     * @param saoMaoListenre
     */
    public void setSaoMaoListenre(ReciveSaoResutListener saoMaoListenre) {
        this.saoMaoListenre = saoMaoListenre;
    }


    public  abstract  SerialHelper getInstener();

    public boolean open(String devicePath){
        try {
            mSerialPort=new SerialPort(new File(devicePath),9600,2);
            mOutputStream=mSerialPort.getOutputStream();
            mInputStream=mSerialPort.getInputStream();
            read=new ReadThread();
            read.start();
            _isOpen=true;
        } catch (IOException e) {
            e.printStackTrace();
            _isOpen=false;
        }
        return _isOpen;
    }
    public void close(){
        if (mSerialPort != null) {
            mSerialPort.close();
        }
    }


    /**
     * 发送字符串
     * @param sHex
     */
    public void sendHex(String sHex) {
        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    public int send(byte[] bOutArray) {
        int iResult = 0;
        try {
            if (mOutputStream == null)
                return 0;
            if (!_isOpen)
                return 0;

            mOutputStream.write(bOutArray);
            iResult = bOutArray.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iResult;
    }

    /**
     * 根据设备商提供的开灯码
     * @param saomaqi
     */
    public void kaideng(String saomaqi) {

        //先判断用的是哪一种扫码器
        String t = "";
        Log.i("dayingji", "开灯: " + saomaqi);
        if (!"".equals(saomaqi)) {
            if (saomaqi.equals("微光c100")) {
                t = "55AA24010001DB";
//                    byte[] b=t.getBytes();
                //   ComA.sendHex(t);


            } else if (saomaqi.equals("E2系列")) {
                t = "08C6040800F20201FE31";
//                    byte[] b=t.getBytes();
            }


        }else{
            t = "55AA24010001DB";
        }

        sendHex(t);


    }

    /**
     * 关灯
     *
     *
     */
    public void guandeng(String type) {
//先判断用的是哪一种扫码器
        Log.i("dayingji", "关灯: " + type);
        String t = "";
        if (!"".equals(type)) {
            if (type.equals("微光c100")) {
                t = "55AA24010000DA";
//                    byte[] b=t.getBytes();
                //   ComA.sendHex(t);


            } else if (type.equals("E2系列")) {
                t = "08C6040800F20202FE30";
//                    byte[] b=t.getBytes();
            }


        }else{
            t = "55AA24010000DA";
        }
        sendHex(t);

    }
    String kaideng = "55AA24000000DB";
    String tou = "55AA30002000";
    StringBuilder getAllOx = new StringBuilder();

    public  class ReadThread extends Thread{
        @Override
        public void run() {
            while (!isInterrupted()) {
                if (mInputStream != null) {
                    try {
                        byte[] buffer=new byte[1024];
                        int size=mInputStream.read(buffer);
                        Log.i(TAG, "run: ======================"+new String(buffer));
                        String he = Bytes2HexString(buffer);
                        if (he == null) {
                            return;
                        }
                        getAllOx.append(he);
                        Log.i("Saoma", "16进制数据" + getAllOx.toString());
                        //去掉开灯码
                        QRUtils.remove(getAllOx, kaideng);
                        //去掉E20开灯码
                        QRUtils.remove(getAllOx, "04D00000FF2C");
                        if (getAllOx.indexOf(tou) != -1) {
                            Log.i("Saoma", "run: 清空数据");
                            QRUtils.remove(getAllOx, getAllOx.toString());
                            getAllOx.append(he);
                        }
                        if (getAllOx.indexOf("04D00000FF2C") != -1) {
                            Log.i("Saoma", "run: 清空数据");
                            QRUtils.remove(getAllOx, getAllOx.toString());
                            getAllOx.append(he);
                        }
                        QRUtils.remove(getAllOx, tou);
                        QRUtils.remove(getAllOx, "55AA30001200");
                        QRUtils.remove(getAllOx, "55AA30001400");
                        //E20开灯码
                        QRUtils.remove(getAllOx, "08C6040800F20201FE31");

                        Log.i("Saoma", "16进制数据去掉头" + getAllOx.toString());
                        //如果有头的则清空容器

                        String alldate = hexStr2Str(getAllOx.toString());

                        Log.i("Saoma", "alldata=" + alldate);
                        //开启匹配模式
                        String result = QRUtils.getQRStr(alldate);
                        Log.i("Saoma", "run: 扫码内容=====" + result);

                        int len = result.length();
                        if (len==32){
                            //积分核销的二维码
                            if (saoMaoListenre != null) {
                                saoMaoListenre.ResutJF(result);
                            }
                            QRUtils.remove(getAllOx, getAllOx.toString());

                        }else if(len==20){
//                            余额支付二维码
                            if (saoMaoListenre != null) {
                                saoMaoListenre.ResutYue(result);
                                QRUtils.remove(getAllOx, getAllOx.toString());
                            }

                        }else if (len==18){
                            //扫码支付二维码
                            if (saoMaoListenre!=null){
                                saoMaoListenre.ResutSAoMA(result);
                            }
                            QRUtils.remove(getAllOx, getAllOx.toString());

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 十六进制转换字符串
     *
     * @return String 对应的字符串  str Byte字符串(Byte之间无分隔符 如:[616C6B])
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
    private final static byte[] hex = "0123456789ABCDEF".getBytes();
    public static String Bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    /**
     * 开启扫码
     *
     * @param type
     */
    public void kaiqisaoma(String type) {
        String t = "";
        if (!"".equals(type)) {
            if (type.equals("微光c100")) {
                t = "55AA21010001DE";
            } else if (type.equals("E2系列")) {
                t = "04E90400FF0F";
//                    byte[] b=t.getBytes();
            }

            sendHex(t);
            byte[] status4 = PrintCmd.GetStatus4();
            send(status4);
        }



    }

    /**
     * 关闭扫码
     *
     * @param type
     */
    public void closesaoma(String type) {
        String t = "";
        if (!"".equals(type)) {
            if (type.equals("微光c100")) {
                t = "55AA21010000DF";
            } else if (type.equals("E2系列")) {
                t = "04EA0400FF0E";
//                    byte[] b=t.getBytes();
            }

            sendHex(t);
        }
    }

}
