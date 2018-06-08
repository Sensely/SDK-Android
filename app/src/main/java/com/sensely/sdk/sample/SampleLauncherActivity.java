package com.sensely.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sensely.sdk.CallBackData;
import com.sensely.sdk.SDKLoaderAssessment;
import com.sensely.sdk.SenselyActivity;
import com.sensely.sdk.SenselySDK;

import java.util.ArrayList;

/*




 */
public class SampleLauncherActivity extends AppCompatActivity
        implements SenselySDK.ICallbackActivity,
        SampleAssessmentAdapter.SampleListner,
        SDKLoaderAssessment.ILoadAssessment{

    private static final String TAG = SampleLauncherActivity.class.getSimpleName();
    private static final int SDK_ACTIVITY_REQ = 1002;
    /**
     * Max allowed duration betwen a next "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 5000;
    private long mLastClickTime = 0;

    SDKLoaderAssessment sdkLoaderAssessment;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;


    private TextView tvResult;
    private EditText login;
    private EditText password;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("nativestatemachine");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_launcher);

        tvResult = findViewById(R.id.tv_result);
        tvResult.setMovementMethod(new ScrollingMovementMethod());
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);

        mRecyclerView = findViewById(R.id.sampleAssessmensList);

        mProgressBar = findViewById(R.id.progress_net_bar);
        mProgressBar.setVisibility(View.GONE);

        SenselySDK senselySDK = SenselySDK.getInstance();
        senselySDK.setCallbackInvokeActivity(this);


        sdkLoaderAssessment = new SDKLoaderAssessment(this);

    }

    protected void startAssessment() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_DURATION){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, SenselyActivity.class);
        startActivityForResult(intent, SDK_ACTIVITY_REQ);
    }

    @Override
    public void onSenselyResolveOnInvoke(CallBackData callBackData) {
        Intent intent = new Intent(this, SenselyStateActionInvokeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SDK_ACTIVITY_REQ) {
            String result = data.getStringExtra("result");
            tvResult.setText(result);
            tvResult.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            findViewById(R.id.signin).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (tvResult.getVisibility() == View.VISIBLE){
            tvResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            findViewById(R.id.signin).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(int index) {
        SenselySDK.getConfigurationInstance()
                .setAssessmentIdx(index);
        startAssessment();
    }

    public void onSignInClick(View view) {
        String userLogin = login.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userLogin) || TextUtils.isEmpty(userPassword)){
            return;
        }

        // For Access to next step
        SenselySDK.getConfigurationInstance()
                .setUser(userLogin)
                .setPassword(userPassword)
                .setAvatarIndex(0);

        sdkLoaderAssessment.getAssessment(userLogin, userPassword);
    }

    @Override
    public void onError(int id, String message) {

    }

    @Override
    public void onGetAssessment(ArrayList<String> assessmentIcons, ArrayList<String> assessmentNames) {

        if (assessmentIcons.isEmpty() || assessmentNames.isEmpty()){
            return;
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        ArrayList<String> menuItems = new ArrayList<>();

        for (int i = 0; i < assessmentIcons.size(); i++) {
            menuItems.add(assessmentNames.get(i));
        }

        SampleAssessmentAdapter sampleAssessmentAdapter
                = new SampleAssessmentAdapter(this, menuItems, this);
        mRecyclerView.setAdapter(sampleAssessmentAdapter);
        sampleAssessmentAdapter.notifyDataSetChanged();

    }
}
