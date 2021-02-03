package com.volvadvit.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.volvadvit.kotlinmessenger.common.*
import com.volvadvit.kotlinmessenger.model.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity: AppCompatActivity() {

    private var userPhotoUri: Uri? = "".toUri()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_select_photo_btn.setOnClickListener {
            selectImageToUpload()
        }
        register_select_photo_view.setOnClickListener {
            selectImageToUpload()
        }

        register_btn.setOnClickListener{
            validateRegisterData()
        }

        register_clickable_text.setOnClickListener {
            startActivity(
                    Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }

    private fun selectImageToUpload() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)   // invoke method onActivityResult
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            userPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, userPhotoUri)    // map of pixels
            register_select_photo_view.setImageBitmap(bitmap)
            register_select_photo_btn.visibility = View.INVISIBLE
        }
    }

    private fun validateRegisterData() {
        val email = register_editText_email.text.toString()
        val password = register_editText_password.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
           mAuthRegisterUser(email, password)
        } else {
            showToast("Please, enter email and password")
        }
    }

    private fun mAuthRegisterUser(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.d("Register", "create User With Email:success ${mAuth.currentUser?.uid}")
                } else {
                    Log.d("Register", it.exception!!.message!!)
                    showToast("Authentication fail: ${it.exception!!.message}")
                }

                uploadPhotoToFirebase()
            }
    }

    private fun saveUserToDatabase(url: String) {
        val user = User(currentUserUid ?: "", register_editText_username.text.toString(), url)
        mRef.child("users").child(currentUserUid!!)
            .setValue(user)

    }

    private fun uploadPhotoToFirebase() {
        if (userPhotoUri != null) {
            mStorage.child("images").child("$currentUserUid").putFile(userPhotoUri!!)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener {
                        val photoUrl = it.toString()
                        saveUserToDatabase(photoUrl)
                    }
                }
                .addOnFailureListener {
                    showToast("Fail upload photo: ${it.message!!}")
                    Log.d("Register", it.message!!)
                }
        } else {
            Log.d("Register", "Photo uri is null")
            showToast("Error: Upload photo again")
        }
    }
}