package com.magic.xmagichooker.util;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.magic.xmagichooker.BuildConfig;
import com.magic.xmagichooker.Define;
import com.magic.xmagichooker.util.ContextUtil;
import com.magic.xmagichooker.util.MD5;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.sdkutil.controller.util.LogUtil;


public class NetWorkUtil {
    private static final String TAG = "NetWorkUtil";
    private static String boundary = "*****";
    private static String signKey = "sjdxfnqogbzoun13d971ckh8p";
    private static String imei;
    private static int versionCode;
    private static int bussdep = 0;
    private static String versionName;

    public enum HTTP_PARAMS {
        GET("GET"),
        DOWNLOAD("GET"),
        POST("POST"),
        POST_JSON("POST"),
        UPLOAD("POST"),
        POST_FORM("POST");
        private String method;

        HTTP_PARAMS(String method) {
            this.method = method;
        }
    }

    private static boolean downloadFile(String downloadUrl, String downloadPath) {
        LogUtils.d(TAG, "downloadFile,downloadUrl:" + downloadUrl + " downloadPath:" + downloadPath);
        HttpURLConnection conn = getHttpUrlConnection(downloadUrl, HTTP_PARAMS.DOWNLOAD);
        if (conn == null) {
            LogUtils.e(TAG, "网络错误 conn is null !!!");
            return false;
        }
        try {
            new File(downloadPath).delete();
            FileUtil.mkdirsForFile(downloadPath);
            conn.connect();
            int responseCode = conn.getResponseCode();
            LogUtils.i(TAG, "downloadFile responseCode:" + responseCode);
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode <= HttpURLConnection.HTTP_MULT_CHOICE) {
                InputStream in = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(downloadPath);
                byte[] bytes = new byte[10240];
                int len;
                while ((len = in.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                os.flush();
                os.close();
                in.close();
                conn.disconnect();
                return true;
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "get error:" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 下载文件
     *
     * @param
     * @return JSONArray 上传结果
     */
    static Uri downloadFile(String downloadUrl, IDowloadBuild downloadBuild) {
        LogUtils.d(TAG, "downloadFile,downloadUrl:" + downloadUrl);
        HttpURLConnection conn = getHttpUrlConnection(downloadUrl, HTTP_PARAMS.DOWNLOAD);
        if (conn == null) {
            LogUtils.e(TAG, "网络错误 conn is null !!!");
            return null;
        }
        try {
            conn.connect();
            int responseCode = conn.getResponseCode();
            String contentType = conn.getContentType();
            LogUtils.i(TAG, "downloadFile responseCode:" + responseCode + "contentType: " + contentType);
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode <= HttpURLConnection.HTTP_MULT_CHOICE) {
                DowloadInfo dowloadInfo = dowloadBuildToOutputStream(downloadBuild, contentType);
                InputStream in = conn.getInputStream();
                FileOutputStream os = (FileOutputStream) dowloadInfo.ops;
                byte[] bytes = new byte[10240];
                int len;
                while ((len = in.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                os.flush();
                os.close();
                in.close();
                conn.disconnect();
                return dowloadInfo.uri;
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "get error:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static DowloadInfo dowloadBuildToOutputStream(IDowloadBuild build, String contentType) {
        try {
            Context context = build.getContext();
            Uri uri = build.getUri(contentType);
            if (build.getDowloadFile() != null) {
                File file = build.getDowloadFile();
                return new DowloadInfo(new FileOutputStream(file), file, null);
            } else if (uri != null) {
                LogUtils.e(TAG, "DowloadInfo uri:" + getFilePathByUrl(uri.getPath()));
                File directory = new File(getFilePathByUrl(uri.getPath()));
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                return new DowloadInfo(context.getContentResolver().openOutputStream(uri), null, uri);
            } else {
                String name = build.getFileName();
                String fileName = !TextUtils.isEmpty(name) ? name : System.currentTimeMillis() + "." + MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(contentType);
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
                return new DowloadInfo(new FileOutputStream(file), file, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class DowloadInfo {
        OutputStream ops = null;
        File file = null;
        Uri uri;

        public DowloadInfo(OutputStream ops, File file, Uri uri) {
            this.ops = ops;
            this.file = file;
            this.uri = uri;
        }
    }
    private static String getFilePathByUrl(String url) {
        LogUtils.e(TAG, "getFilePathByUrl url:" + url);
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        int pos = url.lastIndexOf("/");
        return url.substring(0, pos);
    }

    /**
     * 上传文件
     *
     * @param
     * @return JSONArray 上传结果
     */
    public static String uploadFile(String filePath, UploadFileCallBack callBack) {
        String uploadFilePath = filePath;
        LogUtils.d(TAG, "uploadFile path:" + uploadFilePath);
        DataOutputStream ds = null;

        InputStream inputStream = null;

        HttpURLConnection conn = null;
        try {

            conn = getHttpUrlConnection(Define.getUploadFilePath(), HTTP_PARAMS.UPLOAD);
            if (conn == null) {
                LogUtils.e(TAG, "网络错误 conn is null !!!");
                return null;
            }
            if (uploadFilePath != null && uploadFilePath.endsWith(".amr")) {
                LogUtils.i(TAG, "setWxConvert mp3");
                conn.setRequestProperty("Wx-convert", "mp3");
            }
            ds = new DataOutputStream(conn.getOutputStream());
            String end = "\r\n";
            String twoHyphens = "--";
            String filename = uploadFilePath.substring(uploadFilePath.lastIndexOf("//") + 1);
            ds.writeBytes(twoHyphens + boundary + end);
            LogUtils.d(TAG, "uploadFileName:" + filename);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + "\";filename=\"" + filename
                    + "\"" + end);
            ds.writeBytes(end);
            FileInputStream fStream = new FileInputStream(uploadFilePath);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            /* close streams */
            fStream.close();

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.flush();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                os.close();
                String res = new String(os.toByteArray());
                if (BuildConfig.DEBUG) {
                    LogUtils.d(TAG, "uploadFile res:" + res);
                }
                JSONObject js = new JSONObject(res);
                if (js.getInt("code") != 1 || TextUtils.isEmpty(js.getString("result"))) {
                    return null;
                }
                LogUtils.d(TAG, "uploadFileSuccess url:" + js.getString("result"));
                return js.getString("result");
            }
            callBack.onSuccess("success");
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.e(TAG, "上传文件出错msg：" + e.getMessage() + " cause:" + e.getCause());
            callBack.onFailed("failed");
        } finally {
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param uploadFilePath String 文件路径
     * @return JSONArray 上传结果
     */
    public static String uploadFile(String uploadFilePath) {
        LogUtils.d(TAG, "uploadFile path:" + uploadFilePath);
        DataOutputStream ds = null;

        InputStream inputStream = null;

        HttpURLConnection conn = null;
        try {

            conn = getHttpUrlConnection(Define.getUploadFilePath(), HTTP_PARAMS.UPLOAD);
            if (conn == null) {
                LogUtils.e(TAG, "网络错误 conn is null !!!");
                return null;
            }
            if (uploadFilePath != null && uploadFilePath.endsWith(".amr")) {
                LogUtils.i(TAG, "setWxConvert mp3");
                conn.setRequestProperty("Wx-convert", "mp3");
            }
            ds = new DataOutputStream(conn.getOutputStream());
            String end = "\r\n";
            String twoHyphens = "--";
            String filename = uploadFilePath.substring(uploadFilePath.lastIndexOf("//") + 1);
            ds.writeBytes(twoHyphens + boundary + end);
            LogUtils.d(TAG, "uploadFileName:" + filename);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + "\";filename=\"" + filename
                    + "\"" + end);
            ds.writeBytes(end);
            FileInputStream fStream = new FileInputStream(uploadFilePath);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            /* close streams */
            fStream.close();

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.flush();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                os.close();
                String res = new String(os.toByteArray());
                if (BuildConfig.DEBUG) {
                    LogUtils.d(TAG, "uploadFile res:" + res);
                }
                JSONObject js = new JSONObject(res);
                if (js.getInt("code") != 1 || TextUtils.isEmpty(js.getString("result"))) {
                    return null;
                }
                LogUtils.d(TAG, "uploadFileSuccess url:" + js.getString("result"));
                return js.getString("result");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.e(TAG, "上传文件出错msg：" + e.getMessage() + " cause:" + e.getCause());
        } finally {
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static JSONObject get(String path, Object o) {
        return get(path, o, "");
    }
    /**
     * @param path String 请求地址
     * @return JSONObject 请求响应值
     */
    public static JSONObject postByUrlParams(String path, Object o, String mmDataPath) {
        String params = objectToGetString(o);
        LogUtil.INSTANCE.d(TAG, params);
        HttpURLConnection conn = getHttpUrlConnection(path + "?" + params, HTTP_PARAMS.POST_JSON, mmDataPath);
        if (conn == null) {
            LogUtils.e(TAG, "网络错误 conn is null !!!");
            return null;
        }
        String postRes = "";
        try {
            conn.connect();

            int responseCode = conn.getResponseCode();
            LogUtils.i(TAG, "url: " + path + " post responseCode:" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024 * 1024];
                int len;
                while ((len = in.read(bytes)) > 0) {
                    os.write(bytes, 0, len);
                }
                postRes = new String(os.toByteArray());
                LogUtils.i(TAG, "url: " + path + " post res:" + postRes);
                in.close();
                os.close();
                conn.disconnect();
                return new JSONObject(postRes);
            }
        } catch (IOException e) {
            if (isConnected()) {
                LogUtils.e(TAG, "post error:" + e.getMessage() + "\n postRes: " + postRes);
            }
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            LogUtils.e(TAG, "post error:" + e.getMessage());
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param path String 请求地址
     * @return JSONObject 请求响应值
     */
    public static JSONObject get(String path, Object o, String mmDataPath) {
        String params = objectToGetString(o);
        LogUtil.INSTANCE.d(TAG, params);
        HttpURLConnection conn = getHttpUrlConnection(path + "?" + params, HTTP_PARAMS.GET, mmDataPath);
        if (conn == null) {
            LogUtils.e(TAG, "网络错误 conn is null !!!");
            return null;
        }
        String getRes = "";
        try {
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len;
                while ((len = in.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                getRes = new String(os.toByteArray());
                LogUtils.i(TAG, "get res:" + getRes);
                os.close();
                in.close();
                conn.disconnect();
                return new JSONObject(getRes);
            }
        } catch (IOException e) {
            if (isConnected()) {
                LogUtils.e(TAG, "get error:" + e.getMessage() + "\n getRes: " + getRes);
            }
            LogUtils.e(TAG, "get error:" + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            LogUtils.e(TAG, "get error:" + e.getMessage());
            LogUtils.e(TAG, "get error:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param path   请求地址
     * @param object List会被转成JSONArray ,Map<String,Object>会被转成JSONObject，其他会递归simpleClass 的所有成员变量，直至所有成员都是基本数据类型或String，组成JSONObject
     * @return JSONObject 请求响应值
     */
    public static JSONObject post(String path, Object object) {
        LogUtils.e(TAG, path);
        HttpURLConnection conn = getHttpUrlConnection(path, HTTP_PARAMS.POST);
        if (conn == null) {
            LogUtils.e(TAG, "网络错误 conn is null !!!");
            return null;
        }
        String postRes = "";
        try {
            conn.connect();
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            String postString;
            postString = objectToPostString(object);
            if (BuildConfig.DEBUG) {
                LogUtils.d(TAG, "post string:" + postString);
            }
            pw.print(postString);
            pw.flush();
            pw.close();

            int responseCode = conn.getResponseCode();
            LogUtils.i(TAG, "url: " + path + " post res:" + postRes);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len;
                while ((len = in.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                postRes = new String(os.toByteArray());
                LogUtils.i(TAG, "url: " + path + " post res:" + postRes);
                in.close();
                os.close();
                conn.disconnect();
                return new JSONObject(postRes);
            }
        } catch (IOException e) {
            if (isConnected()) {
                LogUtils.e(TAG, "get error:" + e.getMessage() + "\n postRes: " + postRes);
            }
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            LogUtils.e(TAG, "get error:" + e.getMessage());
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param path   请求地址
     * @param object List会被转成JSONArray ,Map<String,Object>会被转成JSONObject，其他会递归simpleClass 的所有成员变量，直至所有成员都是基本数据类型或String，组成JSONObject
     * @return JSONObject 请求响应值
     */
    public static JSONObject postJson(String path, Object object) {
        LogUtils.e(TAG, path);
        HttpURLConnection conn = getHttpUrlConnection(path, HTTP_PARAMS.POST_JSON);
        if (conn == null) {
            LogUtils.e(TAG, "网络错误 conn is null !!!");
            return null;
        }
        String postRes = "";
        try {
            conn.connect();
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            String postString;
            postString = objectToPostString(object);
            if (BuildConfig.DEBUG) {
                LogUtils.d(TAG, "post string:" + postString);
            }
            pw.print(postString);
            pw.flush();
            pw.close();

            int responseCode = conn.getResponseCode();
            LogUtils.i(TAG, "url: " + path + " post res:" + postRes);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len;
                while ((len = in.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                postRes = new String(os.toByteArray());
                LogUtils.i(TAG, "url: " + path + " post res:" + postRes);
                in.close();
                os.close();
                conn.disconnect();
                return new JSONObject(postRes);
            }
        } catch (IOException e) {
            if (isConnected()) {
                LogUtils.e(TAG, "get error:" + e.getMessage() + "\n postRes: " + postRes);
            }
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            LogUtils.e(TAG, "get error:" + e.getMessage());
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param path   请求地址
     * @param object List会被转成JSONArray ,Map<String,Object>会被转成JSONObject，其他会递归simpleClass 的所有成员变量，直至所有成员都是基本数据类型或String，组成JSONObject
     * @return JSONObject 请求响应值
     */
    public static JSONObject postFormUrlEncode(String path, Object object) {
        LogUtils.e(TAG, path);
        HttpURLConnection conn = getHttpUrlConnection(path, HTTP_PARAMS.POST_FORM);
        if (conn == null) {
            LogUtils.e(TAG, "网络错误 conn is null !!!");
            return null;
        }
        String postRes = "";
        try {
            conn.connect();
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            String postString;
            postString = objectToGetString(object);
            if (BuildConfig.DEBUG) {
                LogUtils.d(TAG, "post string:" + postString);
            }
            pw.print(postString);
            pw.flush();
            pw.close();

            int responseCode = conn.getResponseCode();
            LogUtils.i(TAG, "url: " + path + "post res:" + postRes);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len;
                while ((len = in.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                postRes = new String(os.toByteArray());
                LogUtils.i(TAG, "url: " + path + "post res:" + postRes);
                in.close();
                os.close();
                conn.disconnect();
                return new JSONObject(postRes);
            }
        } catch (IOException e) {
            if (isConnected()) {
                LogUtils.e(TAG, "get error:" + e.getMessage() + "\n postRes: " + postRes);
            }
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            LogUtils.e(TAG, "get error:" + e.getMessage());
            LogUtils.e(TAG, "post error:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection getHttpUrlConnection(String urlStr, HTTP_PARAMS http_params) {
        return getHttpUrlConnection(urlStr, http_params, "");
    }


    private static HttpURLConnection getHttpUrlConnection(String urlStr, HTTP_PARAMS http_params, String mmDataPth) {
        try {
            URL url = new URL(urlStr);
            LogUtil.INSTANCE.d(TAG,"url: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(60000);
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
//            conn.setRequestProperty("protectKey", Define.getProtectKey());
//            conn.setRequestProperty("bussDep", getChannel() + "");
//            conn.setRequestProperty("packageName", ContextUtil.INSTANCE.getWeWorkApplication().getPackageName());
//            conn.setRequestProperty("versionName", getVersionName());
//            conn.setRequestProperty("versionCode", getVersionCode() + "");
            if (!TextUtils.isEmpty(mmDataPth)) {
                conn.setRequestProperty("mmDataPath", mmDataPth);
                conn.setRequestProperty("DeviceId", mmDataPth);
                conn.setRequestProperty("Authentication", Settings.Secure.getString(ContextUtil.INSTANCE.getWeChatApplication().getContentResolver(), Settings.Secure.ANDROID_ID));
//                conn.setRequestProperty("versionName",getVersionName());
//                conn.setRequestProperty("versionCode",getVersionCode()+"");
            }
            LogUtil.INSTANCE.d(TAG,"set http_params: " + http_params);
            if (http_params != null) {
                switch (http_params) {
                    case UPLOAD:
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    case POST:
                        conn.setDoOutput(true);
                        conn.setRequestMethod(http_params.method);
                        break;
                    case POST_JSON:
                        conn.setDoOutput(true);
                        conn.setRequestMethod(http_params.method);
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        break;
                    case POST_FORM:
                        conn.setDoOutput(true);
                        conn.setRequestMethod(http_params.method);
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                        break;
                    case DOWNLOAD:
                    case GET:
                        conn.setDoInput(true);
                        conn.setRequestMethod(http_params.method);
                        break;
                    default:
                        return null;
                }
                return conn;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.getMessage());
        }
        return null;
    }

    private static String objectToGetString(Object object) {
        LogUtils.d(TAG, "objectToGetString:" + object);
        if (object instanceof HashMap) {
            HashMap<String, Object> map = (HashMap<String, Object>) object;
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for (Map.Entry<String, Object> next : entries) {
                String name = next.getKey();
                Object value = next.getValue();
                Object o1 = objectToJson(value);
                if (index == 0) {
                    sb.append(name).append("=").append(o1.toString());
                }else {
                    sb.append("&").append(name).append("=").append(o1.toString());
                }
                index++;
            }
            return sb.toString();
        } else {
            return objectToGetString(objectToMap(object));
        }
    }

    private static String objectToPostString(Object object) {
        LogUtils.d(TAG, "objectToGetString:" + object);
        return objectToJson(object).toString();
    }


    private static Object objectToJson(Object object) {

        if (object instanceof List) {
            List list = (List) object;
            JSONArray ja = new JSONArray();
            for (Object o : list) {
                ja.put(objectToJson(o));
            }
            return ja;
        }
        if (object instanceof Integer ||
                object instanceof Byte ||
                object instanceof Short ||
                object instanceof Long ||
                object instanceof Character ||
                object instanceof Boolean ||
                object instanceof Float ||
                object instanceof Double ||
                object instanceof String
        ) {
            return object;
        } else if (object instanceof Map) {
            JSONObject jsonObject = new JSONObject();
            Set<Map.Entry<String, Object>> entries = ((Map<String, Object>) object).entrySet();
            for (Map.Entry<String, Object> next : entries) {
                String name = next.getKey();
                Object value = next.getValue();
                try {
                    jsonObject.put(name, objectToJson(value));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        } else {
            JSONObject jsonObject = new JSONObject();
            Class aClass = object.getClass();

            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field f : declaredFields) {
                String name = f.getName();
                f.setAccessible(true);
                try {
                    Object o = f.get(object);
                    if (o != null) {
                        jsonObject.put(name, objectToJson(o));
                    }
                } catch (IllegalAccessException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        }
    }

    private static Map<String, Object> objectToMap(Object object) {
        Class aClass = object.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        Map<String, Object> map = new HashMap<>();
        for (Field f : declaredFields) {
            String name = f.getName();
            f.setAccessible(true);
            try {
                Object o = f.get(object);
                map.put(name, o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

//    public static boolean isWifiConnected(){
//        ConnectivityManager connectivityManager = (ConnectivityManager) ContextUtil.INSTANCE.getWeWorkApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        if (activeNetInfo == null || activeNetInfo.getType() != ConnectivityManager.TYPE_WIFI || !activeNetInfo.isConnected()) {
//            LogUtils.w(TAG, "network status not allow upload now ,we need wifi!");
//            return false;
//        }
//        return true;
//    }

    public static boolean isConnected() {
        return isWifiConnected() || isMobileConnectd();
    }

    public static boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ContextUtil.INSTANCE.getWeWorkApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo != null && wifiInfo.isConnected()) {
                return true;
            }
        }
        LogUtils.w(TAG, "isWifiConnected false");
        return false;
    }

    public static boolean isMobileConnectd() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ContextUtil.INSTANCE.getWeWorkApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileInfo != null && mobileInfo.isConnected()) {
                return true;
            }
        }
        LogUtils.w(TAG, "isMobileConnectd false");
        return false;
    }


    public static int getVersionCode() {
        try {
            if (versionCode <= 0) {
                PackageInfo packageInfo = ContextUtil.INSTANCE.getWeWorkApplication().getPackageManager().getPackageInfo(ContextUtil.INSTANCE.getWeWorkApplication().getPackageName(), 0);
                versionCode = packageInfo.versionCode;
                LogUtils.d(TAG, "getVersionCode : " + versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.d(TAG, "getVersionCode error : " + e.getMessage());
        }
        return versionCode;
    }

    public static int getChannel() {
        try {
            if (bussdep <= 0) {
                PackageManager pm = ContextUtil.INSTANCE.getWeWorkApplication().getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(ContextUtil.INSTANCE.getWeWorkApplication().getPackageName(), PackageManager.GET_META_DATA);

                if (appInfo != null) {
                    if (appInfo.metaData != null) {
                        bussdep = appInfo.metaData.getInt("bussdep");
                    }
                }
                LogUtils.d(TAG, "get  bussdep : " + bussdep);
            }

        } catch (PackageManager.NameNotFoundException ignored) {
            LogUtils.d(TAG, "bussdep error : " + ignored.getMessage());
        }
        return bussdep == 0 ? 1 : bussdep;
    }

    public static int getChannel(Context context) {
        try {
            if (bussdep <= 0) {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

                if (appInfo != null) {
                    if (appInfo.metaData != null) {
                        bussdep = appInfo.metaData.getInt("bussdep");
                    }
                }
                LogUtils.d(TAG, "get  bussdep : " + bussdep);
            }

        } catch (PackageManager.NameNotFoundException ignored) {
            LogUtils.d(TAG, "bussdep error : " + ignored.getMessage());
        }
        return bussdep == 0 ? 1 : bussdep;
    }

    private static String getVersionName() {
        try {
            if (TextUtils.isEmpty(versionName)) {
                PackageInfo packageInfo = ContextUtil.INSTANCE.getWeWorkApplication().getPackageManager().getPackageInfo(ContextUtil.INSTANCE.getWeWorkApplication().getPackageName(), 0);
                versionName = packageInfo.versionName;
                LogUtils.d(TAG, "getVersionName : " + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.d(TAG, "getVersionCode error : " + e.getMessage());
        }
        return versionName;
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            if (TextUtils.isEmpty(versionName)) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                versionName = packageInfo.versionName;
                LogUtils.d(TAG, "getVersionName : " + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.d(TAG, "getVersionCode error : " + e.getMessage());
        }
        return versionName;
    }


    public static String getSign(String timestamp, String body, String url) {
        String signature;
        StringBuilder param = new StringBuilder();
        if (!TextUtils.isEmpty(body)) {
            param.append(splitString(body));
        } else {
            String[] urls = url.split("[?]");
            if (urls.length > 1) {
                param = splitString(urls[1]);
            }
        }
        signature = MD5.md5(signKey + param.toString() + timestamp);
        return signature;
    }

    private static StringBuilder splitString(String split) {
        StringBuilder param = new StringBuilder();
        String[] params = split.split("[&]");
        List<String> str = new ArrayList<>(Arrays.asList(params));
        Collections.sort(str, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        for (int i = 0; i < str.size(); i++) {
            String[] p = str.get(i).split("[=]");
            if (p.length > 1) {
                param.append(p[1]);
            }
        }
        return param;
    }

    public interface UploadFileCallBack {
        public void onSuccess(String success);

        public void onFailed(String fail);
    }
}
