package entry;

import com.twitter.chill.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.SaslOutputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.jruby.javasupport.Java;
import scala.Byte;
import scala.Tuple2;
import util.HbasePoolUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2016/5/30.
 */
public class SparkWorkFlow {
    private static Configuration conf = null;

    public static void check() {
        //spark配置
        SparkConf sc = new SparkConf().setAppName("HBaseRead").setMaster("local[2]");
        JavaSparkContext jsc = new JavaSparkContext(sc);

        Configuration conf = HbasePoolUtils.getConfiguration();
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("crawlerData"));
        scan.addColumn(Bytes.toBytes("crawlerData"),Bytes.toBytes("url"));

        conf.set(TableInputFormat.INPUT_TABLE,"gycrawler");
        ClientProtos.Scan proto = null;
        try {
            proto = ProtobufUtil.toScan(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String scanToString = Base64.encodeBytes(proto.toByteArray());
        conf.set(TableInputFormat.SCAN, scanToString);

        //获得hbase查询结果Result
        JavaPairRDD<ImmutableBytesWritable, Result> myRDD =
                jsc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);

        System.out.println("数目:" + myRDD.count());

        //从Result中取出text数据
        JavaPairRDD<String, String> text   = myRDD.mapToPair(
                new PairFunction<Tuple2<ImmutableBytesWritable,Result>,String,String>(){
                    @Override
                    public Tuple2<String, String> call(Tuple2<ImmutableBytesWritable, Result> immutableBytesWritableResultTuple2) throws Exception {
                        byte [] text = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("text"));
                        byte [] url  = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("url"));
                        if (text != null)   {
                            return new Tuple2<String,String>(Bytes.toString(url), Bytes.toString(text));
                        }else   {
                            return null;
                        }
                    }
                }
        );


//        ResultScanner rs = null;
//        try {
//            HTableInterface table = HbasePoolUtils.getHTable("gycrawler");
//            rs = table.getScanner(scan);
//            int i = 0;
//            for (Result r : rs) {
//                i++;
//                for (Cell cell : r.rawCells()) {
//                    String s = Bytes.toString(CellUtil.cloneRow(cell));
//                    String s1 = Bytes.toString(CellUtil.cloneQualifier(cell));
//                    String s2 = Bytes.toString(CellUtil.cloneValue(cell));
//                    System.out.println("value: " + s2);
//                }
//                System.out.println("-------------------------------------------");
//                for (KeyValue kv : r.list()) {
//                    System.out.println("timestamp:" + kv.getTimestamp());
//                    System.out.println("value: " +
//                            Bytes.toString(kv.getValue()));
//                }
//                System.out.println("-----------------imu--------------------------");
//                System.out.println(i);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            rs.close();
//        }


    }

    public static void main(String[] args) {
            check();
    }
}
