package analysis.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.w3c.tidy.Tidy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsc on 2016/2/18.
 */
public class TokenParser
{

    /**
     * 获取标签名
     *
     * @param label
     * @return 如:<a herf=""/> 标签名: a
     */
    private static String getLabelName(String label)
    {
        String name = "";
        char word;
        for (int i = 1; i < label.length(); i++)
        {
            word = label.charAt(i);
            if (word == ' ') break;
            else name += word;
        }
        return name;
    }


    /**
     * 清理标签中不需要的内容
     *
     * @param label
     * @return
     */
    private static String cleanLabel(String label)
    {
        String str;
        str = label.replaceAll("&\\S*;|\\r|\\t|\\n|\\?|·|\\|", "");
        if (!str.startsWith("<"))
            str = str.replaceAll(" |　| ", "");
        else
        {
            //去处不需要的标签
            if (str.startsWith("<br")) str = "";
            if (str.startsWith("<hr")) str = "";
            if (str.startsWith("<p")) str = "";
            if (str.endsWith("p>")) str = "";
            if (str.startsWith("<param")) str = "";
            if (str.startsWith("<input")) str = "";
            if (str.startsWith("<!")) str = "";
            if (str.startsWith("<meta")) str = "";
            if (str.startsWith("<link")) str = "";
            if (str.startsWith("<img")||str.startsWith("</img")) str = "";
            if (str.startsWith("<font")||str.startsWith("</font")) str = "";
            if (str.startsWith("<span")||str.startsWith("</span")) str = "";
            if (str.startsWith("</a>")) str = "";
        }
        return str;
    }

    @Test
    public  void eee()
    {
        System.out.println(cleanLabel("　&nbsp; 我市自2013年11月获得第二批国家公共文化服务体系示范区创建资格后，市委、市政府对示范区创建工作高度重视、全面部署，各级各部门按照示范区创建要求开展了扎实有效的攻坚工作，通过两年的努力，取得了良好的创建效果&nbsp;"));
    }




    /**
     * token提取
     * 使用自动转换机原理
     *
     * @param html
     * @return token列表
     * @throws IOException
     */
    private static List<String> parserHtml(String html) throws IOException
    {
        List<String> tokens = new ArrayList<String>();
        String data = "";
        char word;

        for (int i = 0; i < html.length(); i++)
        {
            //获取字符
            word = html.charAt(i);

            //遇见结束字符 '<'
            if (word == '<')
            {
                data = cleanLabel(data);
                if (data.length() > 0) tokens.add(data);
                data = "<";
                continue;
            }

            //遇见结束字符 '>'
            if (word == '>')
            {
                data = cleanLabel(data + word);
                if (data.length() > 0)
                {
                    //当前data为独立标签，如<img/>，则将其转换为<img></img>
                    if (data.endsWith("/>"))
                    {
                        tokens.add(data.replaceAll("/>", ">"));
                        tokens.add("</" + getLabelName(data) + ">");
                    } else tokens.add(data);
                }
                data = "";
                continue;
            }

            //不为结束符，则更新该字符到data
            data += word;
        }

        return tokens;
    }


    private static String jsoup(String html) throws IOException
    {
        Document parse = Jsoup.parse(html);
        parse.select("script").remove();
        parse.select("style").remove();
        html = parse.toString();
        return html;
    }


    /**
     * 将String转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream StringTOInputStream(String in) throws Exception{

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }


    private static String jtidy(String html) throws Exception
    {
        Tidy tidy = new Tidy();

        //测试资源,与当前类同包
        InputStream is = StringTOInputStream(html);

        /*
         * 配置
         */

        //打印配置
        //tidy.getConfiguration().printConfigOptions(new PrintWriter(System.out), true);

        //读取配置文件
        //tidy.setConfigurationFromFile("config.txt");
        //tidy.setConfigurationFromProps(null);


        //是否缩进
        tidy.setIndentContent(true);

        //设置输出错误与警告信息
        StringWriter stringWriter = new StringWriter();
        PrintWriter errorWriter = new PrintWriter(stringWriter);
        tidy.setErrout(errorWriter);

        //是否XHTML,若是: <br> -> <br/> ; <img src=""> -> <img src=""> ....
        tidy.setXHTML(true);

        //是否隐藏注释
        tidy.setHideComments(true);

        //是否br在一行中显示
        tidy.setBreakBeforeBR(true);

        //不知道是啥
        //tidy.setBurstSlides(false);

        //是否删除空的<p></p>
        tidy.setDropEmptyParas(false);

        //是否用p标签包括文字,如测试html的: plz save me
        tidy.setEncloseBlockText(false);

        //url中的 \ -> /
        tidy.setFixBackslash(true);

        //属性也换行,真疯狂
        tidy.setIndentAttributes(false);

        //不知道是啥
        //tidy.setJoinStyles(false);


        //是否只有body内容
        tidy.setPrintBodyOnly(false);

        //移除空元素如:<div></div>
        tidy.setTrimEmptyElements(false);

        //是否节点结束后另起一行
        tidy.setSmartIndent(true);

        //是否用em替代i，strong替代b
        tidy.setLogicalEmphasis(true);

        //是否把大小的标记转换成小写
        tidy.setUpperCaseTags(true);

        //一行有多长
        tidy.setWraplen(1000);

        //正确显示中文
        tidy.setInputEncoding("gbk");

        /*
         * 执行处理
         */



        //格式化打印
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        org.w3c.dom.Document doc = tidy.parseDOM(is, null);
        tidy.pprint(doc, out);
        String tidied = new String(out.toByteArray());
        //TODO 不要忘记关闭流
        return tidied;
    }

    public static List<String> getTokenList(String html) throws Exception
    {
        html = jsoup(html);
        return parserHtml(html);
    }

}
