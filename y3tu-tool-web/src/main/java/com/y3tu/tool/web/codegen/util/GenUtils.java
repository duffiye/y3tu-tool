package com.y3tu.tool.web.codegen.util;

import com.y3tu.tool.core.date.DateUtil;
import com.y3tu.tool.core.exception.UtilException;
import com.y3tu.tool.core.io.FileUtil;
import com.y3tu.tool.core.io.IOUtil;
import com.y3tu.tool.core.lang.Console;
import com.y3tu.tool.core.lang.Platforms;
import com.y3tu.tool.core.reflect.ReflectionUtil;
import com.y3tu.tool.core.text.CharsetUtil;
import com.y3tu.tool.core.text.StringUtils;
import com.y3tu.tool.core.util.RuntimeUtil;
import com.y3tu.tool.db.ds.DSFactory;
import com.y3tu.tool.db.ds.DsFactoryEnum;
import com.y3tu.tool.db.meta.Column;
import com.y3tu.tool.db.meta.DataTypeEnum;
import com.y3tu.tool.db.meta.MetaUtil;
import com.y3tu.tool.db.meta.Table;
import com.y3tu.tool.setting.Setting;
import com.y3tu.tool.web.codegen.entity.ColumnEntity;
import com.y3tu.tool.web.codegen.entity.GenConfig;
import com.y3tu.tool.web.codegen.entity.TableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author lengleng
 * @date 2018-07-30
 */
@Slf4j
public class GenUtils {

    private static final String ENTITY_JAVA_VM = "Entity.java.vm";
    private static final String MAPPER_JAVA_VM = "Mapper.java.vm";
    private static final String SERVICE_JAVA_VM = "Service.java.vm";
    private static final String SERVICE_IMPL_JAVA_VM = "ServiceImpl.java.vm";
    private static final String CONTROLLER_JAVA_VM = "Controller.java.vm";
    private static final String MAPPER_XML_VM = "Mapper.xml.vm";
    private static final String MENU_SQL_VM = "menu.sql.vm";
    private static final String INDEX_VUE_VM = "index.vue.vm";
    private static final String API_JS_VM = "api.js.vm";
    private static final String CRUD_JS_VM = "crud.js.vm";

    private static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("codegen-template/Entity.java.vm");
        templates.add("codegen-template/Mapper.java.vm");
        templates.add("codegen-template/Mapper.xml.vm");
        templates.add("codegen-template/Service.java.vm");
        templates.add("codegen-template/ServiceImpl.java.vm");
        templates.add("codegen-template/Controller.java.vm");
        templates.add("codegen-template/menu.sql.vm");

        templates.add("codegen-template/index.vue.vm");
        templates.add("codegen-template/api.js.vm");
        templates.add("codegen-template/crud.js.vm");
        return templates;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(GenConfig genConfig, Setting setting, Table table, String[] columns, ZipOutputStream zip) {

        if (setting == null) {
            setting = new Setting("config/codegen.properties");
        }

        //配置信息
        boolean hasBigDecimal = false;
        boolean hasDate = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.getTableName());

        if (StringUtils.isNotBlank(genConfig.getComments())) {
            tableEntity.setComments(genConfig.getComments());
        } else {
            tableEntity.setComments(table.getRemarks());
        }

        String tablePrefix;
        if (StringUtils.isNotBlank(genConfig.getTablePrefix())) {
            tablePrefix = genConfig.getTablePrefix();
        } else {
            tablePrefix = setting.getStr("tablePrefix");
        }

        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), tablePrefix);
        tableEntity.setCaseClassName(className);
        tableEntity.setLowerClassName(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columnList = new ArrayList<>();

        for (String columnName : columns) {
            Column column = table.get(columnName);


            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(columnName);
            columnEntity.setDataType(column.getTypeName());
            columnEntity.setComments(column.getComment());


            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setCaseAttrName(attrName);
            columnEntity.setLowerAttrName(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = DataTypeEnum.getJavaType(column.getTypeName());
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            if (!hasDate && "Date".equals(attrType)) {
                hasDate = true;
            }
            //是否主键
            Set<String> pkNames = table.getPkNames();
            for (String pkName : pkNames) {
                if (columnName.equals(pkName)) {
                    tableEntity.setPk(columnEntity);
                }
            }
            columnList.add(columnEntity);
        }
        tableEntity.setColumns(columnList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        //封装模板数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", tableEntity.getTableName());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getCaseClassName());
        map.put("classname", tableEntity.getLowerClassName());
        map.put("pathName", tableEntity.getLowerClassName().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("hasDate", hasDate);
        map.put("datetime", DateUtil.now());

        if (StringUtils.isNotBlank(genConfig.getComments())) {
            map.put("comments", genConfig.getComments());
        } else {
            map.put("comments", tableEntity.getComments());
        }

        if (StringUtils.isNotBlank(genConfig.getAuthor())) {
            map.put("author", genConfig.getAuthor());
        } else {
            map.put("author", setting.getStr("author"));
        }

        if (StringUtils.isNotBlank(genConfig.getModuleName())) {
            map.put("moduleName", genConfig.getModuleName());
        } else {
            map.put("moduleName", setting.getStr("moduleName"));
        }

        if (StringUtils.isNotBlank(genConfig.getPackageName())) {
            map.put("package", genConfig.getPackageName());
            map.put("mainPath", genConfig.getPackageName());
        } else {
            map.put("package", setting.getStr("packageName"));
            map.put("mainPath", setting.getStr("packageName"));
        }
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, CharsetUtil.UTF_8);
            tpl.merge(context, sw);

            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(Objects
                        .requireNonNull(getFileName(template, tableEntity.getCaseClassName()
                                , map.get("package").toString(), map.get("moduleName").toString()))));
                IOUtil.write(zip, CharsetUtil.UTF_8, false, sw.toString());
                IOUtil.close(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new UtilException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    private static String columnToJava(String columnName) {
        return StringUtils.upperFirst(StringUtils.toCamelCase(columnName));
    }

    /**
     * 表名转换成Java类名
     */
    private static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replace(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取文件名
     */
    private static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = "y3tu-tool" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.contains(ENTITY_JAVA_VM)) {
            return packagePath + "entity" + File.separator + className + ".java";
        }

        if (template.contains(MAPPER_JAVA_VM)) {
            return packagePath + "mapper" + File.separator + className + "Mapper.java";
        }

        if (template.contains(SERVICE_JAVA_VM)) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains(SERVICE_IMPL_JAVA_VM)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains(CONTROLLER_JAVA_VM)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains(MAPPER_XML_VM)) {
            return "y3tu-tool" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + className + "Mapper.xml";
        }

        if (template.contains(MENU_SQL_VM)) {
            return className.toLowerCase() + "_menu.sql";
        }

        if (template.contains(INDEX_VUE_VM)) {
            return "y3tu-tool-ui" + File.separator + "src" + File.separator + "views" +
                    File.separator + moduleName + File.separator + className.toLowerCase() + File.separator + "index.vue";
        }

        if (template.contains(API_JS_VM)) {
            return "y3tu-tool-ui" + File.separator + "src" + File.separator + "api" + File.separator + className.toLowerCase() + ".js";
        }

        if (template.contains(CRUD_JS_VM)) {
            return "y3tu-tool-ui" + File.separator + "src" + File.separator + "const" +
                    File.separator + "crud" + File.separator + className.toLowerCase() + ".js";
        }

        return null;
    }


    /**
     * 程序手动调用生成代码
     * 读取配置文件里面数据库连接和配置
     *
     * @param setting       配置
     * @param dsFactoryEnum 指定数据源
     */
    public static void startGeneratorCode(Setting setting, DsFactoryEnum dsFactoryEnum) {

        try {
            if (setting == null) {
                setting = new Setting("config/codegen.properties");
            }

            //从配置文件中读取数据源
            DataSource dataSource = null;
            if (dsFactoryEnum == null) {
                dataSource = DSFactory.create(setting).getDataSource();
            } else {
                Class<DSFactory> clazz = dsFactoryEnum.getDsFactoryClass();
                dataSource = ReflectionUtil.newInstance(clazz, setting).getDataSource();
            }

            GenConfig genConfig = new GenConfig();
            genConfig.setModuleName(scanner("模块名"));
            genConfig.setTableName(scanner("表名"));

            String workingDir = Platforms.WORKING_DIR;
            String targetDir = workingDir + "/target/";

            File file = FileUtil.file(targetDir + "code.zip");
            OutputStream outputStream = FileUtil.asOututStream(file);
            ZipOutputStream zip = new ZipOutputStream(outputStream);

            //查询表信息
            Table table = MetaUtil.getTableMeta(dataSource, genConfig.getTableName());
            //表字段信息
            String[] columns = MetaUtil.getColumnNames(dataSource, table.getTableName());
            //生成代码
            GenUtils.generatorCode(genConfig, setting, table, columns, zip);
            IOUtil.close(zip);
            outputStream.flush();
            openDir(targetDir);
        } catch (Exception e) {
            throw new UtilException("代码生成失败!", e);
        }

    }

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = Console.scanner();
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        Console.log(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new UtilException("请输入正确的" + tip + "！");
    }

    /**
     * 打开指定文件夹
     *
     * @param outputDir
     */
    public static void openDir(String outputDir) {
        String osName = Platforms.OS_NAME;
        if (osName != null) {
            if (osName.contains("Mac")) {
                RuntimeUtil.exec("open " + outputDir);
            } else if (osName.contains("Windows")) {
                RuntimeUtil.exec("cmd /c start " + outputDir);
            } else {
                log.debug("文件输出目录:" + outputDir);
            }
        }
    }
}