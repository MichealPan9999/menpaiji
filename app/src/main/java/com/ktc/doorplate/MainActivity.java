package com.ktc.doorplate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.ktc.doorplate.utils.ToastUtils;
import com.ktc.doorplate.views.EditDialog;
import com.ktc.doorplate.views.TipDialog;
import com.ktc.rxpermission.RxPermissions;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ktc.jkf.utils.EnvironmentInfo;
import cn.ktc.jkf.utils.SafeHandler;
import cn.ktc.jkf.utils.TextUtils;
import cn.ktc.jkf.utils.persistence.PersistUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SafeHandler.Callback {

    private static final String NAME_PERSIST = "account";
    private static final String KEY = "serviceIp";
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.linear_progress)
    LinearLayout linearProgress;
    @BindView(R.id.progress_circular)
    ProgressBar progressCircular;
    private String serverIpValue;
    private EditDialog mEditDialog;
    private SafeHandler mSafeHandler;
    private static final int LOAD_URL_NET_CONNECTED = 0;
    private static final int LOAD_URL_NET_DISCONNECTED = 1;
    private String tipDialogTitle = "";
    private String tipDialogMessage = "";
    /**
     * 加载URL超时判断，实际需要2分多钟，这里判断如果1分钟内没有加载完则退出。
     */
    private static final int LOAD_URL_TIME_OUT = 2;

    private Context mContext;
    private TipDialog mTipDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.hookWebView();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = MainActivity.this;
        mSafeHandler = new SafeHandler(this);
        serverIpValue = getServerIp();
        Log.d("panzq", "serverIpValue === " + serverIpValue);
        mEditDialog = new EditDialog(MainActivity.this);
        mTipDialog = new TipDialog(mContext);
        initWebView();
        getCommonPermission();
        if (linearProgress.getVisibility() != View.VISIBLE) {
            linearProgress.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化 WebView，包括WebView监听事件
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                String virtualKeyValue = "doorplate/btnmsg?is_open=";
                if (url.contains(virtualKeyValue)) {
                    int index = url.indexOf(virtualKeyValue);
                    String subString = url.substring(index + virtualKeyValue.length(), index + virtualKeyValue.length() + 1);
                    int value = Integer.parseInt(subString);
                    if (value == 0) {
                        StatusBarUtil.banStatusBar(MainActivity.this);
                    } else {
                        StatusBarUtil.unBanStatusBar(MainActivity.this);
                    }
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("panzq", "==========onPageStarted === url = " + url);
                mSafeHandler.sendMessageOnly(LOAD_URL_TIME_OUT, 30000);
                if (linearProgress.getVisibility() != View.VISIBLE) {
                    linearProgress.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("panzq", "onReceivedError === ");
                ToastUtils.showToast(R.string.str_received_error);
                PersistUtil.save(NAME_PERSIST, KEY, null);
                finish();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("panzq", "onPageFinished === ");
                mSafeHandler.removeMessages(LOAD_URL_TIME_OUT);
                if (linearProgress.getVisibility() != View.GONE) {
                    linearProgress.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * 通过服务器ip加载门牌机内容
     *
     * @param serverIp 服务器ip
     */
    private void loadUrl(String serverIp) {
        if (!TextUtils.isEmpty(serverIp)) {
            webView.loadUrl(serverIp + "/ktcTVController/doorplate.html" + "?deviceid=" + EnvironmentInfo.getDeviceId());
            if (webView.getVisibility() != View.VISIBLE){
                webView.setVisibility(View.VISIBLE);
            }
            PersistUtil.save(NAME_PERSIST, KEY, serverIp);
        } else {
            ToastUtils.showToast(R.string.str_serverip_null);
        }
    }

    /**
     * 申请常规权限
     */
    @SuppressLint("CheckResult")
    private void getCommonPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.STATUS_BAR,
                Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(granted -> {
                    if (granted) {
                        if (!TextUtils.isEmpty(serverIpValue)) {
                            checkAndLoadURL();
                        } else {
                            showSettingEidtDialog(R.string.str_title_serverip, R.string.str_key_serverip);
                        }
                    } else {
                        finish();
                    }
                });
    }

    /**
     * 检查网络并加载url
     */
    private void checkAndLoadURL() {
        if (linearProgress.getVisibility() != View.VISIBLE) {
            linearProgress.setVisibility(View.VISIBLE);
        }
        if (networkConnected(mContext)) {
            checkNetWork(serverIpValue + "/ktcTVController/doorplate.html");
        } else {
            tipDialogTitle = getString(R.string.str_net_disconnected_title);
            tipDialogMessage = getString(R.string.str_net_disconnected_message);
            mSafeHandler.sendMessageOnly(LOAD_URL_NET_DISCONNECTED, 500);
        }
    }

    private String getServerIp() {
        return PersistUtil.get(NAME_PERSIST, KEY, "");
    }

    /**
     * 显示各个EidtDialog对话框
     *
     * @param titleid
     * @param nameid
     */
    private void showSettingEidtDialog(int titleid, int nameid) {
        if (webView.getVisibility() != View.GONE){
            webView.setVisibility(View.GONE);
        }
        if (linearProgress.getVisibility() != View.GONE) {
            linearProgress.setVisibility(View.GONE);
        }
        mEditDialog.show();
        mEditDialog.setTitle(getString(titleid));
        mEditDialog.setName(getString(nameid));
        mEditDialog.setValue("http://192.168.10.5:81");
        mEditDialog.setValueListener(new EditDialog.OnSetValueListener() {
            @Override
            public void setValue(String value) {
                if (!TextUtils.isEmpty(value)) {
                    serverIpValue = value;
                    checkAndLoadURL();
                }
            }

            @Override
            public void cancel() {
                MainActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        if (linearProgress.getVisibility() != View.GONE) {
            linearProgress.setVisibility(View.GONE);
        }
        mSafeHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case LOAD_URL_TIME_OUT:
                ToastUtils.showToast(R.string.str_load_url_time_out);
                PersistUtil.save(NAME_PERSIST, KEY, null);
                MainActivity.this.finish();
                break;
            case LOAD_URL_NET_CONNECTED:
                loadUrl(serverIpValue);
                break;
            case LOAD_URL_NET_DISCONNECTED:
                if (linearProgress.getVisibility() != View.GONE) {
                    linearProgress.setVisibility(View.GONE);
                }
                if (webView.getVisibility() != View.GONE){
                    webView.setVisibility(View.GONE);
                }
                mTipDialog.show();
                mTipDialog.setTitle(tipDialogTitle);
                mTipDialog.setTip(tipDialogMessage);
                mTipDialog.setOnButtonListener(new TipDialog.OnButtonListener() {
                    @Override
                    public void setOkButton() {
                        checkAndLoadURL();
                    }

                    @Override
                    public void setCancelButton() {
                        MainActivity.this.finish();
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 判断当前网络是否可用
     *
     * @param context
     * @return
     */
    public boolean networkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (context != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                assert cm != null;
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    return activeNetworkInfo.isConnected();
                }
            } else {
                assert cm != null;
                Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
                    if (networkCapabilities != null) {
                        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据OKHTTP判断是否可以访问url
     *
     * @param url 要访问的url地址
     */
    private void checkNetWork(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("panzq", "onFailure =============" + e.getMessage());
                tipDialogTitle = getString(R.string.str_network_unreachable_title);
                tipDialogMessage = getString(R.string.str_network_unreachable_message);
                mSafeHandler.sendMessageOnly(LOAD_URL_NET_DISCONNECTED, 500);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("panzq", "onResponse =============" + response.isSuccessful());
                if (response.isSuccessful()) {
                    mSafeHandler.sendMessageOnly(LOAD_URL_NET_CONNECTED, 500);
                }
            }
        });
    }
}
