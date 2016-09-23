package util;

/**
 * <类详细说明>
 *
 * @Author： huanghai
 * @Version: 2016-08-26
 **/
public class StringUtil {
    public static  String simplyStr(String str) {
        if(str.contains("编辑"))  {
            int index_tail = str.indexOf("编辑",0);
            String result = str.substring(0,index_tail);
            int index_head = 0;
            int index_source = 0;
            int index_share = 0;
            int index_author = 0;
            if(result.contains("来源"))   {
                index_source = str.indexOf("来源",0);
            }else if(result.contains("分享到"))  {
                index_share = str.indexOf("分享到",0);
            }else if(result.contains("作者")) {
                index_author = str.indexOf("作者",0);
            }
            if(index_source !=0 || index_share !=0 || index_author !=0) {
                index_head = max(index_source,index_share,index_author);
                String res = str.substring(index_head,index_tail);
                return res;
            }else{
                return result;
            }
        }else   {
            return str;
        }
    }

    public static String removeSymbol(String str) {
        char [] wordChar= str.toCharArray();
        StringBuffer buffer=new StringBuffer();
        for (char c : wordChar) {
            if(StringUtil.checkChinese(c)){
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    public static boolean  checkChinese(char word){
        if ((word >= 0x4e00)&&(word<=0x9fbb)) return true;
        else return false;
    }

    public static int max(int a,int b,int c)  {
        int temp =  (a>b)?a:b;
        return (temp>c)?temp:c;
    }

    public static void main(String[] args) {
        String pendingStr = removeSymbol("香港4.25绑架案:8人被捕警方已掌握赃款大概位置-香港,万元,犯罪,深圳,港币,-多彩贵州网手机报手机报香港4.25绑架案:8人被捕警方已掌握赃款大概位置2015-06-1004:29来源：分享到：记者9日从深圳市人民检察院获悉，该院已对香港“4·25”特大绑架案中的八名嫌犯批准逮捕。目前，警方已从嫌犯处缴获赎金600余万元港币及珠宝首饰一批，并已掌握其余赃款的大概位置。4月25日凌晨，犹某魁等七人分工配合，攀爬进罗女士所住的别墅，在洗劫了保险柜内价值数百万元的珠宝首饰后，将罗女士绑架，并索取2800万元港币赎金。记者从深圳市人民检察院了解到，该绑架案的嫌犯共计9人，其中7人实施了绑架，1人协助绑匪偷渡，1人协助绑匪销赃。除犯罪嫌疑人郑某旺在持签证过关时被香港警方抓获后在香港接受法律制裁外，其余8人均在内地被深圳警方抓获，因此在内地进入司法程序。深圳检方以涉嫌绑架罪、抢劫罪，对犯罪嫌疑人犹某魁、王某锟、王某波、熊某辉、张某江、毛某兵六人批准逮捕；以涉嫌掩饰、隐瞒犯罪所得罪，对犯罪嫌疑人梁某顺批准逮捕；以涉嫌组织他人偷渡国边境罪，对犯罪嫌疑人蒋某华批准逮捕。深圳检方表示，参与绑架、抢劫的七名犯罪嫌疑人均为贵州省瓮安县人，多人有犯罪前科。七人中除犹某魁1976年出生外，其余六人均为80后甚至90后。其中犹某魁此前曾多次偷渡到香港进行入室盗窃，甚至还在香港获过刑，因而熟悉香港飞鹅山的情况等。据目前查明的情况，2015年3月底，犯罪嫌疑人犹某魁因赌博欠有巨债，召集无业的老乡，在深圳密谋偷渡香港作案。七人中除郑某旺持签证过关至香港，其余六人分批从深圳沙头角偷渡到香港。七人在飞鹅山上居住了多日，多次对飞鹅山附近豪宅进行了踩点，并根据车辆、监控、保安等情况，最终选定了受害人罗女士所居住的别墅，并于4月25日下手。在受害人家属筹钱期间，犹某魁即联系梁某顺销赃事宜。4月28日下午，犹某魁、王某锟二人在飞鹅山一亭子旁的厕所后面，取到了2800万元港币的赎金，并通知熊某辉等人将受害人罗女士释放。记者从深圳检方了解到，犹某魁、王某锟二人拿到2800万元港币赎金后，先将1800万元港币藏匿起来，将余下的1000万元港币带回据点分赃，并谎称共拿到1000万元赎金，于是七人按各自的地位与作用对赃款进行了分配，还保留了一点“公共经费”。数天后，另几名犯罪嫌疑人从新闻中获知共收到的赎金有2800万元港币，对此甚是愤怒。深圳检方表示，继从犯罪嫌疑人处缴获赎金280余万元港币及部分赃物后，5月16日警方又查获赃款365万元港币及珠宝首饰一批。据悉，目前警方已掌握其余赃款的大概位置。作者：编辑：陈嘉新更多返回频道首页进入论坛每日推荐・贵州食品流通环节合格率达98%“菜篮子”总体安全・贵州将率先在全国开展非公开信息目录管理试点工作・网传“甲醛食品”作祟专家：少量甲醛属正常且安全・端午小长假期间贵州刷卡交易达18亿元・贵阳首批100辆甲醇出租车投入运营・贵州省2015年普通高校专升本网上填报志愿时间为6月27日、28日・“大腕”云集“生态文明建设与气候安全”高峰论坛・绿色金融与绿色经济发展高峰会将于6月26日亮相视频・直播・【视频】贵阳萃智杯・第三届创新创业大赛招募令・【视频】贵阳萃智杯・2014第三届创新创业大赛・【视频直播】文朝荣同志先进事迹报告会・直播：贵州建省600年\"我与贵州的故事\"征文颁奖・直播:生态文明贵阳国际论坛2014年会媒体见面会");
        simplyStr(pendingStr);
    }
}
