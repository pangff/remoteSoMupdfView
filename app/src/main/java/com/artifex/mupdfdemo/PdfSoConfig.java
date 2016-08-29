package com.artifex.mupdfdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by pangff on 16/8/25.
 * Description IApplication
 */
public class PdfSoConfig {

    public static final String soName = "libmupdf_java.so";

    public static PdfSoConfig instance;


    SharedPreferences mSharedPreferences;

    private PdfSoConfig() {

    }

    public static PdfSoConfig getInstance() {
        if (instance == null) {
            instance = new PdfSoConfig();
        }
        return instance;
    }

    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences("PdfSoConfig", Context.MODE_PRIVATE);
    }

    public void saveSoPath(String path){
        mSharedPreferences.edit().putString("pdf",path).commit();
    }

    public String getSoPath(){
        return mSharedPreferences.getString("pdf","");
    }


    public void saveSuitableAbi(String abi){
        mSharedPreferences.edit().putString("abi",abi).commit();
    }

    public String getSuitableAbi(){
        return mSharedPreferences.getString("abi","");
    }


    /**
     * 获取合适的cup
     */
    public static String[] getSuitableCpuApiArray() {
        String[] suitableCpuApi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            suitableCpuApi = Build.SUPPORTED_ABIS;
        } else {
            suitableCpuApi = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        return suitableCpuApi;
    }

}
