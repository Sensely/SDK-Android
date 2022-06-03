package com.sensely.sdk.sample

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.sensely.sdk.api.SenselyWidget
import com.sensely.sdk.sample.databinding.ActivitySenselyWidgetSampleBinding

class SenselyWidgetSampleActivityKotlin : AppCompatActivity() {

    private var simplifiedResults: String = ""
    private var fullResults: String = ""

    private lateinit var binding: ActivitySenselyWidgetSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySenselyWidgetSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resultsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { switchTabContent(tab) }
            override fun onTabReselected(tab: TabLayout.Tab?) { switchTabContent(tab) }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        hideProgressBar()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun switchTabContent(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> binding.resultsTextView.text = simplifiedResults
            1 -> binding.resultsTextView.text = fullResults
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            hideResultsView()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.resultsView.visibility == View.VISIBLE) {
            hideResultsView()
        } else {
            super.onBackPressed()
        }
    }

    private fun showResultsView() {
        binding.resultsView.visibility = View.VISIBLE
        binding.resultsTabLayout.selectTab(binding.resultsTabLayout.getTabAt(0))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun hideResultsView() {
        binding.resultsView.visibility = View.GONE
        binding.resultsTextView.text = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun startSenselyWidget(view: View?) {
        showProgressBar()

        SenselyWidget.initialize(
            context = this,
            launcher = senselyActivityLauncher,
            userName = binding.loginEditText.text.toString().trim { it <= ' ' },
            password = binding.passwordEditText.text.toString().trim { it <= ' ' },
            procedureId = binding.procedureIdEditText.text.toString().trim { it <= ' ' },
            language = binding.languageEditText.text.toString().trim { it <= ' ' },
            conversationData = binding.userInfoEditText.text.toString().trim { it <= ' ' },
            theme = binding.themeEditText.text.toString().trim { it <= ' ' },
            defaultAudio = binding.defaultAudioText.text.toString().trim { it <= ' ' },
            onLoadComplete = this::widgetInitializationComplete,
            onLoadError = this::widgetInitializationError
        )
    }

    private val senselyActivityLauncher = registerForActivityResult(SenselyWidget.SenselyActivityContract()) { result ->
        result?.let {
            this.fullResults = it.fullResults
            this.simplifiedResults = it.simplifiedResults
        }

        showResultsView()
    }

    private fun widgetInitializationComplete() {
        hideProgressBar()
    }

    private fun widgetInitializationError(errorCode: Int) {
        hideProgressBar()

        var errorMessage: CharSequence = "Unknown error"

        when (errorCode) {
            SenselyWidget.INVALID_LOGIN_PASSWORD_ERROR -> errorMessage = "Incorrect login or password"
            SenselyWidget.LOADING_ASSESSMENT_LIST_ERROR -> errorMessage = "Error while loading the assessment list"
            SenselyWidget.EMPTY_ASSESSMENT_LIST_ERROR -> errorMessage = "There are no assessments for this user"
            SenselyWidget.INVALID_PROCEDURE_ID_ERROR -> errorMessage = "Incorrect Procedure Id"
            SenselyWidget.SESSION_EXPIRED_ERROR -> errorMessage = "Session has expired"
            SenselyWidget.UNKNOWN_ERROR -> errorMessage = "Error"
        }

        Toast.makeText(this@SenselyWidgetSampleActivityKotlin, errorMessage,
            Toast.LENGTH_LONG).show()
    }
}