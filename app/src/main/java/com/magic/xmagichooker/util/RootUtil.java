package com.magic.xmagichooker.util;

import android.text.TextUtils;
import android.util.Log;


import com.magic.xmagichooker.Define;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cc.sdkutil.controller.util.LogUtil;

/**
 * @author zoubangyue on 2017/10/30.
 */
public class RootUtil {
    private static final String TAG = "RootUtil";
    /**
     * COMMANDS
     */
    private static final String CMD_CHMOD777 = "chmod 777 ";
    private static final String CMD_CHMOD_R_777 = "chmod 777 -R";
    private static final String CMD_SILENCE_INSTALL = "pm install -r";
    private static final String CMD_SILENCE_UNINSTALL = "pm uninstall -k --user 0";
    private static final String CMD_SILENCE_ENABLE = "pm enable ";
    private static final String CMD_REBOOT = "reboot";
    private static final String TOP_ACTIVITY = "dumpsys activity top | grep ACTIVITY";
    private static final String CMD_REMOUNT_READ_WRITE = "mount -o rw,remount";
    private static final String CMD_CP_FILE = "cp -r ";
    private static final String CMD_MOVE_FILE = "mv ";
    private static final String CMD_RM_FILE = "rm -rf";
    private static final String CMD_MOUNT_GREP = "mount | grep";
    private static final String CMD_STAT = "stat";

    /**
     * CONST
     */
    public static final String MOUNT_RESULT_R = "ro";
    public static final String MOUNT_RESULT_RW = "rw";

    /**
     * cmd output result
     */
    private List<String> result = new ArrayList<>(8);

    public RootUtil(){

    }

    public List<String> getResult(){
        return result;
    }
    /**
     * 判断当前手机是否有ROOT权限
     * @return
     */
    public static boolean isRoot(){
        boolean bool = false;

        try{
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
                bool = false;
            } else {
                bool = true;
            }
        } catch (Throwable t) {
            LogUtil.INSTANCE.e(TAG,"isRoot e: "+t.getMessage());
        }
        return bool;
    }

    /**
     *
     * @param command
     * @return
     */
    private boolean rootCommand(String command){
        Process process = null;
        DataOutputStream out = null;
        BufferedReader in = null;
        BufferedReader einr = null;
        try {
            process = Runtime.getRuntime().exec("su");
            out = new DataOutputStream(process.getOutputStream());
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            einr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            out.writeBytes(command + "\n");
            out.flush();
            out.writeBytes("exit\n");
            out.flush();
            String line;
            result.clear();
            while ((line = in.readLine()) != null) {
                result.add(line);
            }
            if (process.waitFor() != 0) {
                LogUtil.INSTANCE.d(TAG,"exit value = " + process.exitValue());
            }
            LogUtil.INSTANCE.d(TAG, "Root SUC ::" +"\nresult: "+result);
        } catch (Throwable e) {
            LogUtil.INSTANCE.d(TAG, "ROOT ERR" + e.getMessage());
            return false;
        } finally {
            try {
                if(in !=null){
                    in.close();
                }
                if(einr != null){
                    einr.close();
                }
                if(process !=null){
                    process.destroy();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * 在使用时注意路径的写法
     *
     * @param command 命令
     * @param path    路径
     * @return 成功失败
     */
    private boolean rootCommand(String command, String path) {
        if("/data/data".equals(path.trim())
                || "/data/data/".equals(path.trim())
                || ("/data/user/"+ Define.UID).equals(path.trim())
                || ("/data/user/"+Define.UID+"/").equals(path.trim())
                ){
            LogUtil.INSTANCE.d(TAG, "you can't root all /data/data path");
            return false;
        }
        return rootCommand(command+" "+path);
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * 在使用时注意路径的写法
     *
     * @param path    路径
     * @return 成功失败
     */
    public boolean chmod777(String path) {
        if("/data/data".equals(path.trim())
                || "/data/data/".equals(path.trim())
                || ("/data/user/"+ Define.UID).equals(path.trim())
                || ("/data/user/"+Define.UID+"/").equals(path.trim())
                ){
            LogUtil.INSTANCE.d(TAG, "you can't root all /data/data path");
            return false;
        }
        File f = new File(path);

        String commondPre = f.isDirectory() ? CMD_CHMOD_R_777:CMD_CHMOD777;
        return rootCommand(commondPre,path);
    }

    /**
     * 静默安装
     * @param path
     */
    public boolean silenceInstall(String path){
        LogUtil.INSTANCE.d(TAG,"silenceInstall path: " + path);
        if(TextUtils.isEmpty(path)){
            LogUtil.INSTANCE.d(TAG,"silenceInstall path is null");
            return false;
        }
        try {
            return rootCommand(CMD_SILENCE_INSTALL,path);
        }catch (Throwable t){
            LogUtil.INSTANCE.e(TAG,"silenceInstall error : "+t.getMessage());
        }
        return false;
    }

    public boolean silenceUninstall(String packageName){
        LogUtil.INSTANCE.d(TAG,"silenceUninstall");
        if(TextUtils.isEmpty(packageName)){
            LogUtil.INSTANCE.d(TAG,"silenceUninstall packageName is null");
            return false;
        }
        return rootCommand(CMD_SILENCE_UNINSTALL,packageName);
    }

    public boolean silenceEnableApp(String packageName){
        LogUtil.INSTANCE.d(TAG,"silenceEnableApp");
        if(TextUtils.isEmpty(packageName)){
            LogUtil.INSTANCE.d(TAG,"silenceEnableApp packageName is null");
            return false;
        }
        return rootCommand(CMD_SILENCE_ENABLE,packageName);
    }

    /**
     * 重启手机
     * @return
     */
    public boolean reboot(){
        LogUtil.INSTANCE.d(TAG,"reboot ");
        boolean reboot = rootCommand(CMD_REBOOT);
        LogUtil.INSTANCE.d(TAG,"reboot res:"+reboot);
        return reboot;
    }

    /**
     * 获取手机当前页面信息
     * @return
     */
    public boolean getTopActivity(){
        LogUtil.INSTANCE.d(TAG,"getTopActivity ");
        boolean reboot = rootCommand(TOP_ACTIVITY);
        LogUtil.INSTANCE.d(TAG,"getTopActivity res:"+reboot);
        return reboot;
    }

    /**
     * 查看某挂载点在文件系统中的挂载方式 rw 读写 ro 只读
     * @return
     */
    public String mountSystem(){
        LogUtil.INSTANCE.d(TAG,"mount path");
        if(rootCommand(CMD_MOUNT_GREP,"/system")){
            String s = result.get(0);
            LogUtil.INSTANCE.d(TAG,"1:"+s);
            if(!TextUtils.isEmpty(s)){
                int index = s.indexOf("(");
                LogUtil.INSTANCE.d(TAG,"2:"+index);
                if(index != -1){
                    String res = s.substring(index + 1, index + 3);
                    LogUtil.INSTANCE.d(TAG,"mount res:"+res);
                    return res;
                }
            }
        }
        LogUtil.INSTANCE.e(TAG,"mountSystem error!");
        return "";
    }

    /**
     * 修改文件系统中/system 目录挂载方式为可读写
     * @return
     */
    public boolean remountSystemRW(){
        return rootCommand(CMD_REMOUNT_READ_WRITE,"/system");
    }

    /**
     * 复制文件
     * @param fromPath
     * @param toPath
     * @return
     */
    public boolean copy(String fromPath,String toPath){
        LogUtil.INSTANCE.d(TAG,"copy from:"+fromPath+" to:"+toPath);
        if(TextUtils.isEmpty(fromPath)){
            LogUtil.INSTANCE.d(TAG,"fromPath isNull");
            return false;
        }
        if(TextUtils.isEmpty(toPath)){
            LogUtil.INSTANCE.d(TAG,"toPath isNull");
            return false;
        }
        return rootCommand(CMD_CP_FILE+fromPath+" "+toPath);
    }

    /**
     * 移动文件
     * @param fromPath
     * @param toPath
     * @return
     */
    public boolean move(String fromPath,String toPath){
        LogUtil.INSTANCE.d(TAG,"move from:"+fromPath+" to:"+toPath);
        if(TextUtils.isEmpty(fromPath)){
            LogUtil.INSTANCE.d(TAG,"fromPath isNull");
            return false;
        }
        if(TextUtils.isEmpty(toPath)){
            LogUtil.INSTANCE.d(TAG,"toPath isNull");
            return false;
        }
        return rootCommand(CMD_MOVE_FILE+fromPath+" "+toPath);
    }

    /**
     * 移除文件
     * @param path
     * @return
     */
    public boolean remove(String path){
        LogUtil.INSTANCE.d(TAG,"remove");
        if(TextUtils.isEmpty(path)){
            LogUtil.INSTANCE.d(TAG,"path isNull");
            return false;
        }
        return rootCommand(CMD_RM_FILE,path);
    }

    /**
     * 获取文件访问时间 linux 文件系统中没有文件创建时间 只有文件访问时间、修改时间、状态修改时间
     * @return 文件访问时间
     */
    public String getFileAccessTime(String path){
        LogUtil.INSTANCE.d(TAG,"getFileCreateTime path: "+path);
        if(TextUtils.isEmpty(path)){
            return "";
        }
        if(!rootCommand(CMD_STAT, path) || result.size()<=0){
            LogUtil.INSTANCE.d(TAG,"getFileCreateTime failed");
            return "";
        }
        LogUtil.INSTANCE.d(TAG,"result.size: "+result.size());
        if(result.size()==7){
            LogUtil.INSTANCE.d(TAG,"AccessTime : "+result.get(4));
            return result.get(4);
        }
        return "";
    }

    /**
     * 获取文件修改时间 linux 文件系统中没有文件创建时间 只有文件访问时间、修改时间、状态修改时间
     * @return 文件变化时间
     */
    public String getFileModifyTime(String path){
        LogUtil.INSTANCE.d(TAG,"getFileCreateTime path: "+path);
        if(TextUtils.isEmpty(path)){
            return "";
        }
        if(!rootCommand(CMD_STAT, path) || result.size()<=0){
            LogUtil.INSTANCE.d(TAG,"getFileCreateTime failed");
            return "";
        }
        LogUtil.INSTANCE.d(TAG,"result.size: "+result.size());
        if(result.size()==7){
            LogUtil.INSTANCE.d(TAG,"AccessTime : "+result.get(5));
            return result.get(5);
        }
        return "";
    }

    /**
     * 获取文件状态变化时间 linux 文件系统中没有文件创建时间 只有文件访问时间、修改时间、状态修改时间
     * @return 文件状态变化时间
     */
    public String getFileChangeTime(String path){
        LogUtil.INSTANCE.d(TAG,"getFileCreateTime path: "+path);
        if(TextUtils.isEmpty(path)){
            return "";
        }
        if(!rootCommand(CMD_STAT, path) || result.size()<=0){
            LogUtil.INSTANCE.d(TAG,"getFileCreateTime failed");
            return "";
        }
        LogUtil.INSTANCE.d(TAG,"result.size: "+result.size());
        if(result.size()==7){
            LogUtil.INSTANCE.d(TAG,"AccessTime : "+result.get(6));
            return result.get(6);
        }
        return "";
    }

    public static String[] getRootFileContent(String strPath) {
        Process process = null;
        DataOutputStream os = null;
        String allList = "";
        String strItems[] = null;
        try {
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);

            os.writeBytes("ls " + strPath + "\n");
            os.writeBytes("exit\n");

            String strTmp = "";
            while ((strTmp = bufferedReader.readLine()) != null) {
                allList += strTmp + "<";
            }
            strItems = allList.split("<");

            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return strItems;
    }

    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
