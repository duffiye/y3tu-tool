package com.y3tu.tool.web.lock;

/**
 * @author sunyujia@aliyun.com https://github.com/yujiasun/Distributed-Kit
 * @date 2016/2/23
 */
public interface Callback {

    /**
     * 成功获取锁后执行方法
     *
     * @return
     * @throws InterruptedException
     */
    public Object onGetLock() throws InterruptedException;

    /**
     * 获取锁超时回调
     *
     * @return
     * @throws InterruptedException
     */
    public Object onTimeout() throws InterruptedException;
}
