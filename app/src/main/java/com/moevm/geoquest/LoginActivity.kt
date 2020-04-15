package com.moevm.geoquest

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN: Int = 123
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("AUTHORIZATION", "onActivityResult")
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = auth.currentUser
                val manager =
                    getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
                val list = manager.accounts
                if(list.isNotEmpty()){
                    val update = UserProfileChangeRequest.Builder()
                        .setDisplayName(list[0].name.split("@")[0])
                        .build()
                    user?.updateProfile(update)
                }
                updateUI(user)
                // ...
            } else {
                updateUI(null)
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val emailField = findViewById<TextInputLayout>(R.id.login_input).editText
        val passField = findViewById<TextInputLayout>(R.id.password_input).editText

        val singInButton = findViewById<Button>(R.id.sign_in_button)
        singInButton.setOnClickListener {
            val email = emailField?.text.toString()
            val pass = passField?.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("AUTHORIZATION", "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("AUTHORIZATION", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }

        val signUpButton = findViewById<TextView>(R.id.sign_up_button)
        signUpButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        val singUnWithGoogleButton = findViewById<Button>(R.id.sign_in_google_button)
        singUnWithGoogleButton.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d("AUTHORIZATION", "current user is $user")
        if (user != null)
            startActivity(Intent(this, MainActivity::class.java))
    }

}
