package analysis.link;

import analysis.judge.JudgeURL;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/15.
 */
public class LinkValue
{
    private List<List<Url>> contentList;//内容块列表


    final static double textDensity = 6;


    public LinkValue(List<List<Url>> contentList)
    {
        this.contentList = contentList;
    }

    /**
     * 获取导航页中所有内容链接
     * @return
     */
    public List<Url> getContent()
    {
        List<Url> ans = new ArrayList<Url>();
        List<Url> urls;

        for (int i = 0; i < contentList.size(); i++)
        {
            urls = contentList.get(i);

            if (isContent(urls))
            {
                for (int j = 0; j < urls.size(); j++)
                {
                    Url url = urls.get(j);
                    if (JudgeURL.getSortByURL(url.getUrl()) != 1) ans.add(url);
                    else
                    {
                        if (isNextPage(url.getUrl(), url.getTitle()))
                        {
                            ans.add(url);
                        }
                    }
                }
            } else
            {
                //查询是否包含下一页
                for (int j = 0; j < urls.size(); j++)
                {
                    Url url = urls.get(j);
                    if (isNextPage(url.getUrl(), url.getTitle()))
                    {
                        ans.add(url);
                    }
                }
            }
        }
        return ans;
    }


    /**
     * 判断list中的url是否为需求内容
     *
     * @param
     * @return
     */
    public static boolean isContent(List<Url> urls)
    {

        //初始化
        int article = 0;
        int navigate = 0;

        int sum = urls.size();
        int textNum = 0;
        Url url;

        //统计文章链接个数
        for (int i = 0; i < sum; i++)
        {
            url = urls.get(i);

            //统计文章和导航数
            int sort = JudgeURL.getSortByURL(url.getUrl());
            if (sort == -1) article++;
            if (sort == 1) navigate++;

            textNum += url.getTitle().length();
        }

        //如果导航链接在总链接数里超过以下阀值，则直接判为不是内容块
        if ((navigate + 0.0) / sum > 0.6) return false;

        //如果文章页链接的占比 比导航页链接的占比大，则直接判为是内容块
        if ((article + 0.0) / sum > (navigate + 0.0) / sum) return true;

        //如果块中链接数小于等于一则判为，不是内容块
        if (sum > 1)
        {
            //当每条链接的平均标题长度大于以下阀值，则判为内容块
            if (textNum / sum > textDensity) return true;
        }
        return false;
    }


    /**
     * 判断该链接是否为静态网页中的 “下一页”
     * @param url
     * @param title
     * @return
     */
    static Boolean isNextPage(String url, String title)
    {
        final String NEXTPAGR_URL_MATCHER1 = ".*(next|last|page).*";
        final String NEXTPAGR_URL_MATCHER2 = ".*(\\d+|index)_\\d+.\\w+";
        if (Pattern.matches(NEXTPAGR_URL_MATCHER1, url)) return true;
        if (Pattern.matches(NEXTPAGR_URL_MATCHER2, url)) return true;
        if (title.contains("下页|下一页")) return true;
        return false;
    }



}
