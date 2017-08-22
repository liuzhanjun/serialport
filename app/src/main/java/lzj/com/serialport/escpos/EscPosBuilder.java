package lzj.com.serialport.escpos;

import lzj.com.serialport.escpos.command.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EscPosBuilder {

    private final ByteArrayOutputStream out;

    public EscPosBuilder() {
        this.out = new ByteArrayOutputStream();
    }

    public EscPosBuilder raw(int val) throws IOException {
        Raw.Instance.write(out, val);
        return this;
    }

    /**
     * 行距
     * @param n
     * @return
     * @throws IOException
     */
    public EscPosBuilder RowSpac(int n) throws IOException {
        RowSpac.ROW_SPAC.write(out, n);
        return this;
    }

    public EscPosBuilder raw(byte val) throws IOException {
        Raw.Instance.write(out, val);
        return this;
    }

    public EscPosBuilder raw(byte... vals) throws IOException {
        if (vals != null)
            Raw.Instance.write(out, vals);
        return this;
    }

    public EscPosBuilder text(String text) throws IOException {
        if (text != null)
            Raw.Instance.write(out, text);
        return this;
    }

    public EscPosBuilder initialize() {
        Initialize.Instance.uncheckedWrite(out);
        return this;
    }

    public EscPosBuilder feed() {
        Feed.Instance.uncheckedWrite(out);
        return this;
    }

    public EscPosBuilder feed(int lines) throws IOException {
        FeedLines.Instance.write(out, lines <= 0 ? 1 : lines);

        return this;
    }

    public EscPosBuilder nextLine(){
        out.write(0x1B);
        out.write(0x64);
        out.write(1);
        return this;
    }

    public EscPosBuilder font(Font font) {
        if (font != null)
            font.uncheckedWrite(out);
        return this;
    }

    public EscPosBuilder align(Align align) {
        if (align != null)
            align.uncheckedWrite(out);
        return this;
    }

    public EscPosBuilder cut(Cut cut) {
        if (cut != null)
            cut.uncheckedWrite(out);
        return this;
    }

    public EscPosBuilder kick(DrawerKick kick) throws IOException {
        if (kick != null)
            kick.write(out);
        return this;
    }

    public EscPosBuilder kick(DrawerKick kick, int t1Pulse, int t2Pulse) throws IOException {
        if (kick != null)

            kick.write(out, t1Pulse <= 0 ? 0 : t1Pulse, t2Pulse <= 0 ? 0 : t2Pulse);

        return this;
    }

    public byte[] getBytes() {
        return out.toByteArray();
    }

    public EscPosBuilder reset() {
        out.reset();
        return this;
    }

    @Override
    public String toString() {
        return out.toString();
    }

}