package lzj.com.serialport.escpos.command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 表格<br/>
 * 表格从第2列开始，每列之前都会增加一个空格位，也就是说这些列的实际可用宽度会少一个字条位置。 <br/>
 * 如：{10,10,10}的列宽设置，而列的实际可用字符位置数为：{10,9,9} <br/>
 * 输出完成后自动换新行
 */
public class Table{

    /** 表头内容 */
    private String[] headers;

    /** 每个单元格的宽度（字符数），中文算两个，英文算一个 */
    private int[] columnWidths;

    /** 是否靠右打印，默认为靠左打印 */
    private boolean[] alignRights;

    /** 行内容 */
    private ConcurrentHashMap<String[],String> rows = new ConcurrentHashMap<String[],String>();

    /**
     * 线行
     */
    public static final String[] LINE_ROW = new String[] { "-" };

    /**
     * 构造一个表格
     *
     * @param headers
     *            表头
     * @param columnWidths
     *            列宽
     */
    public Table(String[] headers, int[] columnWidths) {
        this.headers = headers;
        this.columnWidths = columnWidths;
    }

    /**
     * 构造一个表格
     *
     * @param headers
     *            表头
     * @param columnWidths
     *            表头
     * @param alignRights
     *            列对齐
     */
    public Table(String[] headers, int[] columnWidths, boolean[] alignRights) {
        if (headers.length != columnWidths.length || headers.length != alignRights.length) {
            throw new RuntimeException("The number of headers & cellWidths & alignRights must be equal!");
        }
        this.headers = headers;
        this.columnWidths = columnWidths;
        this.alignRights = alignRights;
    }

    public String[] getHeaders() {
        return headers;
    }

    public int[] getColumnWidths() {
        return columnWidths;
    }

    public boolean[] getAlignRights() {
        return alignRights;
    }

    public void addOneRow(String[] lineRow,String attrs) {
         rows.put(lineRow,attrs);
    }

    public void addManyRows(ConcurrentHashMap<String[],String> many_rows) {
         rows.putAll(many_rows);
    }

    public ConcurrentHashMap<String[],String> getRows() {
        return rows;
    }

    public boolean isZhongWen(char name) {
        if ((name >= 0x4e00) && (name <= 0x9fbb)) {
            return true;
        }
        return false;
    }


    public void write(OutputStream out) throws IOException {
        //先打头
        for (String header : headers) {
            Raw.Instance.write(out,header);
        }


        Raw.Instance.write(out);
    }

}
