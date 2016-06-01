package analysis.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/18.
 */
public class DocumentParser
{

    private List<DocumentNode> tree = new ArrayList<DocumentNode>();
    private int treeSize = 0;
    private List<String> tokens;

    public DocumentParser(List<String> tokens)
    {
        this.tokens = tokens;
    }

    public Document getDocument()
    {

        //初始化根节点
        DocumentNode root = new DocumentNode(0, 0, 0);
        tree.add(root);

        DocumentNode parent = root;
        //遍历tokens列表构建树
        for (int i = 0; i < tokens.size(); i++)
        {

            String token = tokens.get(i);
            System.out.println(token);
            String sort = getTokenSort(token);

            //开始标签
            if (sort.equals("START_LABEL"))
            {
                int key = treeSize + 1;
                DocumentNode newDocumentNode = new DocumentNode(key, parent.getKey(), parent.getLevel() + 1);
                newDocumentNode.setValue(token);
                addNode(newDocumentNode);

                parent.addChilds(key);
                parent = newDocumentNode;
                continue;
            }

            //结束标签
            if (sort.equals("END_LABEL"))
            {

                while (parent.getKey() != 0)
                {
                    String parentToken = parent.getValue();
                    parent = tree.get(parent.getParent());
                    if (isSameLabel(parentToken, token)) break;
                }

                continue;
            }

            //内容
            int key = treeSize + 1;
            DocumentNode newDocumentNode = new DocumentNode(key, parent.getKey(), parent.getLevel() + 1);
            newDocumentNode.setValue(token);
            newDocumentNode.setSort(sort);
            newDocumentNode.setIsContent(true);
            addNode(newDocumentNode);
            parent.addChilds(key);
            continue;
        }

        Document document = new Document(tree);
        return document;
    }


    public void addNode(DocumentNode node)
    {
        tree.add(node);
        treeSize++;
    }

    /**
     * 判断两个标签是否对应
     *
     * @param start 开始标签
     * @param end   结束标签
     * @return
     */
    Boolean isSameLabel(String start, String end)
    {
        if (getLabelName(start).equals(getLabelName(end))) return true;
        return false;
    }


    private static String getLabelName(String label)
    {
        String sub;
        if (label.contains(" ")) sub = label.split(" ")[0];
        else sub = label.split(">")[0];
        sub = sub.substring(1);
        if (sub.startsWith("/")) sub = sub.substring(1);
        return sub;
    }


    /**
     * 判断标签类型
     *
     * @param token
     * @return
     */
    String getTokenSort(String token)
    {
        if (token.startsWith("<a")) return "LINK_LABEL";
        if (token.startsWith("</")) return "END_LABEL";
        if (token.startsWith("<")) return "START_LABEL";
        return "CONTENT";
    }


}
