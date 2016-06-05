package entry;

import empty.EmptyCheck;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.SaslOutputStream;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.jruby.javasupport.Java;
import scala.Byte;
import scala.Tuple2;
import util.HbasePoolUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        scan.addColumn(Bytes.toBytes("crawlerData"),Bytes.toBytes("html"));
        scan.setMaxVersions(1);

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

        //从Result中取出text数据
        JavaPairRDD<String, String> data   = myRDD.mapToPair(
                new PairFunction<Tuple2<ImmutableBytesWritable,Result>,String, String>(){
                    public Tuple2<String, String> call(Tuple2<ImmutableBytesWritable, Result> immutableBytesWritableResultTuple2) throws Exception {
                        byte [] url  = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("url"));
                        byte [] html = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("html"));
                        return new Tuple2<String,String>(Bytes.toString(url),Bytes.toString(html));
                    }
                }
        ).cache();

        //过滤出空白栏目
        JavaPairRDD<String, String> emptyTopic = data.filter(
                new Function<Tuple2<String, String>,Boolean>()  {
                    public Boolean call(Tuple2<String, String> stringStringTuple2) throws Exception {
                        if(new EmptyCheck(stringStringTuple2._2(),stringStringTuple2._1()).isEmpty()) {
                            return true;
                        }else {
                            return false;
                        }
                    }
                }
        );

        JavaRDD<String> emptyUrl = emptyTopic.map(
                new Function<Tuple2<String, String>, String>() {
                    public String call(Tuple2<String, String> stringStringTuple2) throws Exception {
                        return stringStringTuple2._1();
                    }
                }
        );

        //空白栏目存入HDFS
        emptyUrl.saveAsTextFile("/AnalysisModel/data_"+System.currentTimeMillis()+".txt");

    }

    public static void main(String[] args) {
            check();
    }
}
