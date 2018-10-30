package com.y3tu.tool.filesystem.provider;


import com.y3tu.tool.core.io.FilePathUtil;
import com.y3tu.tool.filesystem.FSProvider;
import com.y3tu.tool.http.HttpUtil;
import com.y3tu.tool.http.callback.FileCallBack;

/**
 * @author vakin
 */
public abstract class AbstractProvider implements FSProvider {

    protected String urlprefix;

    protected String bucketName;

    protected String getFullPath(String file) {
        if (file.startsWith(FilePathUtil.HTTP_PREFIX) || file.startsWith(FilePathUtil.HTTPS_PREFIX)) {
            return file;
        }
        return urlprefix + file;
    }


    @Override
    public String downloadAndSaveAs(String file, String localSaveDir) {
        //异步下载
         HttpUtil.downloadFile(getDownloadUrl(file), new FileCallBack(localSaveDir, null));
         return localSaveDir;
    }

}
