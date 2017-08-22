package lzj.com.serialport.escpos.command;

import java.io.IOException;
import java.io.OutputStream;

public enum Raw implements Command {

    Instance;

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(0);
    }

    public void write(OutputStream out, int val) throws IOException {
        out.write(val);
    }

    public void write(OutputStream out, byte val) throws IOException {
        out.write(val);
    }

    public void write(OutputStream out, String string) throws IOException {
        out.write(string.getBytes("gb2312"));

    }

    public void write(OutputStream out, byte[] vals) throws IOException {
        out.write(vals);
    }

    @Override
    public void uncheckedWrite(OutputStream out) {
        try {
            write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
