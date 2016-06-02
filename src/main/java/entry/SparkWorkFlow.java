package entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import util.HbasePoolUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/5/30.
 */
public class SparkWorkFlow {
    private static Configuration conf = null;

    public static void check() {
        //SparkConf≈‰÷√
        JavaSparkContext sc = new JavaSparkContext("local", "hbaseTest",
                System.getenv("SPARK_HOME"), System.getenv("JARS"));

        //Scan≤Ÿ◊˜
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("crawlerData"));
        scan.addColumn(Bytes.toBytes("crawlerData"), Bytes.toBytes("text"));

        conf = HbasePoolUtils.getConfiguration();

        try {
            conf.set(TableInputFormat.INPUT_TABLE, "gycrawler");
            ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
            String ScanToString = Base64.encodeBytes(proto.toByteArray());
            conf.set(TableInputFormat.SCAN, ScanToString);

            JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        check();
    }
}
