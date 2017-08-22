package lzj.com.serialport.escpos.command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public interface Command {

    void write(OutputStream out) throws IOException;

    void uncheckedWrite(OutputStream out);

}
