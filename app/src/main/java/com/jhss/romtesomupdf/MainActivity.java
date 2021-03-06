package com.jhss.romtesomupdf;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.request.BaseRequest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button btPluginManager;

    Button btPdfFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPluginManager = (Button) findViewById(R.id.btPluginManager);
        btPdfFileManager = (Button) findViewById(R.id.btPdfFileManager);

        resetBtnState();

        btPluginManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PdfManager.getInstance().isPluginInstalled(MainActivity.this)) {
                    PdfManager.getInstance().unInstallPlugin(MainActivity.this,
                            Environment.getExternalStorageDirectory()
                                    + "/jhss/pdf/libmupdf_java.so.zip");
                    resetBtnState();
                } else {
                    downPdfPlugin(btPluginManager);
                }
            }
        });

        btPdfFileManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (PdfManager.getInstance()
                        .isFileExist(
                                Environment.getExternalStorageDirectory() + "/jhss/pdf/qcl.pdf")) {
                    showPdf();
                } else {
                    downPdf(v);
                }
            }
        });
    }

    private void resetBtnState() {
        if (PdfManager.getInstance().isPluginInstalled(this)) {
            btPluginManager.setText("卸载插件");
            btPdfFileManager.setClickable(true);
        } else {
            btPluginManager.setText("安装插件");
            btPdfFileManager.setClickable(false);
        }

        if (PdfManager.getInstance()
                .isFileExist(Environment.getExternalStorageDirectory() + "/jhss/pdf/qcl.pdf")) {
            btPdfFileManager.setText("打开文件");
        } else {
            btPdfFileManager.setText("下载文件");
        }
    }


    public void downPdfPlugin(final View view) {

        PdfManager.getInstance()
                .downLoadPlugin("http://download.youguu.com/download/mupdf",
                        new FileCallback(Environment.getExternalStorageDirectory() +
                                "/jhss/pdf", "libmupdf_java.so.zip") {
                            @Override
                            public void onResponse(boolean isFromCache, File file, Request request,
                                    @Nullable Response response) {
                                if(response!=null && response.isSuccessful()){
                                    PdfManager.getInstance()
                                            .installPlugin(MainActivity.this,
                                                    file.getAbsolutePath());
                                    resetBtnState();
                                }
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                ((TextView) view).setText("下载pdf插件－开始下载");
                            }

                            @Override
                            public void downloadProgress(long currentSize, long totalSize,
                                    float progress,
                                    long networkSpeed) {
                                super.downloadProgress(currentSize, totalSize, progress,
                                        networkSpeed);
                                ((TextView) view).setText(
                                        "下载pdf插件－totalSize:" + totalSize + ";currentSize:"
                                                + currentSize + ";progress:" + progress);
                            }
                        });
    }


    public void downPdf(final View view) {
        PdfManager.getInstance()
                .download("http://www.axmag.com/download/pdfurl-guide.pdf",
                        new FileCallback(Environment.getExternalStorageDirectory() +
                                "/jhss/pdf", "qcl.pdf") {

                            @Override
                            public void onResponse(boolean isFromCache, File file, Request request,
                                    @Nullable Response response) {
                                resetBtnState();
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                ((TextView) view).setText("下载pdf文件－开始下载");
                            }

                            @Override
                            public void downloadProgress(long currentSize, long totalSize,
                                    float progress,
                                    long networkSpeed) {
                                super.downloadProgress(currentSize, totalSize, progress,
                                        networkSpeed);
                                ((TextView) view).setText(
                                        "下载pdf文件－totalSize:" + totalSize + ";currentSize:"
                                                + currentSize
                                                + ";progress:" + progress);
                            }
                        });
    }

    public void showPdf() {
        Uri uri = Uri.fromFile(
                new File(Environment.getExternalStorageDirectory() + "/jhss/pdf/qcl.pdf"));
        Intent intent = new Intent(this, MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }


}
