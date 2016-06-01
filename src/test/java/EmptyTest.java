import download.GetHtml;
import empty.EmptyCheck;

/**
 * Created by Administrator on 2016/4/13.
 */
public class EmptyTest
{

    public static void main(String[] args) throws Exception
    {
        String url="http://www.trs.gov.cn/news/2016422/n51342.html";
        String html= GetHtml.getHtml(url);

        EmptyCheck emptyCheck=new EmptyCheck(html,url);

        System.out.println(emptyCheck.isEmpty());



    }
}
