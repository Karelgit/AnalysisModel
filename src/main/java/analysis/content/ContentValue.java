package analysis.content;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/15.
 */
public class ContentValue
{
    private List<String> contentList;//内容块列表

    static final String DATE_MATCHER = "(\\d{4}(\\-|年|\\\\|/|\\.)\\d{1,2}(\\-|月|\\\\|/|\\.)\\d{1,2}(日)?)";


    private int contentIndex = -1;

    public ContentValue(List<String> contentList)
    {
        this.contentList = contentList;
    }


    /**
     * 提取Title
     * @return
     */
    public String getTitle()
    {
        String title =null;
        int max = 0;
        int key;
        String str;
        for (int i = contentIndex-1; i>contentIndex-6; i--)
        {
            str = contentList.get(i);
            key = str.length();
            for (int j = 0; j < str.length(); j++)
            {
                if (str.charAt(j) >= '0' && str.charAt(j) <= '9') key--;
            }

            if (str.contains("<<") || str.contains("《")) key += 5;
            if (key > max)
            {
                max = key;
                title = str;
            }
        }
        return title;
    }


    /**
     * 提取网页内容
     * @return
     */
    public String getContent()
    {
        int maxLevel = 0;
        int level;
        String content = "";

        for (int i = 0; i < contentList.size(); i++)
        {
            level = countContentLevel(contentList.get(i));
            if ((level > maxLevel)&&(level>0))
            {
                maxLevel = level;
                contentIndex = i;
            }
        }

        if (contentIndex > -1) content = contentList.get(contentIndex);
        return content;
    }

    /**
     * 根据标点计算块为内容块的分值
     * @param content
     * @return
     */
    private int countContentLevel(String content)
    {
        int level = 0;
        level += content.split(",").length-1;
        level += content.split("，").length-1;
        level += content.split("。").length-1;
        level += content.split("！").length-1;
        level += content.split("!").length-1;
        return level;
    }


    /**
     * 从String中提取出满足时间格式的  时间字符串
     * @param token
     * @return
     */
    private String getDateString(String token)
    {
        String date = null;
        Pattern compile = Pattern.compile(".*" + DATE_MATCHER + ".*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(token);
        if (matcher.find()) date = matcher.group(1);
        System.out.println(date);
        return date;
    }


    /**
     * 提取文章页发布时间
     * @return
     */
    public long getDate()
    {
        String date;
        String str;

        //根据关键字提取日期
        for (int i = 0; i < contentList.size(); i++)
        {
            str = contentList.get(i);
            if (str.contains("发布时间") || str.contains("发布日期") || str.contains("日期") || str.contains("时间"))
            {
                date = getDateString(str);
                if (date != null) return getDateFormString(date);
            }
        }

        //从正文往前提取日期
        for (int i = contentIndex-1; i>contentIndex-6; i--)
        {
            if (i<0) break;
            str = contentList.get(i);
            date = getDateString(str);
            if (date != null) return getDateFormString(date);
        }

        //从正文提取日期
        if (contentIndex>-1)
        {
            str = contentList.get(contentIndex);
            date = getDateString(str);
            if (date != null) return getDateFormString(date);
        }

        return 0;

    }


    /**
     * 提取字符串中的时间并将其转化为时间戳
     *
     * @param str
     * @return
     */
    public static long getDateFormString(String str)
    {

        //格式标准化
        str = str.replaceAll("\\\\|/|年|月|\\.", "-");
        str = str.replaceAll("日", "");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try
        {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        long timeStemp = date.getTime();
        return timeStemp;
    }


}
