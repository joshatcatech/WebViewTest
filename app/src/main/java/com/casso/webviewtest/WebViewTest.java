package com.casso.webviewtest;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.ResultReceiver;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.l7tech.msso.MobileSso;
import com.l7tech.msso.MobileSsoFactory;
import com.l7tech.msso.app.App;
import com.l7tech.msso.service.MssoIntents;


public class WebViewTest extends AppCompatActivity implements GestureDetector.OnGestureListener {


    private GestureDetectorCompat mDetector;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);

        mDetector = new GestureDetectorCompat(this, this);
        App app = (App) getIntent().getExtras().getSerializable(MssoIntents.EXTRA_APP);
        if (app != null) {
            WebView webView = (WebView) findViewById(R.id.webView);
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mDetector.onTouchEvent(event);
                    return false;
                }
            });

            //Show progress Bar
            final ProgressBar progressBar;
            progressBar = (ProgressBar) findViewById(R.id.progressBar);

            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                    }

                    progressBar.setProgress(progress);
                    if (progress == 100) {
                        progressBar.setVisibility(ProgressBar.GONE);
                    }
                }


            });
            app.renderWebView(this, webView, new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(final int resultCode, final Bundle resultData) {
                    WebViewTest.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultData != null) {
                                String message = resultData.getString(MssoIntents.RESULT_ERROR_MESSAGE);
                                Toast.makeText(WebViewTest.this, message, Toast.LENGTH_LONG).show();
                                WebViewTest.this.onBackPressed();
                            }
                        }
                    });
                }


            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        WebView webView = ((WebView) findViewById(R.id.webView));
        switch (item.getItemId()) {
            case R.id.clearHistory:
                webView.clearHistory();
                webView.clearCache(true);
                break;

            case R.id.clearCache:
                clearCookies(webView);
                break;

            case R.id.back:
                webView.goBack();
                break;

            case R.id.refresh:
                webView.reload();
                break;

            case R.id.forward:
                webView.goForward();
                break;

            case R.id.showCookies:
                showCookies(webView);
                break;
        }
        return true;
    }

    public void clearCookies(WebView webView) {

        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(webView.getContext());
        String url = webView.getOriginalUrl();

        CookieManager cookieManager = CookieManager.getInstance();
        String visitedCookie = cookieManager.getCookie(url);

        cookieManager.removeAllCookie();
        Toast.makeText(WebViewTest.this, "Clearing Cookies for " + url, Toast.LENGTH_LONG).show();
    }

    public void showCookies(WebView webView) {

        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(webView.getContext());
        String url = webView.getOriginalUrl();

        CookieManager cookieManager = CookieManager.getInstance();
        String visitedCookie = cookieManager.getCookie(url);

        if (visitedCookie != null) {
            Toast.makeText(WebViewTest.this, visitedCookie, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WebViewTest.this, "None", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
