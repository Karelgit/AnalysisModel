package entry;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import util.HbasePoolUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/30.
 */
public class SparkWorkFlow {
    private static Configuration conf = null;
    //spark配置
    public static SparkConf sc = new SparkConf().setAppName("wordCountForSites").setMaster("local[2]");
    public static JavaSparkContext jsc = new JavaSparkContext(sc);

    public static void check(String domain) {
        Configuration conf = HbasePoolUtils.getConfiguration();
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("crawlerData"));
        scan.addColumn(Bytes.toBytes("crawlerData"),Bytes.toBytes("url"));
        scan.addColumn(Bytes.toBytes("crawlerData"),Bytes.toBytes("title"));
        scan.addColumn(Bytes.toBytes("crawlerData"),Bytes.toBytes("text"));
        scan.setMaxVersions(1);

        conf.set(TableInputFormat.INPUT_TABLE,domain);
        ClientProtos.Scan proto = null;
        try {
            proto = ProtobufUtil.toScan(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String scanToString = Base64.encodeBytes(proto.toByteArray());
        conf.set(TableInputFormat.SCAN, scanToString);

        //获得hbase查询结果Result
        JavaPairRDD<ImmutableBytesWritable, Result> myRDD = jsc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);

        JavaRDD<String> words = myRDD.flatMap(
                new FlatMapFunction<Tuple2<ImmutableBytesWritable, Result>, String>() {
                    @Override
                    public Iterable<String> call(Tuple2<ImmutableBytesWritable, Result> immutableBytesWritableResultTuple2) throws Exception {
                        byte [] url = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("url"));
                        byte [] title = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("title"));
                        byte [] text = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("text"));
                        ;
                        String urlString = Bytes.toString(url);
                        String titleString =Bytes.toString(title);
                        String content = Bytes.toString(text);

                        List<Term> parse = ToAnalysis.parse(content);
                        List<String> result = new ArrayList<String>();
                        System.out.println("url: " +urlString +"\n"+"title: " + titleString + "\t" + "text: " + content);
                        /*for (int i=0; i<parse.size(); i++) {
                            result.add(parse.get(i).toString());
                            System.out.println(parse.get(i).toString().substring(0,5));
                        }*/
                        return result;
                    }
                }
        );

        JavaPairRDD<String,Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        });

        JavaPairRDD<String,Integer> counts =pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer a, Integer b) throws Exception {
                return a+b;
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        Date now = new Date(System.currentTimeMillis());
        counts.saveAsTextFile("file:///C:/data_700sites/" + domain + sdf.format(now));
//        counts.saveAsTextFile("file:///usr/local/SparkApp/data" + sdf.format(now));

        /*//从Result中取出text数据
        JavaPairRDD<String, String> data   = myRDD.mapToPair(
                new PairFunction<Tuple2<ImmutableBytesWritable,Result>,String, String>(){
                    public Tuple2<String, String> call(Tuple2<ImmutableBytesWritable, Result> immutableBytesWritableResultTuple2) throws Exception {
                        byte [] text = immutableBytesWritableResultTuple2._2().getValue(Bytes.toBytes("crawlerData"),Bytes.toBytes("text"));
                        System.out.println(Bytes.toString(text));
                        return new Tuple2<String,String>(Bytes.toString(url),Bytes.toString(text));
                    }
                }
        ).cache();

        data.

        data.saveAsTextFile("/data_700sites/+System.currentTimeMillis()");
*/
        /*//过滤出空白栏目
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
        emptyUrl.saveAsTextFile("/AnalysisModel/data_"+System.currentTimeMillis()+".txt");*/

    }

    public static void main(String[] args) {
        //分开统计
        List<String> urlList = new ArrayList<>();
        try {
            urlList = util.FileReader.readFile(System.getProperty("user.dir") + "/data/urls_sites.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i=0; i<urlList.size(); i++) {
            check(urlList.get(i));
        }
        //集中统计
        /*check();*/
    }
}
