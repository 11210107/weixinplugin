package com.magic.xmagichooker.util;



import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author zoubangyue on 2017/10/30.
 */
public class MD5 {
    /**
     * 反编译微信的方法，发现计算结果一样
     *
     * @param paramArrayOfByte
     * @return
     */
    public static String md5(byte[] paramArrayOfByte) {
        char[] arrayOfChar = new char[16];
        arrayOfChar[0] = 48;
        arrayOfChar[1] = 49;
        arrayOfChar[2] = 50;
        arrayOfChar[3] = 51;
        arrayOfChar[4] = 52;
        arrayOfChar[5] = 53;
        arrayOfChar[6] = 54;
        arrayOfChar[7] = 55;
        arrayOfChar[8] = 56;
        arrayOfChar[9] = 57;
        arrayOfChar[10] = 97;
        arrayOfChar[11] = 98;
        arrayOfChar[12] = 99;
        arrayOfChar[13] = 100;
        arrayOfChar[14] = 101;
        arrayOfChar[15] = 102;
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(paramArrayOfByte);
            paramArrayOfByte = md5.digest();
            int k = paramArrayOfByte.length;
            char[] chars = new char[k * 2];
            for (int i = 0, j = 0; i < k; i++) {
                int m = paramArrayOfByte[i];
                int n = j + 1;
                chars[j] = arrayOfChar[(m >>> 4 & 0xF)];
                j = n + 1;
                chars[n] = arrayOfChar[(m & 0xF)];
            }
            return new String(chars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * md5加密
     *
     * @param content
     * @return
     */
    public static String md5(String content) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] encryption = md5.digest();//加密
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    sb.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getData(String cfgFile, int dataKey, Class<?> clazz) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(cfgFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map<Integer, Object> maps = (Map<Integer, Object>) ois.readObject();
            for (Integer key : maps.keySet()) {
                Log.e("key:" + key, ",value: " + maps.get(key));
            }
            if (clazz == Integer.class) {
                return maps.get(dataKey);
            } else if (clazz == String.class) {
                return maps.get(dataKey);
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        return null;
    }


    public static final String wechatMD5(byte[] bArr) {
        int i = 0;
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            byte[] digest = instance.digest();
            int length = digest.length;
            char[] cArr2 = new char[(length * 2)];
            int i2 = 0;
            while (i < length) {
                byte b = digest[i];
                int i3 = i2 + 1;
                cArr2[i2] = cArr[(b >>> 4) & 15];
                i2 = i3 + 1;
                cArr2[i3] = cArr[b & 15];
                i++;
            }
            String str = new String(cArr2);
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析微信重命名用户文件目录
     * @param accountbin 重命名文件
     * @return
     */
    public static byte[] wxReadAccountBin(File accountbin) {
        Throwable th;
        Throwable th2;
        long length = accountbin.length();
        if (length == 4096 || length == 4112) {
            try {
                InputStream ak = new FileInputStream(accountbin);
                try {
                    byte[] bArr = new byte[4096];
                    int i = 0;
                    do {
                        int read = ak.read(bArr, i, 4096 - i);
                        if (read < 0) {
                            return null;
                        }
                        i += read;
                    } while (i < 4096);
                    if (length > 4096) {
                        byte[] bArr2 = new byte[16];
                        int i2 = 0;
                        do {
                            int read2 = ak.read(bArr2, i2, 16 - i2);
                            if (read2 < 0) {
                                return null;
                            }
                            i2 += read2;
                        } while (i2 < 16);
                        if (!Arrays.equals(bArr2, md5Upadate(bArr))) {
                            return null;
                        }
                    }
                    if (ak != null) {
                        ak.close();
                    }
                    return bArr;
                } catch (Throwable th3) {
                    th = th3;
                    th2 = th;
                    if (ak != null) {
                    }
                    throw th;
                }
            } catch (Throwable e2) {
                return null;
            }
        } else {
            return null;
        }
    }

    private static byte[] md5Upadate(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return md5.digest();
        } catch (Exception e) {
            return null;
        }
    }

    public static String decodeWxUserFile(int uin, byte[] bytes) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bytes);
            instance.update(Integer.toString(uin).getBytes());
            byte[] digest = instance.digest();
            StringBuilder sb = new StringBuilder(digest.length * 2);
            char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            for (int i4 = 0; i4 < 16; i4++) {
                byte b2 = digest[i4];
                sb.append(cArr[(b2 >>> 4) & 15]).append(cArr[b2 & 15]);
            }
            String sb2 = sb.toString();
            return sb2;
        } catch (NoSuchAlgorithmException e2) {
            return null;
        }
    }
}
