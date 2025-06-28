package com.tristanmcraven.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.tristanmcraven.expensetracker.databinding.ActivityRouterBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RouterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_router)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = (application as ExpenseTrackerApp).db
        val firstLaunch = runBlocking { db.settingsDao().getFirstLaunch() ?: true }
        val fingerprintRequired = runBlocking { db.settingsDao().isFingerprintRequired() ?: false }


        if (fingerprintRequired) {
            val executor = ContextCompat.getMainExecutor(this)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate")
                .setNegativeButtonText("Cancel")
                .build()
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(applicationContext,
                            "$errString", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)

                        val next = if (firstLaunch) StartActivity::class.java else MainActivity::class.java
                        startActivity(Intent(this@RouterActivity, next))
                        finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
//                        Toast.makeText(applicationContext, "Authentication failed",
//                            Toast.LENGTH_SHORT)
//                            .show() // redundant
                    }
                })

            biometricPrompt.authenticate(promptInfo)
            findViewById<MaterialButton>(R.id.buttonTryAgain).setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
        }
        else {
            val next = if (firstLaunch) StartActivity::class.java else MainActivity::class.java
            startActivity(Intent(this@RouterActivity, next))
            finish()
        }

    }
}