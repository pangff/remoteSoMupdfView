package com.jhss.romtesomupdf;

import com.artifex.mupdfdemo.AppSoDirInfo;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cookie.store.PersistentCookieStore;
import com.lzy.okhttputils.model.HttpParams;

import android.app.Activity;
import android.app.Application;

import java.io.File;

/**
 * Created by pangff on 16/8/24.
 * Description TODO
 */
public class BaseApplication extends Application  {

    public static  BaseApplication self = null;
    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        AppSoDirInfo.path = getAppDirInfo();

        //        HttpHeaders headers = new HttpHeaders();
        //        headers.put("commonHeaderKey1", "commonHeaderValue1");    //所有的 header 都 不支持 中文
        //        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        //        params.put("commonParamsKey1", "commonParamsValue1");     //所有的 params 都 支持 中文
        //        params.put("commonParamsKey2", "这里支持中文参数");

        //必须调用初始化
        OkHttpUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        OkHttpUtils.getInstance()//
                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                 //全局的写入超时时间
                //                .setCookieStore(new MemoryCookieStore())                           //cookie使用内存缓存（app退出后，cookie消失）
                .setCookieStore(new PersistentCookieStore())                       //cookie持久化存储，如果cookie不过期，则一直有效
                //                .addCommonHeaders(headers)                                         //设置全局公共头
                .addCommonParams(params);
    }

    private String getAppDirInfo() {
        File dir = BaseApplication.self.getDir("jniLibs", Activity.MODE_PRIVATE);
        return dir.getAbsolutePath() + File.separator + "armeabi-v7a/libmupdf_java.so";
    }
}
