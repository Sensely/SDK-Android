package com.sensely.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sensely.sdk.CallBackData;
import com.sensely.sdk.SDKLoaderAssessment;
import com.sensely.sdk.SenselyActivity;
import com.sensely.sdk.SenselySDK;
import com.sensely.sdk.presenters.TestingProgramPresenter;
import com.sensely.sdk.views.WelcomeView;
import com.sensely.sdk.model.AccessToken;
import com.sensely.sdk.model.Avatar;
import com.sensely.sdk.model.testingprogram.TestingProgram;
import com.sensely.sdk.net.NetManager;
import com.sensely.sdk.utils.LibUtils;

import java.util.ArrayList;

/*




 */
public class SampleLauncherActivity extends AppCompatActivity
        implements SenselySDK.ICallbackActivity,
        SampleAssessmentAdapter.SampleListner,
        SDKLoaderAssessment.ILoadAssessment,
        WelcomeView {

    private static final String TAG = SampleLauncherActivity.class.getSimpleName();
    private static final int SDK_ACTIVITY_REQ = 1002;
    /**
     * Max allowed duration betwen a next "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 3000;
    private long mLastClickTime = 0;

    SDKLoaderAssessment sdkLoaderAssessment;

    private RecyclerView mRecyclerView;

    private TextView tvResult;
    private EditText login;
    private EditText password;
    private EditText jsonForUserInfo;
    private FrameLayout progressBar;
    private Button signInButton;

    private TestingProgramPresenter testingProgramPresenter;

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
        progressBar = findViewById(R.id.progress_bar);
        signInButton = findViewById(R.id.signin);


        login.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);

        jsonForUserInfo = findViewById(R.id.jsonForUserInfo);
        String userInfo = "{\"userInfo\":{\"gender\":\"F\",\"dob\":\"1980-10-30\"}}";
        jsonForUserInfo.setText(userInfo);

        String configLogin = getString(R.string.login);
        String configPassword = getString(R.string.password);

        if (!TextUtils.isEmpty(configLogin)){
            login.setText(configLogin);
        }

        if (!TextUtils.isEmpty(configPassword)){
            password.setText(configPassword);
        }

        mRecyclerView = findViewById(R.id.sampleAssessmensList);

        SenselySDK senselySDK = SenselySDK.getInstance();
        senselySDK.setCallbackInvokeActivity(this);
        SenselySDK.getConfigurationInstance()
                .setSenselyDomainType(getString(R.string.senselyDomainType));

        sdkLoaderAssessment = new SDKLoaderAssessment(this);

        showWait();

        testingProgramPresenter = new TestingProgramPresenter(NetManager.getInstance(), this);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        testingProgramPresenter.getTestingProgram(deviceId);
    }

    protected void startAssessment() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_DURATION){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, SenselyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AccessToken.ACCESS_TOKEN, NetManager.getInstance().getToken());
        intent.putExtra(SenselyActivity.USER_INFO, jsonForUserInfo.getText().toString());
        startActivityForResult(intent, SDK_ACTIVITY_REQ);
    }

    public void showWait() {
        signInButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void removeWait() {
        signInButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSenselyResolveOnInvoke(CallBackData callBackData) {
        Intent intent = new Intent(this, SenselyStateActionInvokeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SDK_ACTIVITY_REQ && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            tvResult.setText(result);
            tvResult.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
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
            signInButton.setVisibility(View.VISIBLE);
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
                .setAvatar(Avatar.MOLLY);

        sdkLoaderAssessment.getAssessment(userLogin, userPassword);

        showWait();
    }

    @Override
    public void onError(int id, String message) {
        removeWait();
        LibUtils.showMessageDialog(this, "Application Error", message, android.R.string.ok, null);
    }

    @Override
    public void onGetAssessment(ArrayList<String> assessmentIcons, ArrayList<String> assessmentNames) {

        if (assessmentIcons.isEmpty() || assessmentNames.isEmpty()){
            return;
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        ArrayList<String> menuItems = new ArrayList<>();

        for (int i = 0; i < assessmentIcons.size(); i++) {
            menuItems.add(assessmentNames.get(i));
        }

        SampleAssessmentAdapter sampleAssessmentAdapter
                = new SampleAssessmentAdapter(this, menuItems, this);
        mRecyclerView.setAdapter(sampleAssessmentAdapter);
        sampleAssessmentAdapter.notifyDataSetChanged();

        removeWait();
    }

    @Override
    public void onTestingProgramFailure(String appErrorMessage) {
        removeWait();
        LibUtils.setNetworkHost(SenselySDK.getConfigurationInstance()
                .getSenselyDomainType());
        NetManager.getInstance().createRetrofit();
        // Now we can login
    }

    @Override
    public void onTestingProgramReceived(TestingProgram testingProgram) {
        if (testingProgram.getStatus().getResult().equalsIgnoreCase("Success")) {

            if (!TextUtils.isEmpty(testingProgram.getData().getServer())) {
                LibUtils.setNetworkHost(testingProgram.getData().getServer());
                NetManager.getInstance().createRetrofit();
            }

            if (!TextUtils.isEmpty(testingProgram.getData().getUsername()) &&
                    !TextUtils.isEmpty(testingProgram.getData().getPassword())) {

                login.setText(testingProgram.getData().getUsername());
                password.setText(testingProgram.getData().getPassword());

                LibUtils.saveToken(SampleLauncherActivity.this, new AccessToken());

                onSignInClick(null);
            } else if (!TextUtils.isEmpty(testingProgram.getData().getServer())) {
                removeWait();
            }
        } else {
            removeWait();
        }
    }
}
