package entry;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import util.HbasePoolUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/5/30.
 */
public class SparkWorkFlow {
    private static Configuration conf = null;

    public static void check() {
        //SparkConf配置
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local");
        sparkConf.setAppName("Spark HBase");
        JavaSparkContext context = new JavaSparkContext(sparkConf);

        //Scan操作
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("0120140722"));
        scan.setStopRow(Bytes.toBytes("1620140728"));
        scan.addFamily(Bytes.toBytes("crawler"));
        scan.addColumn(Bytes.toBytes("crawler"), Bytes.toBytes("text"));

        conf = HbasePoolUtils.getConfiguration();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
//        scan.write(dos);
        String scanStr = Base64.encodeBytes(out.toByteArray());
        IOUtils.closeQuietly(dos);
        IOUtils.closeQuietly(out);
        //高版本可以使用如下方式
        //ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        //String scanStr = Base64.encodeBytes(proto.toByteArray());
        conf.set(TableInputFormat.INPUT_TABLE, "gycrawler");
        conf.set(TableInputFormat.SCAN, scanStr);

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = context
                .newAPIHadoopRDD(conf, TableInputFormat.class,
                        ImmutableBytesWritable.class, Result.class);

        Long count = hBaseRDD.count();
        System.out.println("count: " + count);

        List<Tuple2<ImmutableBytesWritable, Result>> tuples = hBaseRDD
                .take(count.intValue());
        for (int i = 0, len = count.intValue(); i < len; i++) {
            Result result = tuples.get(i)._2();
            KeyValue[] kvs = result.raw();
            for (KeyValue kv : kvs) {
                System.out.println("rowkey:" + new String(kv.getRow()) + " cf:"
                        + new String(kv.getFamily()) + " column:"
                        + new String(kv.getQualifier()) + " value:"
                        + new String(kv.getValue()));
            }
        }
    }

    public static void main(String[] args) {
        check();
    }
}
