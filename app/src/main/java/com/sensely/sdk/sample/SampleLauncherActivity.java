package com.sensely.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

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
import android.widget.TextView;
import android.widget.Toast;

import com.sensely.sdk.api.CallBackData;
import com.sensely.sdk.api.SDKLoaderAssessment;
import com.sensely.sdk.api.SenselyActivity;
import com.sensely.sdk.api.SenselySDK;
import com.sensely.sdk.api.SenselyWidget;
import com.sensely.sdk.model.AccessToken;
import com.sensely.sdk.utils.ExtendedDataHolder;

import java.util.ArrayList;

/**
 *
 * @author Sensely 2019
 *
 *
 *
 * Sample Using Sensely SDK
 *
 *
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
    private static final int MAX_CLICK_DURATION = 3000;
    private long mLastClickTime = 0;

    SDKLoaderAssessment sdkLoaderAssessment;

    private RecyclerView mRecyclerView;

    private TextView tvResult;
    private EditText login;
    private EditText password;
    private EditText language;
    private EditText jsonForUserInfo;
    private EditText procedureId;
    private FrameLayout progressBar;
    private Button signInButton;
    private Button startSenselyWidgetButton;
    private CheckBox anonymousMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_launcher);

        tvResult = findViewById(R.id.tv_result);
        tvResult.setMovementMethod(new ScrollingMovementMethod());
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        language = findViewById(R.id.language);
        procedureId = findViewById(R.id.procedure_id);
        progressBar = findViewById(R.id.progress_bar);
        signInButton = findViewById(R.id.signin);
        startSenselyWidgetButton = findViewById(R.id.start_sensely_widget);

        anonymousMode = findViewById(R.id.anonymousMode);

        jsonForUserInfo = findViewById(R.id.jsonForUserInfo);
        String userInfo = "{\"userInfo\":{\"gender\":\"F\",\"dob\":\"1980-10-30\"}}";
//        String userInfo = "{\"userInfo\":{\"dob\":\"2019-01-01\",\"favorite_color\":\"玫瑰\"}}";
        jsonForUserInfo.setText(userInfo);

        String configLogin = getString(R.string.login);
        String configPassword = getString(R.string.password);
        String configProcedureId = getString(R.string.procedureId);

        if (!TextUtils.isEmpty(configLogin)) {
            login.setText(configLogin);
        }

        if (!TextUtils.isEmpty(configPassword)) {
            password.setText(configPassword);
        }

        if (!TextUtils.isEmpty(configProcedureId)) {
            procedureId.setText(configProcedureId);
        }

        mRecyclerView = findViewById(R.id.sampleAssessmensList);

        SenselySDK senselySDK = SenselySDK.getInstance();
        senselySDK.setCallbackInvokeActivity(this);
        SenselySDK.getConfigurationInstance()
                .setSenselyDomainType(getString(R.string.senselyDomainType));

        sdkLoaderAssessment = new SDKLoaderAssessment(this);

        removeWait();
    }

    /**
     *
     * Start Assessment by index in Assessment list
     * See description SenselyActivity for detail
     *
     * @param indexAssessment
     *
     */
    protected void startAssessment(int indexAssessment) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_DURATION) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, SenselyActivity.class);
        intent.putExtra(AccessToken.ACCESS_TOKEN, sdkLoaderAssessment.getToken());
        intent.putExtra(SenselyActivity.USER_INFO, jsonForUserInfo.getText().toString());
        intent.putExtra(SenselyActivity.ASSESSMENT_INDEX, indexAssessment);
        intent.putExtra(SenselyActivity.ANONYMOUS_MODE, anonymousMode.isChecked());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, SDK_ACTIVITY_REQ);
    }

    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     *
     * Callback from Assessment
     *
     *
     * @param callBackData
     */
    @Override
    public void onSenselyResolveOnInvoke(CallBackData callBackData) {
        Intent intent = new Intent(this, SenselyStateActionInvokeActivity.class);
        startActivity(intent);
    }

    /**
     *
     * Result after close Assessment
     *
     * @param requestCode id involved. SDK_ACTIVITY_REQ
     * @param resultCode RESULT_OK or cancel
     * @param data Intent with result if resultCode is RESULT_OK
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SDK_ACTIVITY_REQ && resultCode == RESULT_OK) {
            ExtendedDataHolder extras = ExtendedDataHolder.getInstance();

            if (extras.hasExtra("result")) {
                String result = (String) extras.getExtra("result");
                extras.removeExtra("result");

                tvResult.setText(result);
            }

            tvResult.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            language.setVisibility(View.GONE);
            procedureId.setVisibility(View.GONE);
            jsonForUserInfo.setVisibility(View.GONE);
            anonymousMode.setVisibility(View.GONE);
            startSenselyWidgetButton.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (tvResult.getVisibility() == View.VISIBLE) {
            tvResult.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            language.setVisibility(View.VISIBLE);
            procedureId.setVisibility(View.VISIBLE);
            jsonForUserInfo.setVisibility(View.VISIBLE);
            anonymousMode.setVisibility(View.VISIBLE);
            startSenselyWidgetButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(int index) {
        startAssessment(index);
    }

    public void onSignInClick(View view) {
        String userLogin = login.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userLanguage = language.getText().toString().trim();

        if (TextUtils.isEmpty(userLogin)) {
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            return;
        }

        sdkLoaderAssessment.getAssessment(userLogin, userPassword, userLanguage);

        showWait();
    }

    /**
     *
     * Error callback if load assessment from Server was unsuccessful
     *
     *
     * @param id        Error id.
     *                  SDKLoaderAssessment.INVALID_LOGIN_PASSWORD_ERROR
     *                  SDKLoaderAssessment.GET_ASSESSMENT_ERROR
     *
     * @param message   Error message
     */
    @Override
    public void onError(int id, String message) {
        removeWait();
        Toast.makeText(this, "Application Error : " + message,
                Toast.LENGTH_LONG).show();
    }

    /**
     *
     * Callback with List of assessmentIcons and assessmentNames
     *
     * @param assessmentIcons
     * @param assessmentNames
     */
    @Override
    public void onGetAssessment(ArrayList<String> assessmentIcons, ArrayList<String> assessmentNames) {

        if (assessmentIcons.isEmpty() || assessmentNames.isEmpty()) {
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

    public void startSenselyWidget(View view) {
        showWait();

        SenselyWidget.initialize(
                login.getText().toString().trim(),
                password.getText().toString().trim(),
                language.getText().toString().trim(),
                procedureId.getText().toString().trim(),
                anonymousMode.isChecked(),
                jsonForUserInfo.getText().toString(),
                this,
                SDK_ACTIVITY_REQ,
                new SenselyWidget.ISenselyWidgetListener() {
                    @Override
                    public void onError(int errorCode) {
                        removeWait();

                        CharSequence errorMessage = "Unknown error";

                        switch (errorCode) {
                            case SenselyWidget.INVALID_LOGIN_PASSWORD_ERROR:
                                errorMessage = "Incorrect login or password";
                                break;
                            case SenselyWidget.LOADING_ASSESSMENT_LIST_ERROR:
                                errorMessage = "Error while loading the assessment list";
                                break;
                            case SenselyWidget.EMPTY_ASSESSMENT_LIST_ERROR:
                                errorMessage = "There are no assessments for this user";
                                break;
                            case SenselyWidget.INVALID_PROCEDURE_ID_ERROR:
                                errorMessage = "Incorrect Procedure Id";
                                break;
                        }

                        Toast.makeText(SampleLauncherActivity.this, errorMessage,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        removeWait();
                    }
                }
        );
    }
}
