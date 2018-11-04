package com.y3tu.tool.extra.template;

import com.y3tu.tool.extra.template.engine.EngineFactory;

/**
 * 模板工具类
 *
 * @author looly
 */
public class TemplateUtil {

    /**
     * 根据用户引入的模板引擎jar，自动创建对应的模板引擎对象，使用默认配置
     *
     * @return {@link Engine}
     */
    public static Engine createEngine() {
        return createEngine(new TemplateConfig());
    }

    /**
     * 根据用户引入的模板引擎jar，自动创建对应的模板引擎对象
     *
     * @param config 模板配置，包括编码、模板文件path等信息
     * @return {@link Engine}
     */
    public static Engine createEngine(TemplateConfig config) {
        return EngineFactory.create(config);
    }
}
