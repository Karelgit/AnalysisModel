import analysis.content.ContentParser;
import analysis.content.ContentValue;
import download.GetHtml;

/**
 * Created by Administrator on 2016/4/13.
 */
public class ContentTest
{

    public static void main(String[] args) throws Exception
    {
        String url="http://www.huaxia.com/gz-tw/gzkx/2016/04/4806174.html";
        String html= GetHtml.getHtml(url);

        ContentParser contentParser =new ContentParser(html);
        ContentValue contentValue =new ContentValue(contentParser.getContentList());


        //�����ȵ���getContent()���ܵ���getTitle������getDate();
        System.out.println("\n\ncontent:" + contentValue.getContent());
        System.out.println("\n\ncontent:" + contentValue.getTitle());
        System.out.println("\n\ncontent:" + contentValue.getDate());




    }
}
