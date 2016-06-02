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
    private List<String> contentList;//���ݿ��б�

    static final String DATE_MATCHER = "(\\d{4}(\\-|��|\\\\|/|\\.)\\d{1,2}(\\-|��|\\\\|/|\\.)\\d{1,2}(��)?)";


    private int contentIndex = -1;

    public ContentValue(List<String> contentList)
    {
        this.contentList = contentList;
    }


    /**
     * ��ȡTitle
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

            if (str.contains("<<") || str.contains("��")) key += 5;
            if (key > max)
            {
                max = key;
                title = str;
            }
        }
        return title;
    }


    /**
     * ��ȡ��ҳ����
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
     * ���ݱ������Ϊ���ݿ�ķ�ֵ
     * @param content
     * @return
     */
    private int countContentLevel(String content)
    {
        int level = 0;
        level += content.split(",").length-1;
        level += content.split("��").length-1;
        level += content.split("��").length-1;
        level += content.split("��").length-1;
        level += content.split("!").length-1;
        return level;
    }


    /**
     * ��String����ȡ������ʱ���ʽ��  ʱ���ַ���
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
     * ��ȡ����ҳ����ʱ��
     * @return
     */
    public long getDate()
    {
        String date;
        String str;

        //���ݹؼ�����ȡ����
        for (int i = 0; i < contentList.size(); i++)
        {
            str = contentList.get(i);
            if (str.contains("����ʱ��") || str.contains("��������") || str.contains("����") || str.contains("ʱ��"))
            {
                date = getDateString(str);
                if (date != null) return getDateFormString(date);
            }
        }

        //��������ǰ��ȡ����
        for (int i = contentIndex-1; i>contentIndex-6; i--)
        {
            if (i<0) break;
            str = contentList.get(i);
            date = getDateString(str);
            if (date != null) return getDateFormString(date);
        }

        //��������ȡ����
        if (contentIndex>-1)
        {
            str = contentList.get(contentIndex);
            date = getDateString(str);
            if (date != null) return getDateFormString(date);
        }

        return 0;

    }


    /**
     * ��ȡ�ַ����е�ʱ�䲢����ת��Ϊʱ���
     *
     * @param str
     * @return
     */
    public static long getDateFormString(String str)
    {

        //��ʽ��׼��
        str = str.replaceAll("\\\\|/|��|��|\\.", "-");
        str = str.replaceAll("��", "");

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
