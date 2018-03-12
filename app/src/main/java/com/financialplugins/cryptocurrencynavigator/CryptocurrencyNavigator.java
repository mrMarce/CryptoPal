package com.financialplugins.cryptocurrencynavigator;

import android.app.Application;
import android.content.Context;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;



@ReportsCrashes(mailTo = "nickjumarc@gmail.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)

public class CryptocurrencyNavigator extends Application {
    private static final String TAG = "CryptocurrencyNavigator";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
//        ACRA.init(this);
    }


}
