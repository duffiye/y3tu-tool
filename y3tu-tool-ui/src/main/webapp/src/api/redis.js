import request from '@/plugin/axios'
import Qs from 'qs'

/**
 * 获取缓存统计列表
 */
export function cacheList(cacheName) {
    let data = {
        cacheName
    };

    return request({
        url: 'cacheList.json',
        method: 'post',
        data: Qs.stringify(data)
    })
}