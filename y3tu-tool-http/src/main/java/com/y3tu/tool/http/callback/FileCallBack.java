package com.y3tu.tool.http.callback;

import com.y3tu.tool.core.exception.DefaultError;
import com.y3tu.tool.core.io.FileUtil;
import com.y3tu.tool.core.text.StringUtils;
import com.y3tu.tool.http.HttpException;
import okhttp3.Call;
import okhttp3.Response;

import java.io.*;

/**
 * 文件处理回调
 *
 * @author y3tu
 */
public class FileCallBack extends CallBack<File> {
    /**
     * 目标文件夹地址
     */
    private String destFileDir;
    /**
     * 目标文件名
     */
    private String destFileName;

    /**
     * @param destFileDir:文件目录
     * @param destFileName：文件名
     */
    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        throw new HttpException("文件下载失败!", e, DefaultError.HTTP_ERROR);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        onParseResponse(call, response);
    }

    @Override
    public File onParseResponse(Call call, Response response) {
        InputStream is = null;
        byte[] buf = new byte[1024 * 8];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();

            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (StringUtils.isEmpty(destFileName)) {
                String disposition = response.header("Content-Disposition");
                if (disposition != null) {
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        destFileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    destFileName = destFileDir.substring(destFileDir.lastIndexOf("/") + 1, destFileDir.length());
                }
            }

            File file = FileUtil.file(destFileDir + destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
            }
            fos.flush();

            return file;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.body().close();
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }

        }
        return null;
    }
}
