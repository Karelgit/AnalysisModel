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
                System.out.println(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
        }

        /*JavaSparkContext sc = new JavaSparkContext("127.0.0.1", "hbaseTest",
                System.getenv("SPARK_HOME"), System.getenv("JARS"));

        Configuration conf = HbasePoolUtils.getConfiguration();
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("crawlerData"));
        scan.addColumn(Bytes.toBytes("crawlerData"), Bytes.toBytes("text"));
        try {
            String tableName = "gycrawler";
            conf.set(TableInputFormat.INPUT_TABLE, tableName);
            ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
            String ScanToString = Base64.encodeBytes(proto.toByteArray());
            conf.set(TableInputFormat.SCAN, ScanToString);

            JavaPairRDD<ImmutableBytesWritable, Result> myRDD =  sc.newAPIHadoopRDD(conf,TableInputFormat.class,ImmutableBytesWritable.class,Result.class);
            System.out.println(myRDD.count());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void main(String[] args) {
            check();
    }
}
