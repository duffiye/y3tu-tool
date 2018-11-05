package com.y3tu.tool.extra.qrcode;

import com.y3tu.tool.core.exception.ExceptionUtil;
import com.y3tu.tool.core.util.StrUtil;

/**
 * Qrcode异常
 *
 * @author xiaoleilu
 */
public class QrCodeException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public QrCodeException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public QrCodeException(String message) {
        super(message);
    }

    public QrCodeException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public QrCodeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public QrCodeException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
