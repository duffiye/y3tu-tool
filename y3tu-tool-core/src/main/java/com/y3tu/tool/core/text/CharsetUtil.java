package com.y3tu.tool.core.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 
 * 尽量使用Charsets.UTF8而不是"UTF-8"，减少JDK里的Charset查找消耗.
 * 
 * 使用JDK7的StandardCharsets，同时留了标准名称的字符串
 * 
 * @author calvin
 */
public class CharsetUtil {

	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	public static final Charset US_ASCII = StandardCharsets.US_ASCII;
	public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
	
	public static final String UTF_8_NAME = StandardCharsets.UTF_8.name();
	public static final String ASCII_NAME = StandardCharsets.US_ASCII.name();
	public static final String ISO_8859_1_NAME = StandardCharsets.ISO_8859_1.name();

	/**
	 * 系统默认字符集编码
	 *
	 * @return 系统字符集编码
	 */
	public static Charset defaultCharset() {
		return Charset.defaultCharset();
	}

	/**
	 * 转换为Charset对象
	 * @param charsetName 字符集，为空则返回默认字符集
	 * @return Charset
	 * @throws UnsupportedCharsetException 编码不支持
	 */
	public static Charset charset(String charsetName) throws UnsupportedCharsetException {
		return StringUtils.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
	}

}