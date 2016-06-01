package empty;

import analysis.content.ContentParser;
import analysis.content.ContentValue;
import analysis.judge.HtmlSort;
import analysis.link.LinkParser;
import analysis.link.LinkValue;
import analysis.link.Url;
import analysis.util.Document;
import analysis.util.DocumentNode;
import analysis.util.DocumentParser;
import analysis.util.TokenParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/22.
 */
public class EmptyCheck
{
    private String html;
    private String domain;
    private Document document;
    private List<DocumentNode> tree;
    private List<Url> urls = new ArrayList<Url>();


    public EmptyCheck(String html, String url)
    {
        this.html = html;
        this.domain = url;
        init();
    }


    private void init()
    {
        List<String> tokens;
        try
        {
            tokens = TokenParser.getTokenList(html);
            DocumentParser documentParser = new DocumentParser(tokens);
            document = documentParser.getDocument();
            tree = document.getTree();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public Boolean isEmpty()
    {
        if (recommendIsEmpty()) return true;
        if (contentIsEmpty()) return true;

        return false;
    }


    /**
     * 通过内容是否为空，判断该页是否为疑似空白栏目
     * @return
     */
    private Boolean contentIsEmpty()
    {
        int sort= HtmlSort.getHtmlSort(domain,html);
        if (sort==1)
        {
            //导航
            LinkParser linkParser =new LinkParser(document,domain);
            LinkValue linkValue=new LinkValue(linkParser.getContentList());
            List<Url> urls=linkValue.getContent();
            //如果获取的url数小于以下阀值则表示，该导航页为疑似空白栏目
            if (urls.size()<6) return true;
        }
        else
        {
            //文章页
            ContentParser contentParser =new ContentParser(document);
            ContentValue contentValue =new ContentValue(contentParser.getContentList());
            String content=contentValue.getContent();
            //如果获取的content字数小于以下阀值则表示，该文章页为疑似空白栏目
            if (content.length()<20) return true;
        }


        return false;
    }


    /**
     * 判断关键字所在块中，是否有相应的内容块url存在，如不存在则为疑似空白
     * @return
     */
    private Boolean recommendIsEmpty()
    {
        for (int i = 0; i < tree.size(); i++)
        {
            DocumentNode node = document.getNode(i);
            String soft = node.getSort();

            if (soft.equals("CONTENT"))
            {
                String value = node.getValue();
                //添加关键字匹配。判断该关键字下是否应该有url块
                if (value.contains("相关推荐") || value.contains("热点文章") || value.contains("推荐文章") ||
                        value.contains("相关文章") || value.contains("新闻导读") || value.contains("相关新闻")||value.contains("最新推荐"))
                {
                    urls.clear();
                    int parent = getSingleChildParent(node.getParent());
                    getUrls(parent);
                    if (!LinkValue.isContent(urls)) return true;
                }
            }
        }
        return false;
    }




    private int getSingleChildParent(int index)
    {
        if (index == 0) return index;
        DocumentNode node = document.getNode(index);
        int parent = node.getParent();

        if (node.getChilds().size() <= 1) return getSingleChildParent(parent);
        else return index;
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


    private String getTitle(int index)
    {
        DocumentNode node = document.getNode(index + 1);
        if (node.getSort() == "CONTENT") return node.getValue();
        return null;
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


}
