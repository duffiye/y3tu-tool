package com.y3tu.tool.core.exception;


/**
 * 默认异常
 *
 * @author y3tu
 */
public enum ErrorEnum implements IError {
    /**
     * 系统内部错误
     */
    SYSTEM_INTERNAL_ERROR("0000", "系统内部错误"),
    /**
     * 无效参数
     */
    INVALID_PARAMETER("0001", "无效参数"),
    /**
     * 服务不存在
     */
    SERVICE_NOT_FOUND("0002", "服务不存在"),
    /**
     * 参数不全
     */
    PARAMETER_REQUIRED("0003", "Parameter required"),
    /**
     * 参数过长
     */
    PARAMETER_MAX_LENGTH("0004", "Parameter max length limit"),
    /**
     * 参数过短
     */
    PARAMETER_MIN_LENGTH("0005", "Parameter min length limit"),
    /**
     * 参数出错
     */
    PARAMETER_ANNOTATION_NOT_MATCH("0006", "Parameter annotation not match"),
    /**
     * 参数验证失败
     */
    PARAMETER_NOT_MATCH_RULE("0007", "Parameter not match validation rule"),
    /**
     * 请求方法出错
     */
    METHOD_NOT_SUPPORTED("0008", "method not supported"),
    /**
     * 不支持的content类型
     */
    CONTENT_TYPE_NOT_SUPPORT("0009", "content type is not support"),
    /**
     * json格式化出错
     */
    JSON_FORMAT_ERROR("0010", "json format error"),
    /**
     * 远程调用出错
     */
    CALL_REMOTE_ERROR("0011", "call remote error"),
    /**
     * 服务运行SQLException异常
     */
    SQL_EXCEPTION("0012", "sql exception"),
    /**
     * 客户端异常 给调用者 app,移动端调用
     */
    CLIENT_EXCEPTION("0013", "client exception"),
    /**
     * 服务端异常, 微服务服务端产生的异常
     */
    SERVER_EXCEPTION("0014", "server exception"),
    /**
     * 授权失败 禁止访问
     */
    ACCESS_DENIED("0015", "access denied"),
    /**
     * 演示环境 没有权限访问
     */
    SHOW_AUTH_CONTROL("0016", "演示环境,没有权限访问"),
    /**
     * 系统繁忙,请稍候再试
     */
    SYSTEM_BUSY("0017", "系统繁忙,请稍候再试"),

    SYSTEM_NO_PERMISSION("0018", "无权限"),
    GATEWAY_ERROR("0019", "网关异常"),
    GATEWAY_CONNECT_TIME_OUT("0020", "网关超时"),
    UPLOAD_FILE_SIZE_LIMIT("0021", "上传文件大小超过限制"),
    UTIL_EXCEPTION("0022", "工具异常"),
    FILE_NOT_FOUND("0023", "找不到指定文件"),
    HTTP_ERROR("0024", "HTTP异常");


    String code;
    String message;

    ErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
