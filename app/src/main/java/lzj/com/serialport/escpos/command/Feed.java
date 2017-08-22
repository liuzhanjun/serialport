package lzj.com.serialport.escpos.command;

import java.io.IOException;
import java.io.OutputStream;

public enum Feed implements Command {

    Instance;

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(0xA);
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
