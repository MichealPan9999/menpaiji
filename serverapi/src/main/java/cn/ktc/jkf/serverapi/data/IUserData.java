package cn.ktc.jkf.serverapi.data;

/**
 * 用于保存/读取用户附加的数据
 *
 * @author hq
 */
public interface IUserData {
    /**
     * 设置用户数据
     * @param userData 用户数据
     */
    void setUserData(Object userData);

    /**
     * 获取用户数据
     * @return 返回设置的用户数据
     */
    Object getUserData();
}
