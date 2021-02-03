package com.volvadvit.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.volvadvit.kotlinmessenger.common.currentUserUid
import com.volvadvit.kotlinmessenger.common.mAuth
import com.volvadvit.kotlinmessenger.common.showToast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_btn.setOnClickListener {
            validateLoginData()
        }
        login_clickable_text.setOnClickListener {
            startActivity(
                    Intent(this, RegisterActivity::class.java)
            )
            finish()
        }
    }

    private fun validateLoginData() {
        val email = login_editText_email.text.toString()
        val password = login_editText_password.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuthLogInUser(email, password)
        } else {
            showToast("Invalid email or password")
        }
    }

    private fun mAuthLogInUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Success $currentUserUid")
                } else {
                    Log.d("Login", it.exception!!.message!!)
                    showToast("Authentication fail: ${it.exception!!.message!!}")
                }
            }
    }
}