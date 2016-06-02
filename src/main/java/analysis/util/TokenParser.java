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
     * ��ȡ��ǩ��
     *
     * @param label
     * @return ��:<a herf=""/> ��ǩ��: a
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
     * �����ǩ�в���Ҫ������
     *
     * @param label
     * @return
     */
    private static String cleanLabel(String label)
    {
        String str;
        str = label.replaceAll("&\\S*;|\\r|\\t|\\n|\\?|��|\\|", "");
        if (!str.startsWith("<"))
            str = str.replaceAll(" |��| ", "");
        else
        {
            //ȥ������Ҫ�ı�ǩ
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
        System.out.println(cleanLabel("��&nbsp; ������2013��11�»�õڶ������ҹ����Ļ�������ϵʾ���������ʸ����ί����������ʾ�������������߶����ӡ�ȫ�沿�𣬸��������Ű���ʾ��������Ҫ��չ����ʵ��Ч�Ĺ��Ṥ����ͨ�������Ŭ����ȡ�������õĴ���Ч��&nbsp;"));
    }




    /**
     * token��ȡ
     * ʹ���Զ�ת����ԭ��
     *
     * @param html
     * @return token�б�
     * @throws IOException
     */
    private static List<String> parserHtml(String html) throws IOException
    {
        List<String> tokens = new ArrayList<String>();
        String data = "";
        char word;

        for (int i = 0; i < html.length(); i++)
        {
            //��ȡ�ַ�
            word = html.charAt(i);

            //���������ַ� '<'
            if (word == '<')
            {
                data = cleanLabel(data);
                if (data.length() > 0) tokens.add(data);
                data = "<";
                continue;
            }

            //���������ַ� '>'
            if (word == '>')
            {
                data = cleanLabel(data + word);
                if (data.length() > 0)
                {
                    //��ǰdataΪ������ǩ����<img/>������ת��Ϊ<img></img>
                    if (data.endsWith("/>"))
                    {
                        tokens.add(data.replaceAll("/>", ">"));
                        tokens.add("</" + getLabelName(data) + ">");
                    } else tokens.add(data);
                }
                data = "";
                continue;
            }

            //��Ϊ������������¸��ַ���data
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
     * ��Stringת����InputStream
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

        //������Դ,�뵱ǰ��ͬ��
        InputStream is = StringTOInputStream(html);

        /*
         * ����
         */

        //��ӡ����
        //tidy.getConfiguration().printConfigOptions(new PrintWriter(System.out), true);

        //��ȡ�����ļ�
        //tidy.setConfigurationFromFile("config.txt");
        //tidy.setConfigurationFromProps(null);


        //�Ƿ�����
        tidy.setIndentContent(true);

        //������������뾯����Ϣ
        StringWriter stringWriter = new StringWriter();
        PrintWriter errorWriter = new PrintWriter(stringWriter);
        tidy.setErrout(errorWriter);

        //�Ƿ�XHTML,����: <br> -> <br/> ; <img src=""> -> <img src=""> ....
        tidy.setXHTML(true);

        //�Ƿ�����ע��
        tidy.setHideComments(true);

        //�Ƿ�br��һ������ʾ
        tidy.setBreakBeforeBR(true);

        //��֪����ɶ
        //tidy.setBurstSlides(false);

        //�Ƿ�ɾ���յ�<p></p>
        tidy.setDropEmptyParas(false);

        //�Ƿ���p��ǩ��������,�����html��: plz save me
        tidy.setEncloseBlockText(false);

        //url�е� \ -> /
        tidy.setFixBackslash(true);

        //����Ҳ����,����
        tidy.setIndentAttributes(false);

        //��֪����ɶ
        //tidy.setJoinStyles(false);


        //�Ƿ�ֻ��body����
        tidy.setPrintBodyOnly(false);

        //�Ƴ���Ԫ����:<div></div>
        tidy.setTrimEmptyElements(false);

        //�Ƿ�ڵ����������һ��
        tidy.setSmartIndent(true);

        //�Ƿ���em���i��strong���b
        tidy.setLogicalEmphasis(true);

        //�Ƿ�Ѵ�С�ı��ת����Сд
        tidy.setUpperCaseTags(true);

        //һ���ж೤
        tidy.setWraplen(1000);

        //��ȷ��ʾ����
        tidy.setInputEncoding("gbk");

        /*
         * ִ�д���
         */



        //��ʽ����ӡ
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        org.w3c.dom.Document doc = tidy.parseDOM(is, null);
        tidy.pprint(doc, out);
        String tidied = new String(out.toByteArray());
        //TODO ��Ҫ���ǹر���
        return tidied;
    }

    public static List<String> getTokenList(String html) throws Exception
    {
        html = jsoup(html);
        return parserHtml(html);
    }

}
