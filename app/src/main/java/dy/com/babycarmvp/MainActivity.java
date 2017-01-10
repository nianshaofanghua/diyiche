package dy.com.babycarmvp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends ActivityTools implements MyWebChomeClient.OpenFileChooserCallBack, View.OnClickListener {
    WebView mWebView;
    String mUuId;
    TextView mQuest;
    ImageView mOverTime;
    ConnectivityManager manager;
    JsObject mJsObject;
    Boolean mBoolean;
    Activity mContext;
    private final int TIMEOUT_ERROR = 9527;
    private long timeout = 10000;
    private final int HTTP_ERROR = 9528;
    private final int HTTP_SUCCESS = 9529;
    Timer mTimer;
    Boolean mPermission = false;
    Boolean mOnFinish = false;
    private final int LOGIN_PERMISSION = 9530;
    private final int LOGIN_ = 9999;
    private final int LOGIN_P = 1020;
    private Update mInfo;
    Button mPhoto;
    Button mCamera;
    PopupWindow mPpopu;
    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    Intent mSourceIntent;
    private ValueCallback<Uri> mUpload;
    public ValueCallback<Uri[]> mUploadMsgForAndroid5;
    Boolean mCameraOrAlbum = false;
    Message mMessage;
    View mView;
    Button btn1, btn2, btn3, btn4, btn5;
    // handler 接收消息 并判断webview是否超时   404错误   在页面加载完成并且 请求到权限
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIMEOUT_ERROR:
                    if (!(mWebView.getContentHeight() != 0 && mWebView.getProgress() == 100)) {
                        // mOverTime.setVisibility(View.VISIBLE);
                    }
                    break;
                case HTTP_ERROR:
                    // mOverTime.setVisibility(View.VISIBLE);
                    break;
                case HTTP_SUCCESS:
                    mOverTime.setVisibility(View.GONE);
                    break;
                case LOGIN_PERMISSION:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (mOnFinish && mPermission) {
                            L.e("得到登录id" + getuuid());
                            mUuId = "javascript:uuid(\"" + getuuid().substring(9, 23)+"##" + "\")";
                            mWebView.loadUrl(mUuId);

                        }
                    } else {
                        mUuId = "javascript:uuid(\"" + getuuid().substring(9, 23) + "\")";
                        mWebView.loadUrl(mUuId);
                    }

                    break;
                case LOGIN_:
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    Toast.makeText(getApplicationContext(), "键盘弹出方式改变", Toast.LENGTH_SHORT).show();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    break;
                case LOGIN_P:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PermissionGen.with(MainActivity.this).addRequestCode(100).permissions(Manifest.permission.READ_PHONE_STATE).request();

                    }
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mView = findViewById(R.id.activity_main);
        mBoolean = false;
        mContext = this;
        mJsObject = new JsObject();
        mWebView = (WebView) findViewById(R.id.web);
        mQuest = (TextView) findViewById(R.id.requset);
        mOverTime = (ImageView) findViewById(R.id.overtime);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        setWebView(mWebView);
        sendMessage(LOGIN_P);
        update();
        checkNetworkState();
        // 未连接网络文字 监听
        mQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetworkState();
            }
        });

//       js调用Android方法回调 改变布尔值
        mJsObject.setCallBack(new JsObject.CallBack() {
            @Override
            public void callback(String str) {
                mBoolean = true;
//                Message msg = new Message();
//                msg.what = LOGIN_;
//                mHandler.sendMessage(msg);
                Toast.makeText(getApplicationContext(), "登陆键盘弹出", Toast.LENGTH_SHORT).show();
                sendMessage(LOGIN_);
            }
        });

    }

    // 获取手机网络连接情况 如未连接网络 提示
    private boolean checkNetworkState() {
        boolean flag = false;
//得到网络连接信息
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            mWebView.setVisibility(View.GONE);
            mQuest.setVisibility(View.VISIBLE);
        } else {
            mWebView.setVisibility(View.VISIBLE);
            mQuest.setVisibility(View.GONE);
            quest();
        }
        return false;
    }

    // webview基本设置 监听 js交互
    @SuppressWarnings("deprecation")
    public void quest() {
        setView(R.id.activity_main);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setUseWideViewPort(true);
      //  webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDatabaseEnabled(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        mWebView.addJavascriptInterface(mJsObject, "isLogin");
        // mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
       // mWebView.loadUrl("http://192.168.1.125:8080/diyi/open/ipad/login1.jsp");
        //mWebView.loadUrl("http://m.wxb.com.cn/mobile/?channelNo=B10176&channelSite=s01&uuid=e027b27f-6710-48df-b86b-176252048dc7\n");
        // mWebView.loadUrl("http://120.76.214.171:8080/guangzhoudiyi/open/ipad/login.jsp");
       mWebView.loadUrl("http://116.255.205.195:80/diyi/open/ipad/login.jsp");
//mWebView.loadUrl("https://creditcardapp.bankcomm.com/applynew/front/apply/new/index.html?trackCode=A121417239531&commercial_id=null");
        //mWebView.loadUrl("http://192.168.1.125:8080/diyi/open/ipad/login1.jsp");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                mTimer = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
/*
* 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
*/

//                        Message msg = new Message();
//                        msg.what = TIMEOUT_ERROR;
//                        mHandler.sendMessage(msg);
                        sendMessage(TIMEOUT_ERROR);
                        mTimer.cancel();
                        mTimer.purge();
                    }

                };
                mTimer.schedule(tt, timeout, 1);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                L.e("onPageFinished" + url);
                mOnFinish = true;
                sendMessage(LOGIN_PERMISSION);
                validStatusCode(url);
                mTimer.cancel();
                mTimer.purge();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                validStatusCode(failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                validStatusCode(url);
                if (url.startsWith("http:") || url.startsWith("https:") || url.startsWith("tbpb:")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        mWebView.setWebChromeClient(new MyWebChomeClient(MainActivity.this));

        fixDirPath();
    }

    // 判断 请求返回值是否请求超时或者请求失败 并发送给handler
    private void validStatusCode(final String url) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Message msg = new Message();
                    HttpURLConnection.setFollowRedirects(false);
                    URL validatedURL = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) validatedURL.openConnection();
                    con.setRequestMethod("HEAD");
                    int responseCode = con.getResponseCode();
                    if (responseCode == 503 | responseCode == 404 || responseCode == 405 || responseCode == 504) {
                        sendMessage(HTTP_ERROR);
                    } else {

                        sendMessage(HTTP_SUCCESS);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }


    // 请求权限后回调信息
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        L.e("是否返回" + requestCode);

    }

    // 请求权限成功 发送给handler
    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        L.e("第一次拒绝后再次申请");
        mPermission = true;
        sendMessage(LOGIN_PERMISSION);
    }

    // 请求权限失败 发送给handler
    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        mPermission = false;

        sendMessage(LOGIN_PERMISSION);
        dialog(2);
    }

    // 请求存储权限成功 调用下载方法
    @PermissionSuccess(requestCode = 101)
    public void download() {
//        downLoadFile(mInfo.getApk_url(), mInfo.getApp_message());
    }

    // 请求存储权限失败 调用dialog方法
    @PermissionFail(requestCode = 101)
    public void downloadFail() {
        dialog(1);
    }

    @PermissionSuccess(requestCode = 102)
    public void doSuccessCamera() {
        if (mCameraOrAlbum) {
            mSourceIntent = ImageUtil.takeBigPicture();
            startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
        } else {
            mSourceIntent = ImageUtil.choosePicture();
            startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
        }

    }

    // 请求权限失败 发送给handler
    @PermissionFail(requestCode = 102)
    public void doFailCamera() {
        dialog(3);
    }


    public void update() {//http://192.168.1.125:8080/diyi/open/ipad/app-release.apk   http://192.168.1.125:8080/diyi/open/ipad/appUpdate.action?act=getVersion
        RequestParams params = new RequestParams("http://116.255.205.195:80/diyi/open/ipad/appUpdate.action?act=getVersion");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                mInfo = gson.fromJson(result, Update.class);
         L.e(mInfo.getApp_version()+"--------------------");
                if (mInfo.getApp_version() != null) {

                    if (getVersion() < Integer.valueOf(mInfo.getApp_version())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PermissionGen.with(MainActivity.this).addRequestCode(101).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).request();
                        } else {
//                            downLoadFile(mInfo.getApk_url(), mInfo.getApp_message());
                        }


                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    protected void dialog(final int type) {
        String message = null;


        if (type == 1) {
            message = "由于系统限制,需要打开获取SD卡读写权限才能继续操作,请同意打开该权限哦";
        } else if (type == 2) {
            message = "由于系统限制,需要获取相应权限才能登录,请同意打开该权限哦";

        } else if (type == 3) {
            message = "由于系统限制,需要获取相应权限才能打开 相册/拍照,请同意打开该权限哦";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setTitle("权限提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (type == 1) {
                    PermissionGen.with(MainActivity.this).addRequestCode(101).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).request();
                } else if (type == 2) {
                    PermissionGen.with(MainActivity.this).addRequestCode(100).permissions(Manifest.permission.READ_PHONE_STATE).request();

                } else if (type == 3) {
                    PermissionGen.with(MainActivity.this).addRequestCode(102).permissions(Manifest.permission.CAMERA).request();

                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUpload = uploadMsg;
        showPopup(mView);

    }


    @Override
    public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

        mUploadMsgForAndroid5 = filePathCallback;
        showPopup(mView);
        return true;
    }


    private void fixDirPath() {
        String path = ImageUtil.getDirPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    public void showPopup(View view) {
        final View contentview = LayoutInflater.from(mContext).inflate(R.layout.popup_photo, null);
        mCamera = (Button) contentview.findViewById(R.id.camera);
        mPhoto = (Button) contentview.findViewById(R.id.photo);
        mCamera.setOnClickListener(this);
        mPhoto.setOnClickListener(this);
        mPpopu = new PopupWindow(contentview, 400, 200, true);
        mPpopu.setContentView(contentview);
        mPpopu.setFocusable(true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPpopu.showAtLocation(view, Gravity.CENTER, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        mPpopu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //设置背景变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPpopu.isShowing()) {
                    mPpopu.dismiss();
                }
                return false;
            }
        });


    }

    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                mPpopu.dismiss();
                mCameraOrAlbum = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PermissionGen.with(MainActivity.this).addRequestCode(102).permissions(Manifest.permission.CAMERA).request();

                } else {
                    mSourceIntent = ImageUtil.takeBigPicture();
                    startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                }

                break;
            case R.id.photo:
                mPpopu.dismiss();
                mCameraOrAlbum = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PermissionGen.with(MainActivity.this).addRequestCode(102).permissions(Manifest.permission.CAMERA).request();

                } else {
                    mSourceIntent = ImageUtil.choosePicture();
                    startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                }

                break;
            case R.id.btn1:
                mWebView.loadUrl("http://creditcard.ecitic.com/h5/shenqing/list2.html");
                break;
            case R.id.btn2:
                mWebView.loadUrl("http://www.spdbccc.com.cn/zh/wap/newwap/card.html");
                break;
            case R.id.btn3:
                mWebView.loadUrl("http://creditcard.ccb.com/cn/creditcard/card_list.html");

                break;
            case R.id.btn4:
                mWebView.loadUrl("https://c.pingan.com/apply/newpublic/new_apply/index.html#cardList");

                break;
            case R.id.btn5:
                mWebView.loadUrl("http://creditcard.ecitic.com/shenqing/");

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (mUpload != null) {
                mUpload.onReceiveValue(null);
            }

            if (mUploadMsgForAndroid5 != null) {         // for android 5.0+
                mUploadMsgForAndroid5.onReceiveValue(null);
            }
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
            case REQUEST_CODE_PICK_IMAGE: {
                try {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if (mUpload == null) {
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {

                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUpload.onReceiveValue(uri);

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsgForAndroid5 == null) {        // for android 5.0+
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMsgForAndroid5.onReceiveValue(new Uri[]{uri});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    void sendMessage(int type) {
        mMessage = new Message();
        switch (type) {
            case TIMEOUT_ERROR:
                mMessage.what = TIMEOUT_ERROR;
                break;
            case HTTP_ERROR:
                mMessage.what = HTTP_ERROR;
                break;
            case HTTP_SUCCESS:
                mMessage.what = HTTP_SUCCESS;
                break;
            case LOGIN_PERMISSION:
                mMessage.what = LOGIN_PERMISSION;
                break;
            case LOGIN_:
                mMessage.what = LOGIN_;
                break;
            case LOGIN_P:
                mMessage.what = LOGIN_P;
                break;

        }
        mHandler.sendMessage(mMessage);
    }
}