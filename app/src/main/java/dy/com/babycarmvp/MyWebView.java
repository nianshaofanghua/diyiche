package dy.com.babycarmvp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * js交互类
 * Created by ${syj} on 2016/12/21.
 */

public class MyWebView extends WebView{
    private Paint paint1;
    private Paint paint2;
    private float m_radius;
    private int width;
    private int height;
    private int x;
    private int y;

    public MyWebView(Context context) {
        super(context);
        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setAntiAlias(true);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        paint2 = new Paint();
        paint2.setXfermode(null);
        initSetting(context);
    }


    public void initSetting(Context context){
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);//关键点

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(false); // 设置显示缩放按钮
        webSettings.setSupportZoom(false); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(context.getCacheDir().getPath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        this.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        this.setHorizontalScrollBarEnabled(false);
        this.setHorizontalFadingEdgeEnabled(false);
        this.setVerticalFadingEdgeEnabled(false);


        /** 41032919930818961x
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
         * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
         */  DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
    }

    public void setRadius(int w, int h, float radius) {
        m_radius = radius;
        width = w;
        height = h;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }

    private void drawLeftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x, m_radius);
        path.lineTo(x, y);
        path.lineTo(m_radius, y);
        path.arcTo(new RectF(x, y, x + m_radius * 2, y + m_radius * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint1);

    }

    private void drawLeftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x, y + height - m_radius);
        path.lineTo(x, y + height);
        path.lineTo(x + m_radius, y + height);
        path.arcTo(new RectF(x, y + height - m_radius * 2, x + m_radius * 2, y + height), 90, 90);
        path.close();
        canvas.drawPath(path, paint1);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x + width - m_radius, y + height);
        path.lineTo(x + width, y + height);
        path.lineTo(x + width, y + height - m_radius);
        path.arcTo(new RectF(x + width - m_radius * 2, y + height - m_radius * 2, x + width, y + height), 0, 90);
        path.close();
        canvas.drawPath(path, paint1);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x + width, y + m_radius);
        path.lineTo(x + width, y);
        path.lineTo(x + width - m_radius, y);
        path.arcTo(new RectF(x + width - m_radius * 2, y, x + width, y + m_radius * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint1);
    }
}
