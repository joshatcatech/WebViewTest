package com.casso.webviewtest;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.l7tech.msso.MobileSso;
import com.l7tech.msso.MobileSsoFactory;
import com.l7tech.msso.app.App;
import com.l7tech.msso.service.MssoClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.Header;


import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import java.util.Map;
import java.util.HashMap;


public class EnterpriseWebViewClient extends App {

    private static final String TAG = EnterpriseWebViewClient.class.getCanonicalName();

    private void sendError(ResultReceiver handler, int code, String message) {
        if (handler != null) {
            Bundle result = new Bundle();
            result.putString("com.l7tech.msso.service.result.errorMessage", message);
            handler.send(code, result);
        }
    }
    public EnterpriseWebViewClient(JSONObject app) {
        super(app);
    }

    @Override
    protected WebViewClient getWebViewClient(Context context, ResultReceiver errorHandler) {

        final WebViewClient webViewClient = super.getWebViewClient(context, errorHandler);
        final MobileSso mobileSso = MobileSsoFactory.getInstance();


        return new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(view.getContext());

                CookieManager cookieManager = CookieManager.getInstance();

                String allCookies = cookieManager.getCookie(url);
                String[] cookies = allCookies.split(";");

                for (int i = 0; i < cookies.length; i++) {

                    if (cookies[i].contains("caEnterpriseBrowserDemoCookie")) {
                        String[] cookieParts = cookies[i].split("=");
                        String cookieValue = cookieParts[1];

                        if (cookieValue.isEmpty() == false) {
                            if (cookieValue.equals("logged_out")) {
                                if (mobileSso.isLogin() == true) {
                                    doServerLogout();
                                    break;
                                }
                            }
                        }
                    }
                }

            }

            // Network operations performed asynchronously
            private void doServerLogout() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            mobileSso.logout(true);
                        } catch (Exception e) {
                            String msg = "Server Logout Failed: " + e.getMessage();
                        }
                        return null;
                    }
                }.execute((Void) null);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return webViewClient.shouldInterceptRequest(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webViewClient.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                webViewClient.onReceivedSslError(view, handler, error);
            }


        };

    }
}
