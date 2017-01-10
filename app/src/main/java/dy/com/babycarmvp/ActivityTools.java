package dy.com.babycarmvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.sdk.WebView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.UUID;

/**
 activity基类
 * Created by ${syj} on 2016/11/17.
 */

public class ActivityTools extends Activity {
    private long clickTime = 0;
    private WebView mMyWebView;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ProgressBar mProgressBar;
    private View mView;
    private TextView mMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }

    // 请求权限 读取手机信息 如果已经有了权限直接请求uuid



    public void setWebView(WebView webView) {
        mMyWebView = webView;
    }

    public void setView(int view) {
        mView = findViewById(view);
    }

    // 获取uuid
    public String getuuid() {
        String tmDevice;
        String tmSerial;
        String androidId;
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // String DEVICE_ID = tm.getDeviceId();
        // DEVICE_ID += "-" + tm.getSimSerialNumber();
        // DEVICE_ID += "-" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) );//| tmSerial.hashCode()
        return deviceUuid.toString();
    }

    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mMyWebView.canGoBack()) {
            mMyWebView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }

        return false;
    }

    //退出程序
    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
                    Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            System.exit(0);
        }
    }// diyipingu   diyipinggu



    // 下载apk
    public void downLoadFile(String url, final String updateMessage) {
        RequestParams params = new RequestParams(url);
        params.setAutoRename(true);//断点下载

        params.setSaveFilePath(getResources().getString(R.string.path));
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

                showPopupwindow(mView);
                if (updateMessage != null) {
                    mMessage.setText(updateMessage);
                }

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {


                mProgressBar.setMax((int) total);
                mProgressBar.setProgress((int) current);
            }

            @Override
            public void onSuccess(File result) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                startActivity(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mPopupWindow.dismiss();
            }
        });
    }


    // 下载apk进度界面
    public void showPopupwindow(View view) {
        final View contentview = LayoutInflater.from(mContext).inflate(R.layout.popup, null);
        mProgressBar = (ProgressBar) contentview.findViewById(R.id.pro);
        mMessage = (TextView) contentview.findViewById(R.id.message);
        mPopupWindow = new PopupWindow(contentview, 600, 400, true);
        mPopupWindow.setContentView(contentview);
        mPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //设置背景变暗

            }
        });
    }

    //得到版本号
    public int getVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
