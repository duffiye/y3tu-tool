package com.y3tu.tool.web.annotation;

import com.y3tu.tool.web.codegen.config.GeneratorAutoConfig;
import com.y3tu.tool.web.codegen.controller.GeneratorController;
import com.y3tu.tool.web.codegen.service.impl.GeneratorServiceImpl;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 在启动类上添加该注解来启用代码生成器
 * <p>
 * 注意:
 * 代码生成器需要mybatis-plus,velocity支持
 * <p>
 * 访问：
 * /generator/page/ 获取数据源中表列表
 * /generator/code/ 生成相应表的代码（需要传入GenConfig参数对象）
 * <p>
 * 可以改变代码生成器的默认配置
 * 在自己的项目下添加classpath:config/codegen.properties文件,具体改变信息可以参考jar中的codegen.properties文件
 *
 * @author y3tu
 * @date 2018/10/5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({GeneratorAutoConfig.class, GeneratorController.class, GeneratorServiceImpl.class})
public @interface EnableCodeGenTool {
}