package cn.ktc.jkf.serverapi.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 最基本的分组的信息
 */
public class BaseNode implements Cloneable, IUserData {
    /**
     * 父节点的描述
     * JAVA BEAN 为null，仅仅在App中内部数据中使用
     */
    @Expose(serialize = false, deserialize = false)
    private BaseNode parent;
    /**
     * 当前节点ID
     */
    @Expose
    @SerializedName(value = "id")
    protected long id;
    /**
     * 当前节点名称
     */
    @Expose
    @SerializedName(value = "name")
    protected String name;

    /**
     * 当前节点的描述
     */
    @Expose
    @SerializedName(value = "desc")
    protected String desc;

    /**
     * 父节点的ID
     * 有时候JSON中会使用gid，此处做转换
     */
    @Expose
    @SerializedName(value = "pid", alternate = {"gid"})
    private long pid;

    /**
     * 排序。App中将会按照升序排列
     */
    @Expose
    @SerializedName(value = "seq")
    private int seq;

    @Expose(serialize = false, deserialize = false)
    private Object userData;

    public long getId() {
        return id;
    }

    public long getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getSeq() {
        return seq;
    }

    /**
     * 仅仅在App中的数据使用，JAVA BEAN直接使用会返回null
     * 因此JSON反序列化过来仅仅有pid
     */
    public BaseNode getParent() {
        return parent;
    }

    public BaseNode setParent(BaseNode parent) {
        this.parent = parent;
        this.pid = parent.id;
        return this;
    }

    /**
     * 判断是否是根节点。与后台约定：根节点的Pid==-1
     */
    public boolean isRoot() {
        return pid == -1;
    }

    public BaseNode from(BaseNode src) {
        this.id = src.id;
        this.pid = src.pid;
        this.name = src.name;
        return this;
    }

    @Override
    protected BaseNode clone() {
        try {
            return (BaseNode) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    @Override
    public Object getUserData() {
        return userData;
    }
}
