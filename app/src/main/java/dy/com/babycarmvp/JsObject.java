package dy.com.babycarmvp;

import android.webkit.JavascriptInterface;

/**
 js交互类
 * Created by ${syj} on 2016/11/24.
 */

class JsObject {
    private CallBack mCallBack;




    @JavascriptInterface
    public void isLogin() {
        mCallBack.callback("lay");

    }

    @JavascriptInterface
    public void turnDown() {
        System.exit(0);
    }

    void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    interface CallBack {
         void callback(String str);


    }
}
