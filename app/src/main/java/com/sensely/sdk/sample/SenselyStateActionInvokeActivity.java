package com.sensely.sdk.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sensely.sdk.CallBackData;
import com.sensely.sdk.SenselySDK;

public class SenselyStateActionInvokeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensely_state_action_invoke);
    }

    public void onCloseClick(View view) {
        finish();

        if (SenselySDK.getConsumerActions() != null) {
            CallBackData callBackData = new CallBackData();
            //callBackData.setResult("Success");
            callBackData.setSampleResult();
            SenselySDK.getConsumerActions().resultOfInvokeCallback(callBackData);
        }
    }

    /**
     * This method called when user click back button. Library get "Fail" result
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (SenselySDK.getConsumerActions() != null) {
            CallBackData callBackData = new CallBackData();
            callBackData.setResult("Fail");
            SenselySDK.getConsumerActions().resultOfInvokeCallback(callBackData);
        }
    }
}
