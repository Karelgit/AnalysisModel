package analysis.content;

import analysis.util.Document;
import analysis.util.DocumentNode;
import analysis.util.DocumentParser;
import analysis.util.TokenParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ContentParser
{


    private Document document;
    private List<String> contentList = new ArrayList<String>();


    private void parser()
    {
        map = new HashMap<Integer, Integer>();
        countContent(0);
        countContentList(0);
    }


    public ContentParser(Document document)
    {
        this.document = document;
        parser();
    }


    public ContentParser(String html) throws Exception
    {
        try
        {
            List<String> tokens = TokenParser.getTokenList(html);
            DocumentParser documentParser = new DocumentParser(tokens);
            document = documentParser.getDocument();
            parser();
        } catch (IOException e)
        {
            System.out.println("网页格式不规范，token列表构造失败");
            e.printStackTrace();
        }
    }


    /**
     * 获取该节点下所有的内容块
     *
     * @param index
     */
    private String getNodeContent(int index)
    {
        String text = "";
        DocumentNode node = document.getNode(index);
        List<Integer> childs = node.getChilds();

        if (node.getSort().equals("CONTENT")) text = node.getValue();

        if (childs.size() > 0)
        {
            for (int i = 0; i < childs.size(); i++)
            {
                text += getNodeContent(childs.get(i));
            }
        }
        return text;
    }

    /**
     * 获取所有内容块内容，并更新到contentList
     *
     * @param index
     */
    private void countContentList(int index)
    {
        DocumentNode node = document.getNode(index);
        List<Integer> childs = node.getChilds();
        for (int i = 0; i < childs.size(); i++)
        {
            DocumentNode newNode = document.getNode(childs.get(i));
            if (newNode.isContent())
            {

                contentList.add(getNodeContent(childs.get(i)));
                System.out.println(getNodeContent(childs.get(i)) + "\n---------------------------------------\n");
            } else countContentList(childs.get(i));
        }

    }


    /**
     * 通过跟节点做为起始点 遍历计算出内容块
     * 为内容块条件：
     * 1.当前节点为叶子节点且当前节点为内容节点(isText)
     * 2.当前节点为枝节点且当前节点的所有子节点为内容块的比例大于指定阀值(isContent)
     *
     * @param index 节点所在document.tree中的位置
     */

    private Map<Integer, Integer> map;//用于剪枝，标记该节点是否已近被计算过

    private void countContent(int index)
    {
        DocumentNode node = document.getNode(index);
        List<Integer> childs = node.getChilds();
        int contentNumber = 0;

        if (childs.size() > 0)
        {
            for (int i = 0; i < childs.size(); i++)
            {
                int child = childs.get(i);
                if (map.get(child) == null) countContent(child);
                DocumentNode childNode = document.getNode(child);
                if (childNode.isContent()) contentNumber++;
            }
            //当前节点为枝节点且当前节点的所有子节点为内容块的比例大于以下阀值
            if (contentNumber * 1.0 / childs.size() > 0.9) node.setIsContent(true);
            else
            {
                node.setIsContent(false);
            }
        } else
        {
            if (node.getSort().equals("CONTENT")||node.getSort().equals("LINK_LABEL")) node.setIsContent(true);
            else node.setIsContent(false);
        }

        map.put(index, 1);//染色该节点，剪枝
    }


    public List<String> getContentList()
    {
        return contentList;
    }
}
