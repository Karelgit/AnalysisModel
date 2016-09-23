package util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karel on 2016/7/22.
 */
public class FileUtil {
    public static final List<String> readFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        List urlList = new ArrayList<String>();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if(!line.startsWith("#"))   {
                urlList.add(line.trim());
                System.out.println(line.trim());
            }
        }
        br.close();
        return urlList;
    }

    public static  String readFileByStringBuffer(String filePath,ArrayList<String> param) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        StringBuffer stringBuffer = new StringBuffer();
        int i= 0;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            param.add(line);
            if(i>1) {
                stringBuffer.append(line);
            }
            i++;
        }
        br.close();
        return stringBuffer.toString();
    }

    public static String fillParam(String s, ArrayList<String> param)   throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes("UTF-8"))));
        StringBuffer stringBuffer = new StringBuffer();
        int i= 0;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            param.add(line);
            if(i>1) {
                stringBuffer.append(line);
            }
            i++;
        }
        br.close();
        return stringBuffer.toString();
    }

    public static void writeToFile(String filePath,String content) {
        try {
            File file = new File(filePath);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file),"utf-8"));
            bw.write(content);
            bw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
//        readFile("D:\\temp\\_input.txt");
//        ArrayList<String> param = new ArrayList<>();
//        readFileByStringBuffer("D:\\data.txt",param);

        writeToFile("D:\\TestData\\test780.txt","1234");
    }
}
