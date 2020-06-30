package cn.ktc.meeting.serverapi;

import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;

/**
 * 测试 https://hwrite.horion.com/doc/genDocId?docId=5bf37c53ba9aa
 * {
 * statusCode: 200
 * }
 */
public class HorionTestResult extends BaseJsonBean {
    private int statusCode = -1;

    public int getStatusCode() {
        return statusCode;
    }
}
