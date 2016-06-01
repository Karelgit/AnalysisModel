import analysis.link.LinkParser;
import analysis.link.LinkValue;
import analysis.link.Url;
import download.GetHtml;

import java.util.List;

/**
 * Created by Administrator on 2016/4/18.
 */
public class LinkTest
{
    public static void main(String[] args) throws Exception
    {
//        String url="http://xxgk.gygov.gov.cn/xxgk/jcms_files/jcms1/web1/site/col/col218/index.html";
//        String url="http://www.gzfenggang.gov.cn/00952800-4/B/01/index.shtml";


        String url=" http://www.trs.gov.cn/?Temp=zmhdzxft&state=4";
        String html= GetHtml.getHtmlFromUrl(url);

        LinkParser linkParser =new LinkParser(html,url);

        LinkValue linkValue=new LinkValue(linkParser.getContentList());
        List<Url> urls=linkValue.getContent();

        System.out.println("\n\n\n*******************************************************************************\n\n");
        for (int i = 0; i <urls.size() ; i++)
        {
            Url url1=urls.get(i);
            System.out.println("url:" + url1.getUrl() + "\ttitle:" + url1.getTitle() + "\tdate:" + url1.getDate());
        }

    }
}
