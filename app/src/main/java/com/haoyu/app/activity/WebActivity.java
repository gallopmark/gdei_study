package com.haoyu.app.activity;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haoyu.app.base.BaseActivity;
import com.haoyu.app.dialog.MaterialDialog;
import com.haoyu.app.download.DownloadManager;
import com.haoyu.app.download.DownloadTask;
import com.haoyu.app.gdei.student.R;
import com.haoyu.app.utils.Common;
import com.haoyu.app.utils.Constants;
import com.haoyu.app.utils.NetStatusUtil;
import com.haoyu.app.utils.ScreenUtils;
import com.haoyu.app.view.AppToolBar;
import com.haoyu.app.view.ProgressWebView;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;

/**
 * 创建日期：2017/8/28 on 11:35
 * 描述:
 * 作者:马飞奔 Administrator
 */
public class WebActivity extends BaseActivity {
    private WebActivity context = this;
    @BindView(R.id.toolBar)
    AppToolBar toolBar;
    @BindView(R.id.fl_content)
    FrameLayout fl_content;
    @BindView(R.id.ll_failure)
    LinearLayout ll_failure;
    private ProgressWebView webView;
    private String url;
    private String fileRoot = Constants.fileDownDir;
    private String filePath, fileName;

    @Override
    public int setLayoutResID() {
        return R.layout.activity_web;
    }

    @Override
    public void initView() {
        url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        toolBar.setTitle_text(title);
        configWebview();
    }

    private void configWebview() {
        fl_content.setVisibility(View.VISIBLE);
        webView = new ProgressWebView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        fl_content.addView(webView);
        webView.setOnReceivedListener(new ProgressWebView.OnReceivedListener() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                toolBar.setTitle_text(title);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                fl_content.setVisibility(View.GONE);
                ll_failure.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                fl_content.setVisibility(View.GONE);
                ll_failure.setVisibility(View.VISIBLE);
            }
        });
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl(url);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                onDownload(url);
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                        webView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

    }

    private void onDownload(final String url) {
        fileName = Common.getFileName(url);
        filePath = fileRoot + File.separator + fileName;
        if (new File(filePath).exists()) {
            Common.openFile(context, new File(filePath));
        } else {
            if (NetStatusUtil.isConnected(context)) {
                String message;
                if (NetStatusUtil.isWifi(context))
                    message = "创建下载链接：" + url;
                else
                    message = "当前非Wifi网络环境，下载文件会消耗过多的流量！是否创建下载链接：" + url;
                downloadTips(url, message);
            } else {
                toast(context, "当前网络不稳定，请检查网络设置！");
            }
        }
    }

    private void downloadTips(final String url, String message) {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle("下载提示");
        dialog.setMessage(message);
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("下载", new MaterialDialog.ButtonClickListener() {
            @Override
            public void onClick(View v, AlertDialog dialog) {
                download(url);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void download(final String url) {
        View contentView = getLayoutInflater().inflate(R.layout.dialog_download, null);
        TextView tv_fileName = contentView.findViewById(R.id.tv_fileName);
        final ProgressBar mProgressBar = contentView.findViewById(R.id.mRrogressBar);
        Button bt_close = contentView.findViewById(R.id.bt_close);
        TextView tv_download = contentView.findViewById(R.id.tv_download);
        final TextView tv_progress = contentView.findViewById(R.id.tv_progress);
        tv_fileName.setText(fileName);
        tv_download.setText("正在下载");
        tv_progress.setText("0%");
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((ScreenUtils.getScreenWidth(context) / 6 * 5), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(contentView, params);
        DownloadManager.getInstance().create(url).setFilePath(fileRoot).setFileName(fileName).addListener(new com.haoyu.app.download.DownloadListener() {
            @Override
            public void onProgress(DownloadTask downloadTask, long soFarBytes, long totalBytes) {
                mProgressBar.setProgress((int) soFarBytes);
                mProgressBar.setMax((int) totalBytes);
                tv_progress.setText(accuracy(soFarBytes, totalBytes));
            }

            @Override
            public void onSuccess(DownloadTask downloadTask, String savePath) {
                dialog.dismiss();
                if (new File(savePath).exists())
                    Common.openFile(context, new File(savePath));
                else
                    toast(context, "下载的文件已被删除");
            }

            @Override
            public void onFailed(DownloadTask downloadTask) {
                dialog.dismiss();
                toast(context, "下载失败");
            }

            @Override
            public void onPaused(DownloadTask downloadTask) {
                dialog.dismiss();
            }

            @Override
            public void onCancel(DownloadTask downloadTask) {
                dialog.dismiss();
            }
        }).start();
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                DownloadManager.getInstance().pause(url);
            }
        });
    }

    private String accuracy(double num, double total) {
        try {
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
            //可以设置精确几位小数
            df.setMaximumFractionDigits(0);
            //模式 例如四舍五入
            df.setRoundingMode(RoundingMode.HALF_UP);
            double accuracy_num = num / total * 100;
            return df.format(accuracy_num) + "%";
        } catch (Exception e) {
            return num / total * 100 + "%";
        }
    }

    @Override
    public void setListener() {
        toolBar.setOnLeftClickListener(new AppToolBar.OnLeftClickListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });
        ll_failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fl_content.setVisibility(View.VISIBLE);
                ll_failure.setVisibility(View.GONE);
                webView.reload();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers(); //小心这个！！！暂停整个 WebView 所有布局、解析、JS。
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fl_content.removeAllViews();
        if (webView != null) {
            webView.stopLoading();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }
}
