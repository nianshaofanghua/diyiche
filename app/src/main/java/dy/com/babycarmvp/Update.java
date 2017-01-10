package dy.com.babycarmvp;

/**
 * Created by Administrator on 2016/11/11.
 */

public class Update {

    /**
     * app_version : 2
     * apk_url : http://116.255.205.195:8888/diyi/open/ipad/app-release.apk
     */

    private String app_version;
    private String apk_url;
private String app_message;

    public String getApp_message() {
        return app_message;
    }

    public void setApp_message(String app_message) {
        this.app_message = app_message;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

//    public String getApk_url() {
//        return apk_url;
//    }
//
//    public void setApk_url(String apk_url) {
//        this.apk_url = apk_url;
//    }
}
