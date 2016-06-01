package download;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Administrator on 2015/10/26.
 */
public class GetHtml
{


    /**
     * 获取url的html源码
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getHtmlFromUrl(String url) throws IOException
    {
        String str;
        //关闭异常抛出
        WebRequest req = new WebRequest(new URL(url));
        //req.setAdditionalHeader("Cookie", "");

        WebClient webClient = new WebClient();
        if (webClient == null)
        {

            webClient = new WebClient();
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setAppletEnabled(false);
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(15 * 1000);
            // AJAX support
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            // Use extension version htmlunit cache process
            // Enhanced WebConnection based on urlfilter
            //webClient.setWebConnection(new RegexHttpWebConnection(webClient));
            webClient.waitForBackgroundJavaScript(5L * 1000);
            webClient.setJavaScriptTimeout(0);
            //设置足够高度以支持一些需要页面内容多需屏幕滚动显示的页面
        }

        //获取page
        Page page;
        String html = "";

        try
        {
            page = webClient.getPage(req);
            //判断获取到的page是否符合html规则
            if (page.isHtmlPage() && StringUtils.equals(page.getWebResponse().getContentType(), "text/html"))
            {
                html = ((HtmlPage) page).asXml().replaceFirst("<\\?xml version=\"1.0\" encoding=\"(.+)\"\\?>", "<!DOCTYPE html>");
            }
        } catch (InterruptedIOException e)
        {
        } catch (RuntimeException e)
        {
        } catch (IOException e)
        {
        }

        return html;
    }


    public static String getHtml(String url) throws IOException, URISyntaxException
    {

        DefaultHttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());

        // 设置为get取连接的方式.
        HttpGet httpget = new HttpGet(url);

        httpget.setHeader("Cache-Control", "max-age=0");
        httpget.setHeader("Upgrade-Insecure-Requests", "1");


        // 得到返回的response.
        HttpResponse response = client.execute(httpget);
        // 得到返回的client里面的实体对象信息.
        HttpEntity entity = response.getEntity();

        String html="";
        if (entity != null)
        {
            //  System.out.println("内容编码是：" + entity.getContentEncoding());
            // System.out.println("内容类型是：" + entity.getContentType());
            html = EntityUtils.toString(entity,"utf-8");
            System.out.println("内容类型是：" + html);
            String e = response.getHeaders("Location").toString();
            System.out.println(e);

        }



        return html;

    }


    @Test
    public void test() throws IOException, URISyntaxException
    {
        String url = "http://www.nanming.gov.cn/nmxw/gzdt/zwxx/116384.shtml";
        System.out.println(getHtml(url));
    }
}
