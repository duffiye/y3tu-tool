package com.y3tu.tool.web.base.controller;


import com.y3tu.tool.core.collection.CollectionUtil;
import com.y3tu.tool.web.annotation.MethodMapping;
import com.y3tu.tool.web.base.entity.BaseEntity;
import com.y3tu.tool.web.base.pojo.PageInfo;
import com.y3tu.tool.core.pojo.R;
import com.y3tu.tool.web.base.service.BaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller基类
 *
 * @author y3tu
 */
public abstract class BaseController<T extends BaseService, M extends BaseEntity> {

    @Autowired
    public T service;

    /**
     * 分页查询
     *
     * @param params 查询参数 包含分页信息和查询条件
     *               前台传入参数params封装规则
     *               page：当前页
     *               pageSize：每页条数
     *               ascs:升序排列的字段 字符串数组
     *               descs:降序排列的字段 字符串数组
     *               查询条件的key要和mapper.xml文件里面的key保持一致
     * @return
     */
    @MethodMapping
    @ApiOperation(value = "分页查询", notes = "分页查询", httpMethod = "GET")
    public R<PageInfo<T>> getByPage(@RequestParam Map<String, Object> params) {
        PageInfo<T> pageInfo = service.queryPage(PageInfo.mapToPageInfo(params), params);
        return new R(pageInfo);
    }

    /**
     * 获取全部数据
     *
     * @return
     */
    @MethodMapping
    @ApiOperation(value = "查询所有信息", notes = "查询所有信息", httpMethod = "GET")
    public R<List<T>> getAll() {
        return new R(service.list(null));
    }

    /**
     * 通过id获取
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "主键查询", notes = "主键查询", httpMethod = "GET")
    @MethodMapping(value = "/get/{id}")
    public R get(@PathVariable String id) {
        return R.success(service.getById(id));
    }

    /**
     * 保存数据
     *
     * @param entity 保存的数据
     * @return
     */
    @ApiOperation(value = "保存", httpMethod = "POST")
    @MethodMapping(method = RequestMethod.POST)
    public R save(@RequestBody M entity) {
        service.save(entity);
        return R.success("保存成功!");
    }

    /**
     * 更新数据
     *
     * @param entity 更新的数据
     * @return
     */
    @ApiOperation(value = "更新", httpMethod = "PUT")
    @MethodMapping(method = RequestMethod.PUT)
    public R update(@RequestBody M entity) {
        service.updateById(entity);
        return R.success("更新成功!");
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @ApiOperation(value = "删除", httpMethod = "DELETE")
    @MethodMapping(value = "/delById/{id}", method = RequestMethod.DELETE)
    public R delById(@PathVariable String id) {
        service.removeById(id);
        return R.success("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids 主键集合
     * @return
     */
    @ApiOperation(value = "删除", httpMethod = "DELETE")
    @MethodMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    public R delByIds(@PathVariable String[] ids) {
        service.removeByIds(CollectionUtil.toList(ids));
        return R.success("删除成功!");
    }

    /**
     * 重定向至地址 url
     *
     * @param url 请求地址
     * @return
     */
    protected String redirectTo(String url) {
        StringBuffer rto = new StringBuffer("redirect:");
        rto.append(url);
        return rto.toString();
    }


}
