package analysis.judge;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by JYlsc on 2015/9/16.
 */
public class JudgeURL {
    static int sum = 0;
    static List<String> urlArticleRules = new ArrayList<String>();
    static List<String> urlNavigateRules = new ArrayList<String>();



    /**
     * 根据URL对网页分类
     * @param url
     * @return 1:导航类 -1:文章页
     */
    public static int getSortByURL(String url) {
        bindRules();
        int size;
        int sort=0;
        int i;

        //NAVIGATE
        size = urlNavigateRules.size();
        for (i = 0; i < size; i++) {
            if ( Pattern.matches(urlNavigateRules.get(i), url)) sort=1;
        }


        //ARTICLE
        size = urlArticleRules.size();
        for (i = 0; i < size; i++) {
            if ( Pattern.matches(urlArticleRules.get(i), url)) sort=-1;
        }


        return (sort);
    }

    public static void bindRules() {
        urlArticleRules.add(".*/\\d+.(html|shtml|htm|aspx|xhtml|jhtml)");
        urlArticleRules.add(".*(show|Show|content|Content|article|Article|art|Art).*");
        urlNavigateRules.add(".*(list|index|Index|class).*");
        urlNavigateRules.add("\\D*");
    }


    @Test
    public void test()
    {
        String str="http://www.zunyi.gov.cn/zwgk/zdxxgk/czzj/czys/";
        System.out.println(getSortByURL(str));
    }


}