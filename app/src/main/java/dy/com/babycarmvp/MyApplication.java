package dy.com.babycarmvp;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import org.xutils.x;

/**
 * Created by Administrator on 2017/1/5 0005.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {

        super.onCreate();

        // 搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        // TbsDownloader.needDownload(getApplicationContext(), false);
        x.Ext.init(this);
        x.Ext.setDebug(true);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                Log.e("apptbs", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub

            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.d("apptbs", "onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                Log.d("apptbs", "onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                Log.d("apptbs", "onDownloadProgress:" + i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), cb);

    }
}
