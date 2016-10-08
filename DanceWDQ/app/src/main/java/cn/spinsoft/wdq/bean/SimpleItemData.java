package cn.spinsoft.wdq.bean;

import java.io.Serializable;

/**
 * Created by hushujun on 15/11/13.
 */
public class SimpleItemData implements Serializable {
    private String name;
    private int id;
    private int subId;
    private String content;

    public SimpleItemData(String name, int id){
        this.name = name;
        this.id = id;
    }
    public SimpleItemData(String name, int id,String content) {
        this(name,id);
        this.content = content;
    }

    public SimpleItemData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    @Override
    public String toString() {
        return "SimpleItemData{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", content=" + content +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleItemData)) return false;

        SimpleItemData itemData = (SimpleItemData) o;

        if (getId() != itemData.getId()) return false;
        return getName().equals(itemData.getName());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getId();
        return result;
    }
}
