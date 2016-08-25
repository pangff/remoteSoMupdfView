package com.jhss.romtesomupdf;

import android.app.Application;

/**
 * Created by pangff on 16/8/24.
 * Description BaseApplication
 */
public class BaseApplication extends Application  {

    public static  BaseApplication self = null;
    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        PdfManager.getInstance().init(this);
    }

}
