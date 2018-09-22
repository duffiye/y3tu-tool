package com.y3tu.tool.core.io;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import com.y3tu.tool.core.annotation.NotNull;
import com.y3tu.tool.core.collection.ArrayUtil;
import com.y3tu.tool.core.lang.Assert;
import com.y3tu.tool.core.lang.Platforms;
import com.y3tu.tool.core.text.CharUtil;
import com.y3tu.tool.core.text.CharsetUtil;
import com.y3tu.tool.core.text.StringUtils;
import com.y3tu.tool.core.util.URLUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;


/**
 * 文件工具类
 *
 * @author xiaoleilu
 */
public class FileUtil {
    /**
     * 类Unix路径分隔符
     */
    public static final char UNIX_SEPARATOR = CharUtil.SLASH;
    /**
     * Windows路径分隔符
     */
    public static final char WINDOWS_SEPARATOR = CharUtil.BACKSLASH;
    /**
     * Windows下文件名中的无效字符
     */
    private static Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");

    /**
     * Class文件扩展名
     */
    public static final String CLASS_EXT = ".class";
    /**
     * Jar文件扩展名
     */
    public static final String JAR_FILE_EXT = ".jar";
    /**
     * 在Jar中的路径jar的扩展名形式
     */
    public static final String JAR_PATH_EXT = ".jar!";
    /**
     * 当Path为文件形式时, path会加入一个表示文件的前缀
     */
    public static final String PATH_FILE_PRE = URLUtil.FILE_URL_PREFIX;

    /**
     * 是否为Windows环境
     *
     * @return 是否为Windows环境
     */
    public static boolean isWindows() {
        return WINDOWS_SEPARATOR == File.separatorChar;
    }

    /**
     * 创建File对象，自动识别相对或绝对路径，相对路径将自动从ClassPath下寻找
     *
     * @param path 文件路径
     * @return File
     */
    public static File file(String path) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return new File(FilePathUtil.getAbsolutePath(path));
    }

    /**
     * 创建File对象
     *
     * @param parent 父目录
     * @param path   文件路径
     * @return File
     */
    public static File file(String parent, String path) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return new File(parent, path);
    }

    /**
     * 通过多层目录参数创建文件
     *
     * @param directory 父目录
     * @param names     元素名（多层目录名）
     * @return the file 文件
     */
    public static File file(File directory, String... names) {
        Assert.notNull(directory, "directorydirectory must not be null");
        if (ArrayUtil.isEmpty(names)) {
            return directory;
        }
        File file = directory;
        for (String name : names) {
            if (null != name) {
                file = new File(file, name);
            }
        }
        return file;
    }

    /**
     * 通过多层目录创建文件
     * <p>
     * 元素名（多层目录名）
     *
     * @return the file 文件
     */
    public static File file(String... names) {
        if (ArrayUtil.isEmpty(names)) {
            return null;
        }
        File file = null;
        for (String name : names) {
            if (file == null) {
                file = new File(name);
            } else {
                file = new File(file, name);
            }
        }
        return file;
    }

    /**
     * 创建File对象
     *
     * @param uri 文件URI
     * @return File
     */
    public static File file(URI uri) {
        if (uri == null) {
            throw new NullPointerException("File uri is null!");
        }
        return new File(uri);
    }

    /**
     * 创建File对象
     *
     * @param url 文件URL
     * @return File
     */
    public static File file(URL url) {
        return new File(URLUtil.toURI(url));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param fullFilePath 文件的全路径，使用POSIX风格
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(String fullFilePath) throws IORuntimeException {
        if (fullFilePath == null) {
            return null;
        }
        return touch(file(fullFilePath));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(File file) throws IORuntimeException {
        if (null == file) {
            return null;
        }
        if (false == file.exists()) {
            mkParentDirs(file);
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
        return file;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     * @throws IORuntimeException IO异常
     */
    public static File touch(File parent, String path) throws IORuntimeException {
        return touch(file(parent, path));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     * @throws IORuntimeException IO异常
     */
    public static File touch(String parent, String path) throws IORuntimeException {
        return touch(file(parent, path));
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        final File parentFile = file.getParentFile();
        if (null != parentFile && false == parentFile.exists()) {
            parentFile.mkdirs();
        }
        return parentFile;
    }

    /**
     * 创建父文件夹，如果存在直接返回此文件夹
     *
     * @param path 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkParentDirs(String path) {
        if (path == null) {
            return null;
        }
        return mkParentDirs(file(path));
    }

    /**
     * 列出目录文件<br>
     * 给定的绝对路径不能是压缩包中的路径
     *
     * @param path 目录绝对路径或者相对路径
     * @return 文件列表（包含目录）
     */
    public static File[] ls(String path) {
        if (path == null) {
            return null;
        }

        path = FilePathUtil.getAbsolutePath(path);

        File file = new File(path);
        if (file.isDirectory()) {
            return file.listFiles();
        }
        throw new IORuntimeException(StringUtils.format("Path [{}] is not directory!", path));
    }

    /**
     * 文件是否为空<br>
     * 目录：里面没有文件时为空 文件：文件大小为0时为空
     *
     * @param file 文件
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isEmpty(File file) {
        if (null == file) {
            return true;
        }

        if (file.isDirectory()) {
            String[] subFiles = file.list();
            if (ArrayUtil.isEmpty(subFiles)) {
                return true;
            }
        } else if (file.isFile()) {
            return file.length() <= 0;
        }

        return false;
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供file为文件，直接返回过滤结果
     *
     * @param file       当前遍历文件或目录
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
     * @return 文件列表
     */
    public static List<File> loopFiles(File file, FileFilter fileFilter) {
        List<File> fileList = new ArrayList<File>();
        if (null == file) {
            return fileList;
        } else if (false == file.exists()) {
            return fileList;
        }

        if (file.isDirectory()) {
            final File[] subFiles = file.listFiles();
            if (ArrayUtil.isNotEmpty(subFiles)) {
                for (File tmp : subFiles) {
                    fileList.addAll(loopFiles(tmp, fileFilter));
                }
            }
        } else {
            if (null == fileFilter || fileFilter.accept(file)) {
                fileList.add(file);
            }
        }

        return fileList;
    }

    /**
     * 获得指定目录下所有文件<br>
     * 不会扫描子目录
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return 文件路径列表（如果是jar中的文件，则给定类似.jar!/xxx/xxx的路径）
     * @throws IORuntimeException IO异常
     */
    public static List<String> listFileNames(String path) throws IORuntimeException {
        if (path == null) {
            return null;
        }
        List<String> paths = new ArrayList<String>();

        int index = path.lastIndexOf(FileUtil.JAR_PATH_EXT);
        if (index == -1) {
            // 普通目录路径
            File[] files = ls(path);
            for (File file : files) {
                if (file.isFile()) {
                    paths.add(file.getName());
                }
            }
        } else {
            // jar文件
            path = FilePathUtil.getAbsolutePath(path);
            if (false == path.endsWith(String.valueOf(UNIX_SEPARATOR))) {
                path = path + UNIX_SEPARATOR;
            }
            // jar文件中的路径
            index = index + FileUtil.JAR_FILE_EXT.length();
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(path.substring(0, index));
                final String subPath = path.substring(index + 2);
                for (JarEntry entry : Collections.list(jarFile.entries())) {
                    final String name = entry.getName();
                    if (name.startsWith(subPath)) {
                        final String nameSuffix = StringUtils.removePrefix(name, subPath);
                        if (nameSuffix.contains(String.valueOf(UNIX_SEPARATOR)) == false) {
                            paths.add(nameSuffix);
                        }
                    }
                }
            } catch (IOException e) {
                throw new IORuntimeException(StringUtils.format("Can not read file path of [{}]", path), e);
            } finally {
                IOUtil.close(jarFile);
            }
        }
        return paths;
    }

    /**
     * 计算目录或文件的总大小<br>
     * 当给定对象为文件时，直接调用 {@link File#length()}<br>
     * 当给定对象为目录时，遍历目录下的所有文件和目录，递归计算其大小，求和返回
     *
     * @param file 目录或文件
     * @return 总大小，bytes长度
     */
    public static long size(File file) {
        Assert.notNull(file, "file argument is null !");
        if (false == file.exists()) {
            throw new IllegalArgumentException(StringUtils.format("File [{}] not exist !", file.getAbsolutePath()));
        }

        if (file.isDirectory()) {
            long size = 0L;
            File[] subFiles = file.listFiles();
            if (ArrayUtil.isEmpty(subFiles)) {
                // empty directory
                return 0L;
            }
            for (int i = 0; i < subFiles.length; i++) {
                size += size(subFiles[i]);
            }
            return size;
        } else {
            return file.length();
        }
    }

    /**
     * 删除文件或者文件夹<br>
     * 路径如果为相对路径，会转换为ClassPath路径！ 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param fullFileOrDirPath 文件或者目录的路径
     * @return 成功与否
     * @throws IORuntimeException IO异常
     */
    public static boolean del(String fullFileOrDirPath) throws IORuntimeException {
        return del(file(fullFileOrDirPath));
    }

    /**
     * 删除文件或者文件夹<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param file 文件对象
     * @return 成功与否
     * @throws IORuntimeException IO异常
     */
    public static boolean del(File file) throws IORuntimeException {
        if (file == null || false == file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            clean(file);
        }
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return true;
    }

    /**
     * 清空文件夹<br>
     * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param dirPath 文件夹路径
     * @return 成功与否
     * @throws IORuntimeException IO异常
     */
    public static boolean clean(String dirPath) throws IORuntimeException {
        return clean(file(dirPath));
    }

    /**
     * 清空文件夹<br>
     * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param directory 文件夹
     * @return 成功与否
     * @throws IORuntimeException IO异常
     */
    public static boolean clean(File directory) throws IORuntimeException {
        if (directory == null || directory.exists() == false || false == directory.isDirectory()) {
            return true;
        }

        final File[] files = directory.listFiles();
        for (File childFile : files) {
            boolean isOk = del(childFile);
            if (isOk == false) {
                // 删除一个出错则本次删除任务失败
                return false;
            }
        }
        return true;
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].suffix From com.jodd.io.FileUtil
     *
     * @param prefix    前缀，至少3个字符
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws IORuntimeException IO异常
     */
    public static File createTempFile(String prefix, String suffix, File dir, boolean isReCreat) throws IORuntimeException {
        int exceptionsCount = 0;
        while (true) {
            try {
                File file = File.createTempFile(prefix, suffix, dir).getCanonicalFile();
                if (isReCreat) {
                    file.delete();
                    file.createNewFile();
                }
                return file;
            } catch (IOException ioex) {
                // fixes java.io.WinNTFileSystem.createFileExclusively access denied
                if (++exceptionsCount >= 50) {
                    throw new IORuntimeException(ioex);
                }
            }
        }
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指File对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws IORuntimeException IO异常
     * @see Files#isSameFile(Path, Path)
     */
    public static boolean equals(File file1, File file2) throws IORuntimeException {
        Assert.notNull(file1);
        Assert.notNull(file2);
        if (false == file1.exists() || false == file2.exists()) {
            // 两个文件都不存在判断其路径是否相同
            if (false == file1.exists() && false == file2.exists() && FilePathUtil.pathEquals(file1, file2)) {
                return true;
            }
            // 对于一个存在一个不存在的情况，一定不相同
            return false;
        }
        try {
            return Files.isSameFile(file1.toPath(), file2.toPath());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 比较两个文件内容是否相同<br>
     * 首先比较长度，长度一致再比较内容<br>
     * 此方法来自Apache Commons io
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 两个文件内容一致返回true，否则false
     * @throws IORuntimeException IO异常
     */
    public static boolean contentEquals(File file1, File file2) throws IORuntimeException {
        return FileUtil.contentEquals(file1, file2);
    }

    /**
     * 比较两个文件内容是否相同<br>
     * 首先比较长度，长度一致再比较内容，比较内容采用按行读取，每行比较<br>
     * 此方法来自Apache Commons io
     *
     * @param file1   文件1
     * @param file2   文件2
     * @param charset 编码，null表示使用平台默认编码 两个文件内容一致返回true，否则false
     * @throws IOException IO异常
     */
    public static boolean contentEqualsIgnoreEOL(File file1, File file2, Charset charset) throws IOException {
        return FileUtils.contentEqualsIgnoreEOL(file1, file2, charset.name());
    }


    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     * 重命名有两种模式：<br>
     * 1、isRetainExt为true时，保留原扩展名：
     *
     * <pre>
     * FileUtil.rename(file, "aaa", true) xx/xx.png =》xx/aaa.png
     * </pre>
     * <p>
     * 2、isRetainExt为false时，不保留原扩展名，需要在newName中
     *
     * <pre>
     * FileUtil.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param file        被修改的文件
     * @param newName     新的文件名，包括扩展名
     * @param isRetainExt 是否保留原文件的扩展名，如果保留，则newName不需要加扩展名
     * @param isOverride  是否覆盖目标文件
     * @return 目标文件
     */
    public static File rename(File file, String newName, boolean isRetainExt, boolean isOverride) {
        if (isRetainExt) {
            newName = newName.concat(".").concat(FileUtil.getFileExtension(file));
        }
        final Path path = file.toPath();
        final CopyOption[] options = isOverride ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[]{};
        try {
            return Files.move(path, path.resolveSibling(newName), options).toFile();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }


    //////// 文件读写//////

    /**
     * 读取文件到byte[].
     *
     * @see {@link Files#readAllBytes}
     */
    public static byte[] toByteArray(final File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * 读取文件到String.
     */
    public static String toString(final File file) throws IOException {
        return com.google.common.io.Files.toString(file, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 读取文件的每行内容到List<String>.
     *
     * @see {@link Files#readAllLines}
     */
    public static List<String> toLines(final File file) throws IOException {
        return Files.readAllLines(file.toPath(), CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 简单写入String到File.
     */
    public static void write(final CharSequence data, final File file) throws IOException {
        Validate.notNull(file);
        Validate.notNull(data);

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CharsetUtil.CHARSET_UTF_8)) {
            writer.append(data);
        }
    }

    /**
     * 追加String到File.
     */
    public static void append(final CharSequence data, final File file) throws IOException {
        Validate.notNull(file);
        Validate.notNull(data);

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CharsetUtil.CHARSET_UTF_8,
                StandardOpenOption.APPEND)) {
            writer.append(data);
        }
    }

    /**
     * 获取输入流
     *
     * @param fileName 文件全路径
     * @return InputStream对象
     * @see {@link Files#newInputStream}
     */
    public static InputStream asInputStream(String fileName) throws IOException {
        return asInputStream(getPath(fileName));
    }

    /**
     * 获取输入流
     *
     * @param file 文件
     * @return InputStream对象
     * @see {@link Files#newInputStream}
     */
    public static InputStream asInputStream(File file) throws IOException {
        Validate.notNull(file, "file is null");
        return asInputStream(file.toPath());
    }

    /**
     * 获取输入流
     *
     * @param path 文件路径
     * @return InputStream对象
     * @see {@link Files#newInputStream}
     */
    public static InputStream asInputStream(Path path) throws IOException {
        Validate.notNull(path, "path is null");
        return Files.newInputStream(path);
    }

    /**
     * 获取输出流
     *
     * @param fileName 文件全路径
     * @return OutputStream对象
     * @see {@link Files#newOutputStream}
     */
    public static OutputStream asOututStream(String fileName) throws IOException {
        return asOututStream(getPath(fileName));
    }

    /**
     * 获取输出流
     *
     * @param file 文件
     * @return OutputStream对象
     * @see {@link Files#newOutputStream}
     */
    public static OutputStream asOututStream(File file) throws IOException {
        Validate.notNull(file, "file is null");
        return asOututStream(file.toPath());
    }

    /**
     * 获取输出流
     *
     * @param path 文件路径
     * @return OutputStream对象
     * @see {@link Files#newOutputStream}
     */
    public static OutputStream asOututStream(Path path) throws IOException {
        Validate.notNull(path, "path is null");
        return Files.newOutputStream(path);
    }

    /**
     * 获得一个文件读取器
     *
     * @param fileName 文件全路径
     * @param charset  字符集
     * @return BufferedReader对象
     * @see {@link Files#newBufferedReader}
     */
    public static BufferedReader asBufferedReader(String fileName, Charset charset) throws IOException {
        Validate.notBlank(fileName, "filename is blank");
        return asBufferedReader(getPath(fileName), charset);
    }

    /**
     * 获得一个文件读取器
     *
     * @param path    文件Path
     * @param charset 字符集
     * @return BufferedReader对象
     * @throws IOException
     * @see {@link Files#newBufferedReader}
     */
    public static BufferedReader asBufferedReader(Path path, Charset charset) throws IOException {
        Validate.notNull(path, "path is null");
        if (charset == null) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        return Files.newBufferedReader(path, charset);
    }

    /**
     * 获取文件写入器
     *
     * @param fileName 文件全路径
     * @param charset  字符集
     * @return BufferedWriter
     * @see {@link Files#newBufferedWriter}
     */
    public static BufferedWriter asBufferedWriter(String fileName, Charset charset) throws IOException {
        Validate.notBlank(fileName, "filename is blank");
        if (charset == null) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        return Files.newBufferedWriter(getPath(fileName), charset);
    }

    /**
     * 获取文件写入器
     *
     * @param path    文件路径
     * @param charset 字符集
     * @return BufferedWriter
     * @see {@link Files#newBufferedWriter}
     */
    public static BufferedWriter asBufferedWriter(Path path, Charset charset) throws IOException {
        Validate.notNull(path, "path is null");
        if (charset == null) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        return Files.newBufferedWriter(path, charset);
    }

    ///// 文件操作 /////

    /**
     * 复制文件或目录, not following links.
     *
     * @param from 如果为null，或者是不存在的文件或目录，抛出异常.
     * @param to   如果为null，或者from是目录而to是已存在文件，或相反
     */
    public static void copy(@NotNull File from, @NotNull File to) throws IOException {
        Validate.notNull(from);
        Validate.notNull(to);

        copy(from.toPath(), to.toPath());
    }

    /**
     * 复制文件或目录, not following links.
     *
     * @param from 如果为null，或者是不存在的文件或目录，抛出异常.
     * @param to   如果为null，或者from是目录而to是已存在文件，或相反
     */
    public static void copy(@NotNull Path from, @NotNull Path to) throws IOException {
        Validate.notNull(from);
        Validate.notNull(to);

        if (Files.isDirectory(from)) {
            copyDir(from, to);
        } else {
            copyFile(from, to);
        }
    }

    /**
     * 文件复制.
     *
     * @param from 如果为null，或文件不存在或者是目录，，抛出异常
     * @param to   如果to为null，或文件存在但是一个目录，抛出异常
     * @see {@link Files#copy}
     */
    public static void copyFile(@NotNull File from, @NotNull File to) throws IOException {
        Validate.notNull(from);
        Validate.notNull(to);
        copyFile(from.toPath(), to.toPath());
    }

    /**
     * 文件复制. @see {@link Files#copy}
     *
     * @param from 如果为null，或文件不存在或者是目录，，抛出异常
     * @param to   如果to为null，或文件存在但是一个目录，抛出异常
     */
    public static void copyFile(@NotNull Path from, @NotNull Path to) throws IOException {
        Validate.isTrue(Files.exists(from), "%s is not exist or not a file", from);
        Validate.notNull(to);
        Validate.isTrue(!FileUtil.isDirExists(to), "%s is exist but it is a dir", to);
        Files.copy(from, to);
    }

    /**
     * 复制目录
     */
    public static void copyDir(@NotNull File from, @NotNull File to) throws IOException {
        Validate.isTrue(isDirExists(from), "%s is not exist or not a dir", from);
        Validate.notNull(to);

        copyDir(from.toPath(), to.toPath());
    }

    /**
     * 复制目录
     */
    public static void copyDir(@NotNull Path from, @NotNull Path to) throws IOException {
        Validate.isTrue(isDirExists(from), "%s is not exist or not a dir", from);
        Validate.notNull(to);
        makesureDirExists(to);

        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(from)) {
            for (Path path : dirStream) {
                copy(path, to.resolve(path.getFileName()));
            }
        }
    }

    /**
     * 文件移动/重命名.
     *
     * @see {@link Files#move}
     */
    public static void moveFile(@NotNull File from, @NotNull File to) throws IOException {
        Validate.notNull(from);
        Validate.notNull(to);

        moveFile(from.toPath(), to.toPath());
    }

    /**
     * 文件移动/重命名.
     *
     * @see {@link Files#move}
     */
    public static void moveFile(@NotNull Path from, @NotNull Path to) throws IOException {
        Validate.isTrue(isFileExists(from), "%s is not exist or not a file", from);
        Validate.notNull(to);
        Validate.isTrue(!isDirExists(to), "%s is  exist but it is a dir", to);

        Files.move(from, to);
    }

    /**
     * 目录移动/重命名
     */
    public static void moveDir(@NotNull File from, @NotNull File to) throws IOException {
        Validate.isTrue(isDirExists(from), "%s is not exist or not a dir", from);
        Validate.notNull(to);
        Validate.isTrue(!isFileExists(to), "%s is exist but it is a file", to);

        final boolean rename = from.renameTo(to);
        if (!rename) {
            if (to.getCanonicalPath().startsWith(from.getCanonicalPath() + File.separator)) {
                throw new IOException("Cannot move directory: " + from + " to a subdirectory of itself: " + to);
            }
            copyDir(from, to);
            del(from);
            if (from.exists()) {
                throw new IOException("Failed to delete original directory '" + from + "' after copy to '" + to + '\'');
            }
        }
    }


    /**
     * 判断目录是否存在, from Jodd
     */
    public static boolean isDirExists(String dirPath) {
        if (dirPath == null) {
            return false;
        }
        return isDirExists(getPath(dirPath));
    }

    public static boolean isDirExists(Path dirPath) {
        return dirPath != null && Files.exists(dirPath) && Files.isDirectory(dirPath);
    }

    /**
     * 判断目录是否存在, from Jodd
     */
    public static boolean isDirExists(File dir) {
        if (dir == null) {
            return false;
        }
        return isDirExists(dir.toPath());
    }

    /**
     * 确保目录存在, 如不存在则创建
     */
    public static void makesureDirExists(String dirPath) throws IOException {
        makesureDirExists(getPath(dirPath));
    }

    /**
     * 确保目录存在, 如不存在则创建
     */
    public static void makesureDirExists(File file) throws IOException {
        Validate.notNull(file);
        makesureDirExists(file.toPath());
    }

    /**
     * 确保目录存在, 如不存在则创建.
     *
     * @see {@link Files#createDirectories}
     */
    public static void makesureDirExists(Path dirPath) throws IOException {
        Validate.notNull(dirPath);
        Files.createDirectories(dirPath);
    }

    /**
     * 确保父目录及其父目录直到根目录都已经创建.
     */
    public static void makesureParentDirExists(File file) throws IOException {
        Validate.notNull(file);
        makesureDirExists(file.getParentFile());
    }

    /**
     * 判断文件是否存在, from Jodd.
     *
     * @see {@link Files#exists}
     * @see {@link Files#isRegularFile}
     */
    public static boolean isFileExists(String fileName) {
        if (fileName == null) {
            return false;
        }
        return isFileExists(getPath(fileName));
    }

    /**
     * 判断文件是否存在, from Jodd.
     *
     * @see {@link Files#exists}
     * @see {@link Files#isRegularFile}
     */
    public static boolean isFileExists(File file) {
        if (file == null) {
            return false;
        }
        return isFileExists(file.toPath());
    }

    /**
     * 判断文件是否存在, from Jodd.
     *
     * @see {@link Files#exists}
     * @see {@link Files#isRegularFile}
     */
    public static boolean isFileExists(Path path) {
        if (path == null) {
            return false;
        }
        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * 在临时目录创建临时目录，命名为${毫秒级时间戳}-${同一毫秒内的随机数}.
     *
     * @see {@link Files#createTempDirectory}
     */
    public static Path createTempDir() throws IOException {
        return Files.createTempDirectory(System.currentTimeMillis() + "-");
    }


    private static Path getPath(String filePath) {
        return Paths.get(filePath);
    }

    /**
     * 获取文件名(不包含路径)
     */
    public static String getFileName(@NotNull String fullName) {
        Validate.notEmpty(fullName);
        int last = fullName.lastIndexOf(Platforms.FILE_PATH_SEPARATOR_CHAR);
        return fullName.substring(last + 1);
    }

    /**
     * 获取文件名的扩展名部分(不包含.)
     *
     * @see {@link com.google.common.io.Files#getFileExtension}
     */
    public static String getFileExtension(File file) {
        return com.google.common.io.Files.getFileExtension(file.getName());
    }

    /**
     * 获取文件名的扩展名部分(不包含.)
     *
     * @see {@link com.google.common.io.Files#getFileExtension}
     */
    public static String getFileExtension(String fullName) {
        return com.google.common.io.Files.getFileExtension(fullName);
    }
}
