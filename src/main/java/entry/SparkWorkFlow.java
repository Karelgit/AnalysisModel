package entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import util.HbasePoolUtils;

/**
 * Created by Administrator on 2016/5/30.
 */
public class SparkWorkFlow {
    private static Configuration conf = null;

    public static void check() {
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("crawlerData"));
        scan.addColumn(Bytes.toBytes("crawlerData"),Bytes.toBytes("url"));
        ResultScanner rs = null;
        try {
            HTableInterface table = HbasePoolUtils.getHTable("gycrawler");
            rs = table.getScanner(scan);
            int i = 0;
            for (Result r : rs) {
                i++;
                for (Cell cell : r.rawCells()) {
                    String s = Bytes.toString(CellUtil.cloneRow(cell));
                    String s1 = Bytes.toString(CellUtil.cloneQualifier(cell));
                    String s2 = Bytes.toString(CellUtil.cloneValue(cell));
                    System.out.println("row: " + s + "; qualifier: " + s1 + "; value: " + s2);
                }
                System.out.println("-------------------------------------------");
                for (KeyValue kv : r.list()) {
                    System.out.println("timestamp:" + kv.getTimestamp());
                    System.out.println("row:" + Bytes.toString(kv.getRow()) + ";\nfamily:" +
                            Bytes.toString(kv.getFamily()) +  ";\nqualifier: " +
                            Bytes.toString(kv.getQualifier()) + ";\nvalue: " +
                            Bytes.toString(kv.getValue()));
                }
                System.out.println("-------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
        }
    }

    public static void main(String[] args) {
            check();
    }
}
