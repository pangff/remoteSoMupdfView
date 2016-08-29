package com.jhss.romtesomupdf;

import com.jhss.pdf.PdfManager;

import android.app.Application;

/**
 * Created by pangff on 16/8/24.
 * Description BaseApplication
 */
public class BaseApplication extends Application  {

    @Override
    public void onCreate() {
        super.onCreate();
        PdfManager.getInstance().init(this);
    }

}
