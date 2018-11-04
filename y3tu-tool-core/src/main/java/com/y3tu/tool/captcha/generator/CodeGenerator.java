package com.y3tu.tool.captcha.generator;

/**
 * 验证码文字生成器
 *
 * @author looly
 */
public interface CodeGenerator {
    /**
     * 生成验证码
     *
     * @return 验证码
     */
    public String generate();

    /**
     * 获取验证码长度
     *
     * @return 验证码长度
     */
    public int getLength();
}
