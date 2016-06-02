package analysis.link;

import analysis.judge.JudgeURL;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/15.
 */
public class LinkValue
{
    private List<List<Url>> contentList;//���ݿ��б�


    final static double textDensity = 6;


    public LinkValue(List<List<Url>> contentList)
    {
        this.contentList = contentList;
    }

    /**
     * ��ȡ����ҳ��������������
     * @return
     */
    public List<Url> getContent()
    {
        List<Url> ans = new ArrayList<Url>();
        List<Url> urls;

        for (int i = 0; i < contentList.size(); i++)
        {
            urls = contentList.get(i);

            if (isContent(urls))
            {
                for (int j = 0; j < urls.size(); j++)
                {
                    Url url = urls.get(j);
                    if (JudgeURL.getSortByURL(url.getUrl()) != 1) ans.add(url);
                    else
                    {
                        if (isNextPage(url.getUrl(), url.getTitle()))
                        {
                            ans.add(url);
                        }
                    }
                }
            } else
            {
                //��ѯ�Ƿ������һҳ
                for (int j = 0; j < urls.size(); j++)
                {
                    Url url = urls.get(j);
                    if (isNextPage(url.getUrl(), url.getTitle()))
                    {
                        ans.add(url);
                    }
                }
            }
        }
        return ans;
    }


    /**
     * �ж�list�е�url�Ƿ�Ϊ��������
     *
     * @param
     * @return
     */
    public static boolean isContent(List<Url> urls)
    {

        //��ʼ��
        int article = 0;
        int navigate = 0;

        int sum = urls.size();
        int textNum = 0;
        Url url;

        //ͳ���������Ӹ���
        for (int i = 0; i < sum; i++)
        {
            url = urls.get(i);

            //ͳ�����º͵�����
            int sort = JudgeURL.getSortByURL(url.getUrl());
            if (sort == -1) article++;
            if (sort == 1) navigate++;

            textNum += url.getTitle().length();
        }

        //����������������������ﳬ�����·�ֵ����ֱ����Ϊ�������ݿ�
        if ((navigate + 0.0) / sum > 0.6) return false;

        //�������ҳ���ӵ�ռ�� �ȵ���ҳ���ӵ�ռ�ȴ���ֱ����Ϊ�����ݿ�
        if ((article + 0.0) / sum > (navigate + 0.0) / sum) return true;

        //�������������С�ڵ���һ����Ϊ���������ݿ�
        if (sum > 1)
        {
            //��ÿ�����ӵ�ƽ�����ⳤ�ȴ������·�ֵ������Ϊ���ݿ�
            if (textNum / sum > textDensity) return true;
        }
        return false;
    }


    /**
     * �жϸ������Ƿ�Ϊ��̬��ҳ�е� ����һҳ��
     * @param url
     * @param title
     * @return
     */
    static Boolean isNextPage(String url, String title)
    {
        final String NEXTPAGR_URL_MATCHER1 = ".*(next|last|page).*";
        final String NEXTPAGR_URL_MATCHER2 = ".*(\\d+|index)_\\d+.\\w+";
        if (Pattern.matches(NEXTPAGR_URL_MATCHER1, url)) return true;
        if (Pattern.matches(NEXTPAGR_URL_MATCHER2, url)) return true;
        if (title.contains("��ҳ|��һҳ")) return true;
        return false;
    }



}
