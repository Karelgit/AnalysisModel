package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karel on 2016/7/22.
 */
public class FileReader {
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

    public static void main(String[] args) throws Exception{
        readFile("C:\\Users\\karel\\Desktop\\sites.txt");
    }
}
