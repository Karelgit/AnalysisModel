package entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import util.HbasePoolUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * Created by Administrator on 2016/5/30.
 */
public class SparkWorkFlow {
    private static Configuration conf = null;

    public static void check() {
        //SparkConf设置
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local");
        sparkConf.setAppName("Spark HBase");
        JavaSparkContext context = new JavaSparkContext(sparkConf);




//        JavaSparkContext jsc = new JavaSparkContext();

//        JavaRDD<String> text =

        //Scan操作
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes("0120140722"));
        scan.setStopRow(Bytes.toBytes("1620140728"));
        scan.addFamily(Bytes.toBytes("basic"));
        scan.addColumn(Bytes.toBytes("basic"), Bytes.toBytes("name"));

        conf = HbasePoolUtils.getConfiguration();
        conf.set(TableInputFormat.INPUT_TABLE, "gycrawler");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        /*scan.write(dos);
        String scanStr = Base64.encodeBytes(out.toByteArray());
        IOUtils.closeQuietly(dos);
        IOUtils.closeQuietly(out);
        //高版本可以用如下方式：
        //ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        //String scanStr = Base64.encodeBytes(proto.toByteArray());
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
        }*/
    }

    public static void main(String[] args) {
        check();
    }
}
