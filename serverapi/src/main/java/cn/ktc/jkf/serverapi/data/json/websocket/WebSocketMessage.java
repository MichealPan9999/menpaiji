package cn.ktc.jkf.serverapi.data.json.websocket;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.concurrent.atomic.AtomicInteger;

import cn.ktc.jkf.serverapi.data.ErrorCode;
import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;
import cn.ktc.jkf.serverapi.data.json.JsonHelper;
import cn.ktc.jkf.serverapi.data.json.websocket.message.ChannelInfoMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.message.EmptyMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.message.HeartbeatMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.message.InitMessage;

/**
 * WebSocket 消息用到的 基类，其他所有的类型{@link MessageType}都有单独的JAVA类实现
 *
 * @author hq
 */
public class WebSocketMessage extends BaseJsonBean {
    /**
     * {@link #ptype} 字段：转发到其他客户端
     */
    public static final int PTYPE_SEND_OTHERCLIENT = 0x01;
    /**
     * {@link #ptype} 字段：后台需要处理 {@link #data}内容
     */
    public static final int PTYPE_PROCESS = 0x02;
    /**
     * {@link #ptype} 字段：后台需要回复。回复的 msgtype={@link #rtype}
     */
    public static final int PTYPE_REPLY = 0x04;

    /**
     * {@link #ptype} 字段：转发到其他客户端，其他客户端接收到处理 此字段忽略
     */
    public static final int PTYPE_RECV_PROCESS = 0x10;
    /**
     * 自增ID。每创建一个消息就增加
     */
    @Expose(serialize = false, deserialize = false)
    private static volatile AtomicInteger MSGID = new AtomicInteger(0);

    /**
     * 保存JSON字符串解析后的JsonObject对象
     */
    @Expose(serialize = false, deserialize = false)
    protected JsonObject jsonObject;

    /**
     * WebSocket时使用，消息类型
     */
    @Expose
    @SerializedName("msgtype")
    protected int msgType;

    /**
     * WebSocket时使用，消息的ID App在发送时，会自动+1
     */
    @Expose
    @SerializedName("msgid")
    protected int msgId;

    /**
     * WebSocket时使用，指明时针对哪个消息回复
     */
    @Expose
    @SerializedName("replyid")
    protected Integer replyId = null;

    @Expose
    @SerializedName(value = "reqid")
    private String reqId;

    /**
     * 数据字段。例如：所有的同步数据都采用此字段保存，且为JsonString格式
     */
    @Expose
    @SerializedName(value = "data")
    protected String data;

    /**
     * 实际上就是data中的type。放到外面，后台转发时附加上，App接收到后不需要反序列化2次
     */
    @Expose
    @SerializedName("ctype")
    protected Integer ctype;

    /**
     * 告知服务器的处理方式 </br>
     * 0x01:表示需要转发到其他客户端 </br>
     * 0x02:后台需 要特殊处理。此时后台需要解析出**data**内容并根据msgtype处理</br>
     * 0x04:需要回复。后台检测到该标志位后，需要回复一个空包到发送端 </br>
     * 0x10:接收端处理。后台原样转发到接收端
     */
    @Expose
    @SerializedName("ptype")
    protected Integer ptype;

    /**
     * 当 {@link #ptype} 设置为 &0x04 时，必须要设置该字段 后台回复时的msgType会使用这个字段的取值
     */
    @Expose
    @SerializedName("rtype")
    protected Integer rtype;
    /**
     * 当App端指定 ptype&PTYPE_REPLY 时，后台将会把这个字段内容原封不动返回到App App设置ID等用来区分不同的信息
     */
    @Expose
    @SerializedName("rData")
    protected String rData;

    /**
     * 接收的内容
     */
    @Expose
    @SerializedName("action")
    protected String action;
    /**
     * 接收的内容
     */
    @Expose
    @SerializedName("version")
    protected int version;
    /**
     * 接收的内容
     */
    @Expose
    @SerializedName("content")
    protected String content;

    public static int newMsgId() {
        return MSGID.incrementAndGet();
    }

    public WebSocketMessage() {
        this.code = ErrorCode.SUCCESS;
        this.msgId = newMsgId();
    }

    public WebSocketMessage(int msgType) {
        this.code = ErrorCode.SUCCESS;
        this.msgId = newMsgId();
        this.msgType = msgType;
    }

    public final int getMsgType() {
        return msgType;
    }

    public final int getMsgId() {
        return msgId;
    }

    public final int getReplyId() {
        return replyId == null ? 0 : replyId;
    }

    public final WebSocketMessage setReplyId(int replyId) {
        this.replyId = replyId;
        return this;
    }

    public void setMsgType(int type) {
        this.msgType = type;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCType() {
        return ctype == null ? 0 : ctype;
    }

    public void setCType(int ctype) {
        this.ctype = ctype;
    }

    public int getPType() {
        return ptype == null ? 0x01 : ptype;
    }

    public void setPType(Integer ptype) {
        this.ptype = ptype;
    }

    public void setRType(Integer rtype) {
        this.rtype = rtype;
    }

    public boolean needReply() {
        return ptype != null & (0 != (ptype & PTYPE_REPLY));
    }

    public int getRType() {
        return this.rtype == null ? this.msgType : this.rtype;
    }

    public void setRData(String rdata) {
        this.rData = rdata;
    }

    public String getRData() {
        return this.rData;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 更新MsgId Heartbeat消息可能会被复用，每发送一次时需要更新MsgId
     */
    public final void updateMsgId() {
        msgId = newMsgId();
    }

    /**
     * 获取解析出来的JsonObject对象
     */
    public JsonObject getJsonObject() {
        return jsonObject;
    }

    /**
     * 当要对某个消息回复时，调用此方法快速设置一些字段
     *
     * @param message 需要回复的消息
     */
    public void replyMessage(WebSocketMessage message) {
        this.replyId = message.msgId;
        this.rData = message.rData;
        this.msgType = message.getRType();
    }

    /**
     * 获取一个空的Reply消息，用于回复后台
     *
     * @param message 需要回复的消息。从此消息中获取到msgId
     * @return WebSocketMessage对象
     */
    public static WebSocketMessage getEmptyReplyMessage(WebSocketMessage message) {
        WebSocketMessage replyMessage = new WebSocketMessage();
        replyMessage.msgType = MessageType.NULL_DATA;
        replyMessage.replyId = message.msgId;
        return replyMessage;
    }

    public static boolean checkNeedReply(int ptype) {
        return 0 != (ptype & PTYPE_REPLY);
    }

    public static boolean checkNeedSendOther(int ptype) {
        return 0 != (ptype & PTYPE_SEND_OTHERCLIENT);
    }

    /**
     * 根据返回的内容，解析出MessageType后创建对应的JavaBean
     *
     * @param json JSON字符串
     * @return JAVABEAN对象
     */
    public static WebSocketMessage fromJson(String json) {
        JsonObject jsonObject = JsonHelper.fromString2(json);
        if (json == null) {
            return null;
        }
        WebSocketMessage webSocketMessage = JsonHelper.fromJsonObject(jsonObject, WebSocketMessage.class);
        if (webSocketMessage == null) {
            return null;
        }
        switch (webSocketMessage.getMsgType()) {
            case MessageType.NULL_DATA:
                webSocketMessage = JsonHelper.fromJsonObject(jsonObject, EmptyMessage.class);
                break;
            case MessageType.HEARTBEAT:
                webSocketMessage = JsonHelper.fromJsonObject(jsonObject, HeartbeatMessage.class);
                break;
            case MessageType.APP_INIT:
                webSocketMessage = JsonHelper.fromJsonObject(jsonObject, InitMessage.class);
                break;
            case MessageType.TV_EMERGENCY:
                webSocketMessage = JsonHelper.fromJsonObject(jsonObject, WebSocketMessage.class);
                break;
            case MessageType.TV_CHANNELINFO:
                webSocketMessage = JsonHelper.fromJsonObject(jsonObject, ChannelInfoMessage.class);
                break;
            case MessageType.TV_CHANNELINFO_STOP:
                webSocketMessage = JsonHelper.fromJsonObject(jsonObject, ChannelInfoMessage.class);
                break;
            default:
                webSocketMessage = JsonHelper.fromJsonObject(jsonObject, WebSocketMessage.class);
                break;
        }
        webSocketMessage.jsonObject = jsonObject;
        return webSocketMessage;
    }
}
