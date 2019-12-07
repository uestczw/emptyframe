package com.yianit.common;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 类名称: AjaxJsonResponseWraper
 * 类描述: ajax返回值统一封装.
 * 创建人: zhangwei
 * 创建时间: 2015年9月9日 下午2:42:49
 * 修改人: zhangwei
 * 修改时间: 2015年9月9日 下午2:42:49
 * 修改备注:
 */

public class AjaxJsonResponseWraper {
    /**
     * 
     */
    public static final String HEADER_STR = "HeadInfo";
    /**
     * 
     */
    private static final String CODE_STR = "Code";
    /**
     * 
     */
    private static final String MESSAGE_STR = "Message";
    /**
     * 
     */
    // private static final String ISAJAX_STR = "IsAjax";
    /**
     * 
     */
    private static final String SUCESS_CODE = "0";
    /**
     * 
     */
    private static final String ERROR_CODE = "1";

    private static final String TIMEOUT_CODE = "2";
    /**
     * 
     */
    private static final String DATA_STR = "DataInfo";
    /***/
    private Map<String, Object> jsonMsg = null;

    /**
     * createErrorResponse:(ajax返回失败且带失败信息). <br/>
     * 
     * @author zhangwei
     * @param msg
     * @return
     * @since JDK 1.7
     */

    public static Map<String, Object> createErrorResponse(String msg) {
        Map<String, Object> jsonHeader = new HashMap<String, Object>();
        jsonHeader.put(CODE_STR, ERROR_CODE);
        jsonHeader.put(MESSAGE_STR, msg);

        Map<String, Object> jsonMsg = new HashMap<String, Object>();
        jsonMsg.put(HEADER_STR, jsonHeader); // omit data field
        return jsonMsg;
    }

    public static Map<String, Object> createTimeOutResponse(Map<String, String> data) {
        Map<String, Object> jsonHeader = new HashMap<String, Object>();
        jsonHeader.put(CODE_STR, TIMEOUT_CODE);
        jsonHeader.put(MESSAGE_STR, "登录超时,请刷新界面重新登录");

        Map<String, Object> jsonMsg = new HashMap<String, Object>();
        jsonMsg.put(HEADER_STR, jsonHeader); // omit data field
        jsonMsg.put("DataInfo", data);
        return jsonMsg;
    }

    /**
     * createSuccessResponse:(ajax返回成功且带成功信息). <br/>
     * 
     * @author zhangwei
     * @param msg
     * @return
     * @since JDK 1.7
     */

    public static Map<String, Object> createSuccessResponse(String msg) {
        Map<String, Object> jsonHeader = new HashMap<String, Object>();
        jsonHeader.put(CODE_STR, SUCESS_CODE);
        jsonHeader.put(MESSAGE_STR, msg);

        Map<String, Object> jsonMsg = new HashMap<String, Object>();
        jsonMsg.put(HEADER_STR, jsonHeader); // omit data field
        return jsonMsg;
    }

    /**
     * createSuccessResponseWithData:(ajax返回成功且带数据). <br/>
     * 
     * @author Administrator
     * @param data
     * @return
     * @since JDK 1.7
     */

    public static Map<String, Object> createSuccessResponseWithData(Object data) {
        Map<String, Object> jsonHeader = new HashMap<String, Object>();
        jsonHeader.put(CODE_STR, SUCESS_CODE);
        jsonHeader.put(MESSAGE_STR, "");

        Map<String, Object> jsonMsg = new HashMap<String, Object>();
        jsonMsg.put(HEADER_STR, jsonHeader); // omit data field
        jsonMsg.put("DataInfo", data);
        return jsonMsg;
    }

    /**
     * createSuccessResponseWithDataAndMsg:(ajax返回成功且带信息和数据). <br/>
     * 
     * @author zhangwei
     * @param msg
     * @param data
     * @return
     * @since JDK 1.7
     */

    public static Map<String, Object> createSuccessResponseWithDataAndMsg(String msg, Map<String, Object> data) {
        Map<String, Object> jsonHeader = new HashMap<String, Object>();
        jsonHeader.put(CODE_STR, SUCESS_CODE);
        jsonHeader.put(MESSAGE_STR, msg);

        Map<String, Object> jsonMsg = new HashMap<String, Object>();
        jsonMsg.put(HEADER_STR, jsonHeader); // omit data field
        jsonMsg.put("DataInfo", data);
        return jsonMsg;
    }

    /**
     * setDataField:(添加数据). <br/>
     * 
     * @author zhangwei
     * @param dataKey
     * @param value
     * @since JDK 1.7
     */

    public void setDataField(String dataKey, Object value) {
        Map<String, Object> data = (Map<String, Object>) getJsonMsg().get(DATA_STR);
        data.put(dataKey, value);
    }

    /**
     * setHeaderInfo:(设置自定义头部信息). <br/>
     * 
     * @author zhangwei
     * @param errCode
     * @param msg
     * @since JDK 1.7
     */

    public void setHeaderInfo(int errCode, String msg) {
        Map<String, Object> header = (Map<String, Object>) getJsonMsg().get(HEADER_STR);
        header.put(CODE_STR, errCode);
        header.put(MESSAGE_STR, msg);
    }

    public String getResponse() {
        return JSONObject.toJSONString(jsonMsg);
    }

    /**
     * reset:(情况数据). <br/>
     * 
     * @author zhangwei
     * @since JDK 1.7
     */

    public void reset() {
        getJsonMsg().clear();
    }

    @Override
    public String toString() {
        return getResponse();
    }

    /**
     * getJsonMsg:(获取组装好的json数据). <br/>
     * 
     * @author Administrator
     * @return
     * @since JDK 1.7
     */

    private Map<String, Object> getJsonMsg() {
        if (jsonMsg == null) {
            jsonMsg = buildJsonMsg();
        }
        return jsonMsg;
    }

    /**
     * buildJsonMsg:(组装json数据). <br/>
     * 
     * @author zhangwei
     * @return
     * @since JDK 1.7
     */

    private Map<String, Object> buildJsonMsg() {
        Map<String, Object> jsonHeader = new HashMap<String, Object>();
        jsonHeader.put(CODE_STR, 0);
        jsonHeader.put(MESSAGE_STR, "");

        Map<String, Object> jsonData = new HashMap<String, Object>();

        Map<String, Object> jsonMsg = new HashMap<String, Object>();
        jsonMsg.put(HEADER_STR, jsonHeader);
        jsonMsg.put(DATA_STR, jsonData);
        return jsonMsg;
    }
}
