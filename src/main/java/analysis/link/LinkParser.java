package analysis.link;

import analysis.util.Document;
import analysis.util.DocumentNode;
import analysis.util.DocumentParser;
import analysis.util.TokenParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/12.
 */
public class LinkParser
{

    private Document document;
    private List<List<Url>> contentList = new ArrayList<List<Url>>();
    private List<Url> urls;
    private String domain;

    public LinkParser(Document document, String domain)
    {
        this.document = document;
        this.domain = domain;
        parser();
    }

    private void parser()
    {
        map = new HashMap<Integer, Integer>();
        countContent(0);
        countContentList(0);
    }

    public LinkParser(String html, String domain) throws Exception
    {
        try
        {
            List<String> tokens = TokenParser.getTokenList(html);
            this.domain = domain;
            DocumentParser documentParser = new DocumentParser(tokens);
            document = documentParser.getDocument();
            parser();
        } catch (IOException e)
        {
            System.out.println("��ҳ��ʽ���淶��token�б���ʧ��");
            e.printStackTrace();
        }
    }


    /**
     * ��ȡ�ýڵ������е����ݿ�
     *
     * @param index
     */
    private void getUrls(int index)
    {
        DocumentNode node = document.getNode(index);
        List<Integer> childs = node.getChilds();

        if (node.getSort().equals("LINK_LABEL"))

        {
            Url url = new Url();
            String value = node.getValue();

            //��ȡUrl
            url.setUrl(getLabelUrl(value));

            //��ȡtitle
            String title = getLabelTitle(value);
            if (title == null) title = getTitle(index);
            url.setTitle(title);

            //��ȡdate
            long date = getDate(index);
            url.setDate(date);

            System.out.println("url:" + url.getUrl() + "\ttitle:" + title + "\tdate:" + date);

            if (url.getTitle() != null && url.getUrl() != null) urls.add(url);

        }

        if (childs.size() > 0)
        {
            for (int i = 0; i < childs.size(); i++)
            {
                getUrls(childs.get(i));
            }
        }
    }


    /**
     * ��ȡ<a></a>��ǩ���title����
     * @param index
     * @return
     */
    private String getTitle(int index)
    {
        DocumentNode node = document.getNode(index + 1);
        if (node.getSort() == "CONTENT") return node.getValue();
        return null;
    }


    /**
     * ��ȡ<a></a>��ǩ��Χ��ʱ��
     * @param index
     * @return
     */
    private long getDate(int index)
    {
        DocumentNode node = document.getNode(index);
        DocumentNode parentNode = document.getNode(node.getParent());
        int parent = getSingleChildParent(parentNode.getParent());
        return getDateInNode(parent);
    }


    /**
     * ��ȡindex�ڵ��°�����ʱ��
     * @param index
     * @return
     */
    private long getDateInNode(int index)
    {
        DocumentNode node = document.getNode(index);

        if (node.getSort().equals("CONTENT"))
        {
            String value = node.getValue();
            String date = getDateString(value);
            if (date != null) return getDateFormString(date);
        }

        List<Integer> childs = node.getChilds();
        if (childs.size() == 0) return 0;
        else
        {
            for (int i = 0; i < childs.size(); i++)
            {
                long ans = getDateInNode(childs.get(i));
                if (ans != 0) return ans;
            }
            return 0;
        }
    }

    private String getDateString(String token)
    {
        String date = null;
        Pattern compile = Pattern.compile(".*" + DATE_MATCHER + ".*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(token);
        if (matcher.find()) date = matcher.group(1);
        return date;
    }

    /**
     * Ѱ��index�ĸ��ڵ��У���һ�� �ӽڵ�������1�Ľڵ㡣�൱��Ѱ�Ҹýڵ�������С�Ŀ�Ŀ�ڵ�
     * @param index
     * @return
     */
    private int getSingleChildParent(int index)
    {
        if (index == 0) return index;
        DocumentNode node = document.getNode(index);
        int parent = node.getParent();

        if (node.getChilds().size() <= 1) return getSingleChildParent(parent);
        else return index;
    }


    /**
     * ��ȡ�������ݿ����ݣ������µ�contentList
     *
     * @param index
     */
    private void countContentList(int index)
    {
        DocumentNode node = document.getNode(index);
        List<Integer> childs = node.getChilds();
        for (int i = 0; i < childs.size(); i++)
        {
            DocumentNode newNode = document.getNode(childs.get(i));
            if (newNode.isContent())
            {
                urls = new ArrayList<Url>();
                getUrls(childs.get(i));

                if (urls.size() > 0) contentList.add(urls);
                System.out.println("\n\n---------------------------------------------------------------------------------------------------------------------\n\n");
            } else countContentList(childs.get(i));
        }

    }


    /**
     * ͨ�����ڵ���Ϊ��ʼ�� ������������ݿ�
     * Ϊ���ݿ�������
     * 1.��ǰ�ڵ�ΪҶ�ӽڵ��ҵ�ǰ�ڵ�Ϊ���ݽڵ�(isText)
     * 2.��ǰ�ڵ�Ϊ֦�ڵ��ҵ�ǰ�ڵ�������ӽڵ㶼Ϊ���ݿ�(isContent)
     *
     * @param index �ڵ�����document.tree�е�λ��
     */

    private Map<Integer, Integer> map;//���ڼ�֦����Ǹýڵ��Ƿ��ѽ��������

    private void countContent(int index)
    {
        DocumentNode node = document.getNode(index);
        List<Integer> childs = node.getChilds();
        int contentNumber = 0;

        if (childs.size() > 0)
        {
            for (int i = 0; i < childs.size(); i++)
            {
                int child = childs.get(i);
                if (map.get(child) == null) countContent(child);
                DocumentNode childNode = document.getNode(child);
                if (childNode.isContent()) contentNumber++;
            }
            if (contentNumber * 1.0 / childs.size() > 0.9) node.setIsContent(true);
            else
            {
                node.setIsContent(false);
            }
        } else
        {
            if (node.getSort().equals("CONTENT") || node.getSort().equals("LINK_LABEL")) node.setIsContent(true);
            else node.setIsContent(false);
        }

        map.put(index, 1);//Ⱦɫ�ýڵ㣬��֦
    }


    /**
     * ƴ��url
     *
     * @param mainUrl
     * @param newUrk
     * @return
     */
    public static String joinUrl(String mainUrl, String newUrk)
    {
        URL url;
        String q = "";
        try
        {
            url = new URL(new URL(mainUrl), newUrk);
            q = url.toExternalForm();
        } catch (MalformedURLException e)
        {
        }
        if (q.indexOf("#") != -1) q = q.replaceAll("^(.+?)#.*?$", "$1");
        return q;
    }


    /**
     * �ӱ�ǩ����ȡurl
     *
     * @param label
     * @return
     */
    private String getLabelUrl(String label)
    {
        String MACHER = ".*href=('|\")(\\S*)('|\").*";
        String url = null;
        label = label.replaceAll("&\\S*;|\\r|\\t|\\n|\\?|��|\\|", "");
        //ƥ��url
        Pattern compile = Pattern.compile(MACHER, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(label);
        if (matcher.find())
        {
            //ƴ��url
            url = joinUrl(domain, matcher.group(2));
        }
        return url;
    }


    /**
     * �ӱ�ǩ����ȡtitle
     *
     * @param label ��ǩ����
     * @return title����
     */
    public static String getLabelTitle(String label)
    {
        String MACHER = ".*title=('|\")([^'^\"]*)('|\").*";
        String title = null;
        label = label.replaceAll("&\\S*;|\\r|\\t|\\n|\\?|��|\\|", "");
        Pattern compile = Pattern.compile(MACHER, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(label);
        if (matcher.find())
        {
            title = matcher.group(2);
            if (title.length() == 0) title = null;
        }
        return title;
    }


    /**
     * ��ȡ�ַ����е�ʱ�䲢����ת��Ϊʱ���
     *
     * @param str
     * @return
     */
    static final String DATE_MATCHER = "(\\d{4}(\\-|��|\\\\|/|\\.)\\d{1,2}(\\-|��|\\\\|/|\\.)\\d{1,2}(��)?)";

    public static long getDateFormString(String str)
    {
        System.out.println(str);
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

    public List<List<Url>> getContentList()
    {
        return contentList;
    }
}
