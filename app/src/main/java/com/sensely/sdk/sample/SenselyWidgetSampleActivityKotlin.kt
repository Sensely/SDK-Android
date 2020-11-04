package com.sensely.sdk.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.sensely.sdk.api.SenselyWidget

import com.sensely.sdk.utils.ExtendedDataHolder
import org.json.JSONObject

class SenselyWidgetSampleActivityKotlin : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var procedureIdEditText: EditText
    private lateinit var languageEditText: EditText
    private lateinit var themeEditText: EditText
    private lateinit var userInfoEditText: EditText
    private lateinit var resultsView: View
    private lateinit var resultsTextView: TextView
    private lateinit var resultsTabLayout: TabLayout
    private lateinit var progressBar: FrameLayout

    private var simplifiedResults: String = ""
    private var fullResults: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensely_widget_sample)

        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        procedureIdEditText = findViewById(R.id.procedureIdEditText)
        languageEditText = findViewById(R.id.languageEditText)
        themeEditText = findViewById(R.id.themeEditText)
        userInfoEditText = findViewById(R.id.userInfoEditText)

        resultsView = findViewById(R.id.resultsView)
        resultsTextView = findViewById(R.id.resultsTextView)

        resultsTabLayout = findViewById(R.id.resultsTabLayout)
        resultsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { switchTabContent(tab) }
            override fun onTabReselected(tab: TabLayout.Tab?) { switchTabContent(tab) }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        progressBar = findViewById(R.id.progressBar)

        hideProgressBar()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SDK_ACTIVITY_REQ && resultCode == Activity.RESULT_OK) {
            val extras = ExtendedDataHolder.getInstance()
            if (extras.hasExtra("result")) {
                fullResults = extras.getExtra("result") as String
                extras.removeExtra("result")

                try {
                    val jsonResponse = JSONObject(fullResults)
                    simplifiedResults = jsonResponse.getString("conversationOutput")
                } catch (e: Exception) { }

                showResultsView()
            }
        }
    }

    private fun switchTabContent(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> resultsTextView.text = simplifiedResults
            1 -> resultsTextView.text = fullResults
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
        if (resultsView.visibility == View.VISIBLE) {
            hideResultsView()
        } else {
            super.onBackPressed()
        }
    }

    private fun showResultsView() {
        resultsView.visibility = View.VISIBLE
        resultsTabLayout.selectTab(resultsTabLayout.getTabAt(0))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun hideResultsView() {
        resultsView.visibility = View.GONE
        resultsTextView.text = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun startSenselyWidget(view: View?) {
        showProgressBar()
        SenselyWidget.initialize(
                this,
                SDK_ACTIVITY_REQ,
                loginEditText.text.toString().trim { it <= ' ' },
                passwordEditText.text.toString().trim { it <= ' ' },
                procedureIdEditText.text.toString().trim { it <= ' ' },
                languageEditText.text.toString().trim { it <= ' ' },
                themeEditText.text.toString().trim { it <= ' ' },
                userInfoEditText.text.toString().trim { it <= ' ' },
                this::widgetInitializationComplete,
                this::widgetInitializationError
        )

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
        }

        Toast.makeText(this@SenselyWidgetSampleActivityKotlin, errorMessage,
                Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val SDK_ACTIVITY_REQ = 1234
    }
}