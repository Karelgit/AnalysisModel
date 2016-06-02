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

        //��ʼ�����ڵ�
        DocumentNode root = new DocumentNode(0, 0, 0);
        tree.add(root);

        DocumentNode parent = root;
        //����tokens�б�����
        for (int i = 0; i < tokens.size(); i++)
        {

            String token = tokens.get(i);
            System.out.println(token);
            String sort = getTokenSort(token);

            //��ʼ��ǩ
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

            //������ǩ
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

            //����
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
     * �ж�������ǩ�Ƿ��Ӧ
     *
     * @param start ��ʼ��ǩ
     * @param end   ������ǩ
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
     * �жϱ�ǩ����
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
