package analysis.util;

import java.util.List;

/**
 * Created by Administrator on 2016/2/18.
 */
public class Document
{
    private List<DocumentNode> tree;


    public Document(List<DocumentNode> tree)
    {
        this.tree = tree;
    }

    public List<DocumentNode> getTree()
    {
        return tree;
    }

    public void setTree(List<DocumentNode> tree)
    {
        this.tree = tree;
    }

    public DocumentNode getNode(int index)
    {
        return tree.get(index);
    }

    public int size()
    {
        return tree.size();
    }
}
