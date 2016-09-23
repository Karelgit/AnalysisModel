package entry;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.jetty.util.ajax.JSON;
import util.FileUtil;
import util.JSONUtil;
import util.StringUtil;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 原始文章内容的分割，分割为3个部分
 * title
 * content
 * 包含词性/ns/nr/nt的content内容
 *
 * @Author： Huanghai
 * @Version: 2016-09-02
 **/
public class ParamSplit {
    public static void  splitParams(String path,String des_path)  {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String token = sdf.format(new Date(System.currentTimeMillis()));
        String title = "";
        String content="";
        String filtered_content = "";
        ArrayList<String> param = new ArrayList<>();

        String tmp_content = null;
        try {
            tmp_content = FileUtil.readFileByStringBuffer(path,param);
        } catch (IOException e) {
            e.printStackTrace();
        }

        content = ansjFilter(tmp_content);
        filtered_content= nounFilter(tmp_content);
//        FileUtil.writeToFile(des_path+ "\\content_filtered\\contentFiltered_"+token,filtered_content);
//        FileUtil.writeToFile(des_path + "\\content\\content_"+token,content);
        FileUtil.writeToFile(des_path+ "/content_filtered/contentFiltered_"+token,filtered_content);
        FileUtil.writeToFile(des_path + "/content/content_"+token,content);
        if(param.size()>1)  {
            title = ansjFilter(param.get(1));
        }
//        FileUtil.writeToFile(des_path + "\\title\\title_"+token,title);
        FileUtil.writeToFile(des_path + "/title/title_"+token,title);
    }

    public static String ansjFilter(String input) {
        String output = "";
        input = StringUtil.removeSymbol(input);
        List<Term> parse = ToAnalysis.parse(input);
        String tmp;
        String result="";
        for (Term term : parse) {
            tmp = term.toString();
            if(tmp.contains("/"))   {
                result = tmp.substring(0,tmp.indexOf("/"));
            }
            output+= result +" ";
        }
        return output;
    }

    public static String nounFilter (String input) {
        StringBuffer stringBuffer = new StringBuffer();
        input = StringUtil.removeSymbol(input);
        List<Term> parse = ToAnalysis.parse(input);
        String tmp;
        String noun;
        for (Term term : parse) {
            tmp = term.toString();
            if(tmp.contains("/"))   {
                noun = tmp.substring(tmp.indexOf("/")+1,tmp.length());
                if(noun.equals("ns")||noun.equals("nr")||noun.equals("nt")) {
                    stringBuffer.append(tmp.substring(0,tmp.indexOf("/")) + " ");
                }
            }
        }
        return stringBuffer.toString();
    }

    public static String splitParamsContent(String input)  {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String content="";
        ArrayList<String> param = new ArrayList<>();

        String tmp_content = null;
        try {
            tmp_content = FileUtil.fillParam(input,param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        content = ansjFilter(tmp_content);
        JSONObject content_jsonObject = new JSONObject();
        try {
            content_jsonObject.put("text",content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String output_content = content_jsonObject.toString();

//        System.out.println(output_content + "\n");
        return output_content;
    }

    public static String splitParamsFilterContent(String input)  {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String filtered_content="";
        ArrayList<String> param = new ArrayList<>();

        String tmp_content = null;
        try {
            tmp_content = FileUtil.fillParam(input,param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        filtered_content = nounFilter(tmp_content);
        JSONObject content_jsonObject = new JSONObject();
        try {
            content_jsonObject.put("text",filtered_content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String output_filtered_content = content_jsonObject.toString();

//        System.out.println(output_filtered_content + "\n");
        return output_filtered_content;
    }

    public static String splitParamsTitle(String input)  {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String title = "";
        ArrayList<String> param = new ArrayList<>();

        String tmp_content = null;
        try {
            tmp_content = FileUtil.fillParam(input,param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(param.size()>1)  {
            title = ansjFilter(param.get(1));
        }

        JSONObject title_jsonObject = new JSONObject();
        try {
            title_jsonObject.put("text",title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String output_title = title_jsonObject.toString();

//        System.out.println(output_title + "\n" );
        return output_title;
    }

    //测试
    public static void main(String[] args) throws IOException{
        String path = "D:\\data.txt";
        String des_path = "D:\\TestData";
        splitParams(path,des_path);
    }
}
