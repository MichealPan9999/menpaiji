package cn.ktc.jkf.serverapi;

import cn.ktc.jkf.serverapi.data.ErrorCode;
import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;

/**
 * 后台请求异常基类
 * 后台请求自定义异常时，必须要通过这个基类
 *
 * @author hq
 */
public class ServerApiException extends Exception {
    private static final long serialVersionUID = 1;
    /**
     * 定义各个API的 IDException 的起始顺序
     */
    public static final int IDEXCEPTION_LOGINAPI = 1000;
    public static final int IDEXCEPTION_COMPANYAPI = 2000;
    public static final int IDEXCEPTION_MEETINGAPI = 3000;
    /**
     * 保存出错时的JavaBean对象
     */
    protected final BaseJsonBean resultObject;
    /**
     * 定义异常的类型ID
     */
    protected final int idException;

    public ServerApiException(BaseJsonBean jsonBean, int id) {
        super(jsonBean.getMessage());
        this.resultObject = jsonBean;
        this.idException = id;
    }


    public int getErrorCode() {
        return resultObject.getCode();
    }

    public BaseJsonBean getResultObject() {
        return resultObject;
    }

    /**
     * 检测到用户TOKEN无效时，用户应该退出，返回登录页面
     */
    public boolean isUTokenInvalid() {
        return resultObject.getCode() == ErrorCode.UTOKEN_INVALID;
    }
}
