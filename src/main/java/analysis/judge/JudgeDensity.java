package analysis.judge;

import analysis.util.TokenParser;

import java.util.List;

/**
 * Created by leishichi on 2015/9/15.
 */

public class JudgeDensity
{

    private final static double DENSITY_THRESHOLDS = 0.06;
    private static double sumKey = 0;
    private static int linkTextNumber;


    public static int getSortByDensity(String html) throws Exception
    {
        int linkNumber = 0;
        int textNumber = 0;
        double densityKey;

        //将网页转换为token
        List<String> tokens = TokenParser.getTokenList(html);


        //
        String token, nextToken;
        String sort;
        for (int i = 0; i < tokens.size(); i++)
        {
            token = tokens.get(i);
            sort = getTokenSort(token);
            if (sort.equals("CONTENT")) textNumber += token.length();
            if (sort.equals("LINK_LABEL"))
            {
                linkNumber++;
                nextToken = tokens.get(i + 1);
                if (getTokenSort(nextToken).equals("CONTENT")) i++;
            }
        }
        densityKey = (linkNumber + 0.0) / (textNumber + 0.0);
        if (densityKey >= DENSITY_THRESHOLDS) return (1);
        else return (-1);
    }


    static String getTokenSort(String token)
    {
        if (token.startsWith("<a")) return "LINK_LABEL";
        if (token.startsWith("</")) return "END_LABEL";
        if (token.startsWith("<")) return "START_LABEL";
        return "CONTENT";
    }


}
