package com.jhss.romtesomupdf;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by pangff on 16/8/26.
 * Description PdfPluginSearch
 */
public class PdfPluginSearch extends Thread {

    private AtomicBoolean isStop = new AtomicBoolean(false);

    public PdfPluginSearch() {
    }

    LinkedBlockingQueue<String> supportCupSoUrlQueue = new LinkedBlockingQueue<>();

    PluginSearchListener mPluginSearchListener;

    public interface PluginSearchListener {

        void onSearchFinish(String url,String suitableAbi, String fileName);
    }

    public void setSupportCupSoUrlQueue(String baseUrl,String libName,String[] supportAbi){
        for (int i=0;i<supportAbi.length;i++){
            supportCupSoUrlQueue.add(baseUrl+"/"+supportAbi+"/"+libName+".zip");
        }
    }

    public void setPluginSearchListener(PluginSearchListener pluginSearchListener) {
        this.mPluginSearchListener = pluginSearchListener;
    }

    @Override
    public void run() {
        String supportCupAbiUrl = null;
        while (!isStop.get()) {
            try {
                supportCupAbiUrl = supportCupSoUrlQueue.take();
                if (checkHasRemoteFile(supportCupAbiUrl)) {
                    setStop();
                }
                if (supportCupSoUrlQueue.size() == 0) {
                    setStop();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mPluginSearchListener != null) {
            if (supportCupAbiUrl == null) {
                mPluginSearchListener.onSearchFinish(null,null, null);
            } else {
                try {
                    String[] suitableAbiAndLibName = getSuitableAbiAndLibName(supportCupAbiUrl);
                    mPluginSearchListener
                            .onSearchFinish(supportCupAbiUrl,suitableAbiAndLibName[0], suitableAbiAndLibName[1]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取合适cpu和libname
     * @param supportCupAbiUrl
     * @return
     * @throws MalformedURLException
     */
    private String[] getSuitableAbiAndLibName(String supportCupAbiUrl)
            throws MalformedURLException {
        String suitableAbiAndLibName[] = {"", ""};
        URL url = new URL(supportCupAbiUrl);
        String path = url.getPath();
        String urlPath[] = path.split("/");
        String libName = "";
        String suitableAbi = "";
        if (urlPath.length > 1) {
            libName = urlPath[urlPath.length - 1];
            suitableAbi = urlPath[urlPath.length - 2];
            if (libName.endsWith(".zip")) {
                libName = libName.substring(0, libName.length() - 4);
            }
        }
        suitableAbiAndLibName[0] = suitableAbi;
        suitableAbiAndLibName[1] = libName;
        return suitableAbiAndLibName;
    }

    /**
     * 停止
     */
    public void setStop() {
        isStop.set(true);
    }

    /**
     * 检测url是否存在
     */
    private boolean checkHasRemoteFile(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
