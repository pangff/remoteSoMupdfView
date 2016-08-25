package com.jhss.romtesomupdf;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;

import android.app.Activity;
import android.content.Context;

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

    private PdfManager(){

    }

    public static PdfManager getInstance(){
        if(instance==null){
            instance = new PdfManager();
        }
        return instance;
    }


    /**
     * 下载
     * @param fileUrl
     * @param fileCallback
     */
    public void download(String fileUrl,final FileCallback fileCallback){
        OkHttpUtils.get(fileUrl)
                .tag(this)
                .execute(fileCallback);
    }

    /**
     * 插件是否安装
     * @param context
     * @return
     */
    public boolean isPluginInstalled(Context context){
        File dir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        File distFile = new File(dir.getAbsolutePath() + File.separator + "armeabi-v7a/libmupdf_java.so");
        return distFile.exists();
    }


    /**
     * 文件是否存在
     * @param filePath
     * @return
     */
    public boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 卸载插件
     * @param context
     * @param downloadPluginFile
     * @return
     */
    public void unInstallPlugin(Context context,String downloadPluginFile){
        File dir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        File distFile = new File(dir.getAbsolutePath() + File.separator + "armeabi-v7a/libmupdf_java.so");
        distFile.delete();

        File downloadFile = new File(downloadPluginFile);
        downloadFile.delete();
    }

    /**
     * 安装插件
     * @param context
     * @param downloadPluginFile
     */
    public void installPlugin(Context context,String downloadPluginFile){
        final File dir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        unZip(downloadPluginFile,dir.getAbsolutePath()+"/armeabi-v7a");
    }

    /**
     * 解压缩
     * @param unZipfileName
     * @param mDestPath
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
