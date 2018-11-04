package com.y3tu.tool.web.xss;


import com.y3tu.tool.core.exceptions.BusinessException;
import com.y3tu.tool.core.util.StrUtil;

/**
 * @author y3tu
 * @date 2018/1/16
 */
public class SqlFilter {
    /**
     * SQL注入过滤
     *
     * @param str 待验证的字符串
     */
    public static String sqlInject(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        //去掉'|"|;|\字符
        str = StrUtil.replace(str, "'", "");
        str = StrUtil.replace(str, "\"", "");
        str = StrUtil.replace(str, ";", "");
        str = StrUtil.replace(str, "\\", "");
        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alert", "drop"};

        //判断是否包含非法字符
        for (String keyword : keywords) {
            if (str.indexOf(keyword) != -1) {
                throw new BusinessException("包含非法字符");
            }
        }

        return str;

    }
}
