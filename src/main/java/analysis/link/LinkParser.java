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
            System.out.println("网页格式不规范，token列表构造失败");
            e.printStackTrace();
        }
    }


    /**
     * 获取该节点下所有的内容块
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

            //获取Url
            url.setUrl(getLabelUrl(value));

            //获取title
            String title = getLabelTitle(value);
            if (title == null) title = getTitle(index);
            url.setTitle(title);

            //获取date
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
     * 提取<a></a>标签后的title内容
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
     * 提取<a></a>标签周围的时间
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
     * 提取index节点下包含的时间
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
     * 寻找index的父节点中，第一个 子节点数大于1的节点。相当于寻找该节点所在最小的块的块节点
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
     * 获取所有内容块内容，并更新到contentList
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
     * 通过跟节点做为起始点 遍历计算出内容块
     * 为内容块条件：
     * 1.当前节点为叶子节点且当前节点为内容节点(isText)
     * 2.当前节点为枝节点且当前节点的所有子节点都为内容块(isContent)
     *
     * @param index 节点所在document.tree中的位置
     */

    private Map<Integer, Integer> map;//用于剪枝，标记该节点是否已近被计算过

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

        map.put(index, 1);//染色该节点，剪枝
    }


    /**
     * 拼接url
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
     * 从标签中提取url
     *
     * @param label
     * @return
     */
    private String getLabelUrl(String label)
    {
        String MACHER = ".*href=('|\")(\\S*)('|\").*";
        String url = null;
        label = label.replaceAll("&\\S*;|\\r|\\t|\\n|\\?|·|\\|", "");
        //匹配url
        Pattern compile = Pattern.compile(MACHER, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(label);
        if (matcher.find())
        {
            //拼接url
            url = joinUrl(domain, matcher.group(2));
        }
        return url;
    }


    /**
     * 从标签中提取title
     *
     * @param label 标签内容
     * @return title内容
     */
    public static String getLabelTitle(String label)
    {
        String MACHER = ".*title=('|\")([^'^\"]*)('|\").*";
        String title = null;
        label = label.replaceAll("&\\S*;|\\r|\\t|\\n|\\?|·|\\|", "");
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
     * 提取字符串中的时间并将其转化为时间戳
     *
     * @param str
     * @return
     */
    static final String DATE_MATCHER = "(\\d{4}(\\-|年|\\\\|/|\\.)\\d{1,2}(\\-|月|\\\\|/|\\.)\\d{1,2}(日)?)";

    public static long getDateFormString(String str)
    {
        System.out.println(str);
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

    public List<List<Url>> getContentList()
    {
        return contentList;
    }
}
