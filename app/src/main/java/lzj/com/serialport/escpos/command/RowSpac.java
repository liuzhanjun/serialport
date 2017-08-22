package lzj.com.serialport.escpos.command;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by yunniu on 2017/8/22.
 */

public enum   RowSpac  implements Command{

    ROW_SPAC
    ;


    @Override
    public void write(OutputStream out) throws IOException {
        out.write(0);
    }
    public void write(OutputStream out,int n) throws IOException {
        out.write(0x1B);
        out.write(0x33);
        out.write(n);
    }

    @Override
    public void uncheckedWrite(OutputStream out) {

    }
}
