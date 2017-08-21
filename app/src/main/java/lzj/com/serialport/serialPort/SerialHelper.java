package lzj.com.serialport.serialPort;

import android.os.Handler;
import android.os.HandlerThread;
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
    private boolean isReading=true;


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

        read.interrupt();
        read=null;
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

    public  class ReadThread extends HandlerThread{
        public ReadThread() {
            super("ReadThread");
        }

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


    /**
     * 行间距 设置行间距 (SetLinespace)
     * 设置 汉字 放大(  SetSizechinese)
     * 设置 设置 字符 放大( ( SetSize char) )
     * 置字体加粗 设置字体加粗(SetBold)
     * 设置 设置 字符对齐(SetAlignment)
     */


    public void sendMessageBig(String str) {
        send(PrintCmd.SetAlignment(1));//对齐类型，0 左对齐 、1 居中对齐、2 右对齐
        send(PrintCmd.SetLinespace(96));//小字48，大字96
        send(PrintCmd.SetSizechar(1, 1, 0, 0));//字符两倍
        send(PrintCmd.SetSizechinese(1, 1, 0, 0));//中文两倍
        send(PrintCmd.PrintString(str, 0));
    }

    public void sendMessageSmall(String str, int type) {
        send(PrintCmd.SetSizechar(0, 0, 0, 0));//字符1倍
        send(PrintCmd.SetSizechinese(0, 0, 0, 0));//中文1倍
        send(PrintCmd.SetLinespace(48));//小字48，大字96
        send(PrintCmd.SetAlignment(type));//对齐类型，0 左对齐 、1 居中对齐、2 右对齐
        send(PrintCmd.PrintString(str, 0));
    }

    public void sendMessageSmallB(String str, int type) {
        send(PrintCmd.SetBold(1));//1，加粗，0，不加粗
        send(PrintCmd.SetSizechar(0, 0, 0, 0));//字符1倍
        send(PrintCmd.SetSizechinese(0, 0, 0, 0));//中文1倍
        send(PrintCmd.SetLinespace(48));//小字48，大字96
        send(PrintCmd.SetAlignment(type));//对齐类型，0 左对齐 、1 居中对齐、2 右对齐
        send(PrintCmd.PrintString(str, 0));
    }


    public String dataStringSet2(String food_name, String food_num, String food_price, int num) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        try {

            char[] name_arr = food_name.toCharArray();//字符數
            Log.d("abcd", "name_arr: " + name_arr.length);
            char[] num_arr = food_num.toCharArray();
            Log.d("abcd", "num_arr: " + num_arr.length);
            char[] price_arr = food_price.toCharArray();

            for (int i = 0; i < num; i++) {
                if (i == 0) {
                    if (name_arr.length > 9) {
                        sb.append(name_arr[0]);
                        sb.append(name_arr[1]);
                        sb.append(name_arr[2]);
                        sb.append(name_arr[3]);
                        sb.append(name_arr[4]);
                        sb.append(name_arr[5]);
                        sb.append(name_arr[6]);
                        sb.append(name_arr[7]);
                        sb.append(".");
                        sb.append(".");
                        sb.append(name_arr[name_arr.length - 1]);
                        int znum = 0;
                        char[] chars = sb.toString().toCharArray();
                        for (int j = 0; j < chars.length; j++) {
                            if (isZhongWen(chars[j])) {
                                znum++;//中文個數
                            }
                        }
                        Log.d("dataStringSet2", "znum: " + znum);
                        for (int j = 0; j < 9 - znum; j++) {
                            //一個中文占2位，數字占1位
                            sb.append(" ");//40-4*znum =
                        }
//                        sb.append(" ");
                        i = 20;
                    } else {
                        int zhongwen = 0;
                        for (int j = 0; j < name_arr.length; j++) {
                            sb.append(name_arr[j]);
                            if (isZhongWen(name_arr[j])) {
                                zhongwen++;
                            }
                        }
                        i = zhongwen * 2 + name_arr.length - zhongwen;
                        Log.d("dataStringSet2", "dataStringSet2: i--->" + i);
//                        sb.append(" ");

                    }
                } else if (i == 22) {
                    for (int j = 0; j < num_arr.length; j++) {
                        sb.append(num_arr[j]);
                        i++;
                    }
                } else if (i == 26) {
                    for (int j = 0; j < price_arr.length; j++) {
                        sb.append(price_arr[j]);
                        i++;
                    }
                } else {
                    sb.append(" ");
                }
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isZhongWen(char name) {
        if ((name >= 0x4e00) && (name <= 0x9fbb)) {
            return true;
        }
        return false;
    }

    // 打印二维码
    private void PrintQRCode(String url) throws IOException {
        send(PrintCmd.PrintQrcode(url, 25, 7, 1));
    }


    // 走纸换行，再切纸，清理缓存
    public void PrintFeedCutpaper(int iLine) throws IOException {
       send(PrintCmd.PrintFeedline(iLine));
       send(PrintCmd.PrintCutpaper(1));
       send(PrintCmd.SetClean());
    }

}
