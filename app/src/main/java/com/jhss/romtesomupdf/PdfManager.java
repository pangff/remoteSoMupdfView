package com.jhss.romtesomupdf;

import com.artifex.mupdfdemo.PdfSoConfig;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by pangff on 16/8/24.
 * Description PdfManager
 */
public class PdfManager {


    private static PdfManager instance;

    PdfPluginSearch mPdfPluginSearch;

    private PdfManager() {

    }

    public static PdfManager getInstance() {
        if (instance == null) {
            instance = new PdfManager();
        }
        return instance;
    }


    public void init(Application context) {
        OkHttpUtils.init(context);
        PdfSoConfig.getInstance().init(context);
        mPdfPluginSearch = new PdfPluginSearch();
    }


    private String getAppDirInfo(Application context) {
        File dir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        return dir.getAbsolutePath();
    }

    /**
     * 下载
     */
    public void download(String fileUrl, final FileCallback fileCallback) {
        OkHttpUtils.get(fileUrl)
                .tag(this)
                .execute(fileCallback);
    }

    /**
     * 安装插件
     */
    public void downLoadPlugin(final String baseUrl, final FileCallback fileCallback) {
        String[] supportAbiArray = PdfSoConfig.getSuitableCpuApiArray();
        mPdfPluginSearch.setSupportCupSoUrlQueue(baseUrl, PdfSoConfig.soName, supportAbiArray);
        mPdfPluginSearch.setPluginSearchListener(new PdfPluginSearch.PluginSearchListener() {
            @Override
            public void onSearchFinish(String fileUrl, String suitableAbi, String fileName) {
                if (fileUrl == null) {
                    fileCallback.onError(false, null, null, null);
                } else {
                    PdfSoConfig.getInstance().saveSuitableAbi(suitableAbi);
                    download(fileUrl, fileCallback);
                }
            }
        });
        mPdfPluginSearch.start();
    }

    /**
     * 插件是否安装
     */
    public boolean isPluginInstalled(Context context) {
        String cupAbi = PdfSoConfig.getInstance().getSuitableAbi();
        if (TextUtils.isEmpty(cupAbi)) {
            return false;
        }
        File dir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        File distFile = new File(
                dir.getAbsolutePath() + File.separator + cupAbi + File.separator
                        + PdfSoConfig.soName);
        return distFile.exists();
    }


    /**
     * 文件是否存在
     */
    public boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 卸载插件
     */
    public void unInstallPlugin(Context context, String downloadPluginFile) {
        String cupAbi = PdfSoConfig.getInstance().getSuitableAbi();
        File dir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        File distFile = new File(
                dir.getAbsolutePath() +
                        File.separator + cupAbi + File.separator
                        + PdfSoConfig.soName);
        distFile.delete();

        File downloadFile = new File(downloadPluginFile);
        downloadFile.delete();
    }


    /**
     * 安装插件
     */
    public void installPlugin(Context context, String downloadPluginFile) {
        String cupAbi = PdfSoConfig.getInstance().getSuitableAbi();
        final File dir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        unZip(downloadPluginFile, dir.getAbsolutePath() + "/" + cupAbi);
        String path = getAppDirInfo((Application) context.getApplicationContext()) + "/"
                + cupAbi + "/" + PdfSoConfig.soName;
        PdfSoConfig.getInstance().saveSoPath(path);
    }

    /**
     * 解压缩
     */
    private void unZip(String unZipfileName, String mDestPath) {
        if (!mDestPath.endsWith("/")) {
            mDestPath = mDestPath + "/";
        }
        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;
        File file = null;
        int readedBytes = 0;
        byte buf[] = new byte[4096];
        try {
            zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(unZipfileName)));
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                file = new File(mDestPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    while ((readedBytes = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readedBytes);
                    }
                    fileOut.close();
                }
                zipIn.closeEntry();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
