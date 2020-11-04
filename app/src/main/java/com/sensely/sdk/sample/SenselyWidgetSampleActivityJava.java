package com.sensely.sdk.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import com.sensely.sdk.api.SenselyWidget;
import com.sensely.sdk.utils.ExtendedDataHolder;

import org.json.JSONObject;

import kotlin.Unit;

public class SenselyWidgetSampleActivityJava extends AppCompatActivity {

    private static final int SDK_ACTIVITY_REQ = 1234;

    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText procedureIdEditText;
    private EditText languageEditText;
    private EditText themeEditText;
    private EditText userInfoEditText;
    private View resultsView;
    private TextView resultsTextView;
    private TabLayout resultsTabLayout;
    private FrameLayout progressBar;

    private String simplifiedResults;
    private String fullResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensely_widget_sample);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        procedureIdEditText = findViewById(R.id.procedureIdEditText);
        languageEditText = findViewById(R.id.languageEditText);
        themeEditText = findViewById(R.id.themeEditText);
        userInfoEditText = findViewById(R.id.userInfoEditText);

        resultsView = findViewById(R.id.resultsView);
        resultsTextView = findViewById(R.id.resultsTextView);

        resultsTabLayout = findViewById(R.id.resultsTabLayout);
        resultsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { switchTabContent(tab); }
            @Override public void onTabReselected(TabLayout.Tab tab) { switchTabContent(tab); }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
        });

        progressBar = findViewById(R.id.progressBar);

        hideProgressBar();
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SDK_ACTIVITY_REQ && resultCode == RESULT_OK) {
            ExtendedDataHolder extras = ExtendedDataHolder.getInstance();

            if (extras.hasExtra("result")) {
                fullResults = (String) extras.getExtra("result");
                extras.removeExtra("result");

                try {
                    JSONObject jsonResponse = new JSONObject(fullResults);
                    simplifiedResults = jsonResponse.getString("conversationOutput");
                } catch (Exception e) { }

                showResultsView();
            }
        }
    }

    private void switchTabContent(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                resultsTextView.setText(simplifiedResults);
                break;
            case 1:
                resultsTextView.setText(fullResults);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideResultsView();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (resultsView.getVisibility() == View.VISIBLE) {
            hideResultsView();
        } else {
            super.onBackPressed();
        }
    }

    private void showResultsView() {
        resultsView.setVisibility(View.VISIBLE);
        resultsTabLayout.selectTab(resultsTabLayout.getTabAt(0));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void hideResultsView() {
        resultsView.setVisibility(View.GONE);
        resultsTextView.setText("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public void startSenselyWidget(View view) {
        showProgressBar();

        SenselyWidget.INSTANCE.initialize(
                this,
                SDK_ACTIVITY_REQ,
                loginEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim(),
                procedureIdEditText.getText().toString().trim(),
                languageEditText.getText().toString().trim(),
                themeEditText.getText().toString().trim(),
                userInfoEditText.getText().toString().trim(),
                this::widgetInitializationComplete,
                this::widgetInitializationError
        );
    }

    private Unit widgetInitializationComplete() {
        hideProgressBar();

        return Unit.INSTANCE;
    }

    private Unit widgetInitializationError(int errorCode) {
        hideProgressBar();

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
            case SenselyWidget.SESSION_EXPIRED_ERROR:
                errorMessage = "Session has expired";
                break;
        }

        Toast.makeText(SenselyWidgetSampleActivityJava.this, errorMessage,
                Toast.LENGTH_LONG).show();

        return Unit.INSTANCE;
    }
}
