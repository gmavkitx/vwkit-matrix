package matrix.module.common.helper.files;

import matrix.module.common.exception.ServiceException;
import matrix.module.common.utils.FolderUtil;
import matrix.module.common.utils.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * zip工具
 *
 * @author wangcheng
 */
public class ZipHelper {

    public static ZipHelper getInstance() {
        return new ZipHelper();
    }

    public ZipHelper() {
    }

    /**
     * zip压缩
     *
     * @param sourceFilePath  压缩文件目录或文件
     * @param destZipFilePath 压缩文件路径
     */
    public void doCompress(String sourceFilePath, String destZipFilePath) {
        if (destZipFilePath == null || "".equals(destZipFilePath)
                || sourceFilePath == null || "".equals(sourceFilePath)) {
            throw new IllegalArgumentException("doCompress参数异常");
        }
        File fileDir = new File(sourceFilePath);
        List<File> files = new ArrayList<>();
        this.getFileList(fileDir, files);
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(new File(destZipFilePath));
            zos = new ZipOutputStream(fos);
            for (File file : files) {
                if (!file.isDirectory()) {
                    ZipEntry entry = new ZipEntry(file.getAbsolutePath().replace(sourceFilePath, ""));
                    zos.putNextEntry(entry);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        StreamUtil.streamWriteStream(fis, zos);
                    } finally {
                        StreamUtil.closeStream(fis);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            StreamUtil.closeStream(zos);
            StreamUtil.closeStream(fos);
        }
    }

    /**
     * 解压缩
     *
     * @param sourceZipFilePath 压缩文件路径
     * @param destFilePath      解压缩文件路径
     */
    @SuppressWarnings("all")
    public void doUnCompress(String sourceZipFilePath, String destFilePath) {
        if (sourceZipFilePath == null || "".equals(sourceZipFilePath)
                || destFilePath == null || "".equals(destFilePath)) {
            throw new IllegalArgumentException("doUnCompress参数异常");
        }
        File zipFile = new File(sourceZipFilePath);
        if (zipFile.exists()) {
            String fileName = zipFile.getName();
            fileName = fileName.substring(0, fileName.indexOf("."));
            destFilePath = destFilePath + File.separator + fileName;
        }
        File unzipFileDir = new File(destFilePath);
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
            FolderUtil.mkdirs(unzipFileDir.getAbsolutePath());
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryFilePath = destFilePath + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    int index = entryFilePath.lastIndexOf(File.separator);
                    String entryDirPath = "";
                    if (index != -1) {
                        entryDirPath = entryFilePath.substring(0, index);
                    }
                    File entryDir = new File(entryDirPath);
                    if (!entryDir.exists() || !entryDir.isDirectory()) {
                        FolderUtil.mkdirs(entryDir.getAbsolutePath());
                    }
                    File entryFile = new File(entryFilePath);
                    if (entryFile.exists()) {
                        new SecurityManager().checkDelete(entryFilePath);
                        entryFile.delete();
                    }
                    FileOutputStream fos = null;
                    InputStream is = null;
                    try {
                        fos = new FileOutputStream(entryFile);
                        is = zip.getInputStream(entry);
                        StreamUtil.streamWriteStream(is, fos);
                    } finally {
                        StreamUtil.closeStream(is);
                        StreamUtil.closeStream(fos);
                    }
                } else {
                    File entryFile = new File(entryFilePath);
                    if (entryFile.exists()) {
                        SecurityManager securityManager = new SecurityManager();
                        securityManager.checkDelete(entryFilePath);
                        entryFile.delete();
                    }
                    FolderUtil.mkdirs(entryFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            StreamUtil.closeStream(zip);
        }
    }

    private void getFileList(File fileDir, List<File> files) {
        if (fileDir != null && Objects.requireNonNull(fileDir.list()).length > 0) {
            File[] listFiles = fileDir.listFiles();
            assert listFiles != null;
            for (File file : listFiles) {
                files.add(file);
                if (file.isDirectory()) {
                    this.getFileList(file, files);
                }
            }
        }
    }
}
