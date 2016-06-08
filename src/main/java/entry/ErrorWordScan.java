//package entry;
//
//import com.yeezhao.guizhou.client.SpellCheckerClient;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.client.HTable;
//import org.apache.hadoop.hbase.client.Result;
//import org.apache.hadoop.hbase.client.ResultScanner;
//import org.apache.hadoop.hbase.client.Scan;
//import org.apache.hadoop.hbase.util.Bytes;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//import util.PropertyHelper;
//
//import java.io.IOException;
//import java.util.HashMap;
//
///**
// * Created by Administrator on 2016/6/7.
// */
//public class ErrorWordScan {
//    private PropertyHelper propertyHelper;
//    private JedisPool jedisPool;
//    private Configuration hbConfig;
//    private SpellCheckerClient client;
//    private HTable table;
//
//    public void init() {
//        try {
//            propertyHelper = new PropertyHelper("redisconf");
//
//            JedisPoolConfig config = new JedisPoolConfig();
//            config.setMaxTotal(Integer.valueOf(propertyHelper.getValue("MAXTOTAL")));
//            config.setMaxIdle(Integer.valueOf(propertyHelper.getValue("IDLE")));
//            config.setMaxWaitMillis(Integer.valueOf(propertyHelper.getValue("MAXWAIT")));
//            config.setTestOnBorrow(true);
//            jedisPool = new JedisPool(config, propertyHelper.getValue("IP"), Integer.valueOf(propertyHelper.getValue("PORT")));
//            hbConfig = new Configuration();
//            hbConfig.addResource("hbase-site.xml");
//            //  hbConfig = HBaseConfiguration.create(hbConfig);
////            client = new SpellCheckerClient("108.108.108.15",8020,1);
//
//            table = new HTable(hbConfig, "gycrawler");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void startReadHBase() {
//
//        Jedis jedis = null;
//        ResultScanner rs = null;
//        try {
//
//            Scan scan = new Scan();
////            FilterList filterList = new FilterList();
////            Filter filter = new SingleColumnValueFilter(Bytes.toBytes("crawl"), Bytes.toBytes("source"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes("GuizhouWholeSite"));
////            Filter taskFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("a9bf99e7314c8b052812112f111eb798"));
////            filterList.addFilter(filter);
////            filterList.addFilter(taskFilter);
////            scan.setFilter(filterList);
//
//            rs = table.getScanner(scan);
//            jedis = jedisPool.getResource();
//            jedis.select(10);
//            for (Result r : rs) {
//
//
//                HashMap<String, String> data = new HashMap<String, String>();
//
////                String rowkey = Bytes.toString(r.getRow());
////                String rootUrl = Bytes.toString(r.getValue(Bytes.toBytes("crawl"), Bytes.toBytes("rootUrl")));
////                String fromUrl = Bytes.toString(r.getValue(Bytes.toBytes("crawl"), Bytes.toBytes("fromUrl")));
//                String url = Bytes.toString(r.getValue(Bytes.toBytes("crawlerData"), Bytes.toBytes("url")));
//                /*String round = Bytes.toString(r.getValue(Bytes.toBytes("crawl"), Bytes.toBytes("round")));
//
//                String content = Bytes.toString(r.getValue(Bytes.toBytes("crawl"), Bytes.toBytes("content")));*/
//
//                System.out.println("***************url************** : ");
//
//                 /*JSONObject jsonObject = new JSONObject(content);
//                String crawl_time = jsonObject.getString("crawl_time");
//                data.put("rootUrl", rootUrl);
//                data.put("fromUrl", fromUrl.replaceAll("\\|", ""));
//                data.put("round", round);
//                data.put("crawlTime", crawl_time);
//
//                if (jsonObject.has("text")) {
//                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("text"));
//                    System.out.println(rowkey);
//                    if (jsonObject1.has("text")) {
//
//                        String content2 = "";
//                        if (jsonObject1.optJSONArray("text") != null)
//                            content2 = jsonObject1.getJSONArray("text").toString();
//                        else if (jsonObject1.optJSONObject("text") != null)
//                            content2 = jsonObject1.getJSONObject("text").toString();
//                        else
//                            content2 = jsonObject1.opt("text").toString();
//                        data.put("content", content2);
//                        String errorwords = client.query(content2);
//                        if (errorwords != null && !errorwords.equals("")) {
//                            data.put("errorwords", errorwords);
//                            data.put("url",url);
//                            data.put("tid",pattern.split(rowkey)[0]);
//                          //  jedis.hmset("commons:crawler:errorword:" + pattern.split(rowkey)[0] + ":" + url,  data);
//                            jedis.set("commons:crawler:errorword:" + pattern.split(rowkey)[0] + ":" + url, JSONUtil.object2JacksonString(data));
//
//                        }
//                    }
//                }*/
//
//            }
//
//
//        } catch (IOException e) {
//
//            if (jedis != null) jedisPool.returnBrokenResource(jedis);
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        ErrorWordScan errorWordScan = new ErrorWordScan();
//        errorWordScan.init();
//        errorWordScan.startReadHBase();
//    }
//}
