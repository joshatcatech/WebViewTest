/*
 * Created by awitrisna on 2013-11-15.
 * Copyright (c) 2013 CA Technologies. All rights reserved.
 */

package com.casso.webviewtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.l7tech.msso.EnterpriseApp;
import com.l7tech.msso.MobileSso;
import com.l7tech.msso.MobileSsoFactory;
import com.l7tech.msso.service.MssoIntents;


public class EnterpriseBrowserLauncher extends Activity {

    public MobileSso mobileSso() {
        //Initialize the MobileSso with the configuration defined under /assets/msso_config.json
        MobileSso mobileSso = MobileSsoFactory.getInstance(this);
        return mobileSso;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mobileSso();

        EnterpriseApp.getInstance().processEnterpriseApp(EnterpriseBrowserLauncher.this, new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode != MssoIntents.RESULT_CODE_SUCCESS) {
                    String message = resultData.getString(MssoIntents.RESULT_ERROR_MESSAGE);
                    if (message == null) {
                        message = "<Unknown error>";
                    }
                    showMessage(message, Toast.LENGTH_LONG);
                }
            }
        }, EnterpriseWebViewClient.class);


    }

    public void showMessage(final String message, final int toastLength) {
        if (message.toLowerCase().contains("jwt")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("JWT Error");
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Add your code for the button here.
                }
            });
            alertDialog.show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EnterpriseBrowserLauncher.this, message, toastLength).show();
                }
            });
        }
    }
}