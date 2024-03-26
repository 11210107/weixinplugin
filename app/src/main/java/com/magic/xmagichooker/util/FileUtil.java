package com.magic.xmagichooker.util;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        LogUtils.d(TAG, "copyFile oldPath:" + oldPath + " newPath:" + newPath);
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.isDirectory()) {
                //copy路径
                File destDir = new File(newPath);
                destDir.mkdirs();
                File[] files = oldFile.listFiles();
                if (files != null) {
                    for (File childFile : files) {
                        copyFile(childFile.getAbsolutePath(), destDir + File.separator + childFile.getName());
                    }
                }
            } else if (oldFile.exists()) {
                LogUtils.d(TAG, "copyFile exist:");
                //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                fs.flush();
                fs.close();
                inStream.close();
                LogUtils.d(TAG, "copyFile over");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "copyFile error e:" + e.getMessage());
        }
    }

    public static void deleteFile(String filePath) {
        LogUtils.d(TAG, "deleteFile: " + filePath);
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            deleteFile(file);
        }
    }

    public static void deleteFile(File deleteFile) {
        LogUtils.d(TAG, "deleteFile: " + deleteFile);
        if (deleteFile != null) {
            String filePath = deleteFile.getAbsolutePath();
            File file = new File(filePath);
            if (file.isDirectory()) {
                LogUtils.d(TAG, "deleteFile isDirectory");
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        deleteFile(f.getAbsolutePath());
                    }
                }
                file.delete();
            } else if (file.exists()) {
                LogUtils.d(TAG, "deleteFile exists");
                boolean result = file.delete();
                LogUtils.d(TAG, "deleteFile result:" + result);
            }
        }
    }

    public static boolean mkdirsForFile(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return false;
        }
        int lastIndex = dirPath.lastIndexOf("/");
        if (lastIndex == -1) {
            return false;
        }
        File file = new File(dirPath);
        if (file.exists()) {
            return true;
        }
        String path = dirPath.substring(0, lastIndex);
        return new File(path).mkdirs();
    }

    /**
     * @return
     */
    public static String checkFreeAvailable() {
        LogUtils.d(TAG,"checkFreeAvailable");
        File dataDirectory = Environment.getDataDirectory();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        //转化 为 M
        long freeSpaceData = dataDirectory.getFreeSpace()/(1024*1024);
        long freeSpaceSdcard = externalStorageDirectory.getFreeSpace()/(1024*1024);

        return "freeSpaceData:"+freeSpaceData+"M freeSpaceSdcard:"+freeSpaceSdcard+"M";
    }

    /**
     * 查找最大的文件
     * @param filename
     * @param imageFolder
     * @return
     */
    @Nullable
    public static File getTargetFile(String filename, File imageFolder) {
        File targetFile = null;
        for (File file : imageFolder.listFiles()) {
            if (file.getName().startsWith(filename)) {
                if (targetFile == null) {
                    targetFile = file;
                } else {
                    if (file.length() > targetFile.length()) {
                        targetFile = file;
                    }
                }
            } else if (file.getName().startsWith("th_" + filename)) {
                if (targetFile == null) {
                    targetFile = file;
                } else {
                    if (file.length() > targetFile.length()) {
                        targetFile = file;
                    }
                }
            }
        }
        return targetFile;
    }
}