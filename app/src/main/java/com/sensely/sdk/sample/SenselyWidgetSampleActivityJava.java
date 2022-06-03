package com.sensely.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.sensely.sdk.api.SenselyWidget;
import com.sensely.sdk.sample.databinding.ActivitySenselyWidgetSampleBinding;
import com.sensely.sdk.utils.ExtendedDataHolder;

import org.json.JSONObject;

import kotlin.Unit;

public class SenselyWidgetSampleActivityJava extends AppCompatActivity {

    private static final int SDK_ACTIVITY_REQ = 1234;

    private String simplifiedResults;
    private String fullResults;

    private ActivitySenselyWidgetSampleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySenselyWidgetSampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.resultsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { switchTabContent(tab); }
            @Override public void onTabReselected(TabLayout.Tab tab) { switchTabContent(tab); }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
        });

        hideProgressBar();
    }

    public void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
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
                binding.resultsTextView.setText(simplifiedResults);
                break;
            case 1:
                binding.resultsTextView.setText(fullResults);
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
        if (binding.resultsView.getVisibility() == View.VISIBLE) {
            hideResultsView();
        } else {
            super.onBackPressed();
        }
    }

    private void showResultsView() {
        binding.resultsView.setVisibility(View.VISIBLE);
        binding.resultsTabLayout.selectTab(binding.resultsTabLayout.getTabAt(0));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void hideResultsView() {
        binding.resultsView.setVisibility(View.GONE);
        binding.resultsTextView.setText("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public void startSenselyWidget(View view) {
        showProgressBar();

        SenselyWidget.INSTANCE.initialize(
                this,
                binding.loginEditText.getText().toString().trim(),
                binding.passwordEditText.getText().toString().trim(),
                binding.procedureIdEditText.getText().toString().trim(),
                binding.languageEditText.getText().toString().trim(),
                binding.userInfoEditText.getText().toString().trim(),
                binding.themeEditText.getText().toString().trim(),
                binding.defaultAudioText.getText().toString().trim(),
                SDK_ACTIVITY_REQ,
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
            case SenselyWidget.UNKNOWN_ERROR:
                errorMessage = "Error";
                break;
        }

        Toast.makeText(SenselyWidgetSampleActivityJava.this, errorMessage,
                Toast.LENGTH_LONG).show();

        return Unit.INSTANCE;
    }
}