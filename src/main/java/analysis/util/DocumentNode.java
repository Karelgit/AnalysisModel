package analysis.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/18.
 */
public class DocumentNode
{
    private int key;//�Լ�λ��
    private int parent;//���ڵ�λ��
    private int level;//����
    private String value=null;//��ǩ����
    private List<Integer> childs=new ArrayList<Integer>();

    private boolean isContent = false;//�Ƿ�Ϊ���ݿ�
    private String sort="";





    public DocumentNode(int key, int parent, int level)
    {
        this.key = key;
        this.parent = parent;
        this.level = level;
    }


    public String getSort()
    {
        return sort;
    }

    public void setSort(String sort)
    {
        this.sort = sort;
    }

    public int getParent()
    {
        return parent;
    }

    public void setParent(int parent)
    {
        this.parent = parent;
    }

    public List<Integer> getChilds()
    {
        return childs;
    }

    public void addChilds(int child)
    {
        this.childs.add(child);
    }

    public void setChilds(List<Integer> childs)
    {
        this.childs = childs;
    }

    public int getKey()
    {
        return key;
    }

    public void setKey(int key)
    {
        this.key = key;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public boolean isContent()
    {
        return isContent;
    }

    public void setIsContent(boolean isContent)
    {
        this.isContent = isContent;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
