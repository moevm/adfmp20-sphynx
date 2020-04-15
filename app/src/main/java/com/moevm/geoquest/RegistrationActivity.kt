package com.moevm.geoquest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<TextInputLayout>(R.id.login_input).editText
        val passField = findViewById<TextInputLayout>(R.id.password_input).editText
        val passConfirmField = findViewById<TextInputLayout>(R.id.password_confirmation).editText

        findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            val email = emailField?.text.toString()
            val pass = passField?.text.toString()
            val passConf = passConfirmField?.text.toString()
            Log.d("AUTHORIZATION", "email: $email, pass: $pass, conf:$passConf")
            if (email.isNotBlank() && pass.isNotBlank() && pass == passConf) {
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            val update = UserProfileChangeRequest.Builder()
                                .setDisplayName(email.split("@")[0])
                                .build()
                            user?.updateProfile(update)
                            Log.d("AUTHORIZATION", "createUserWithEmail:success, user: $user")
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.w("AUTHORIZATION", "createUserWithEmail:failure", task.exception)
                            updateUI(null)
                        }
                    }

            } else {
                Log.d("AUTHORIZATION", "Confirmation not equal password")
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d("AUTHORIZATION", "current user is $user")
        if (user != null)
            startActivity(Intent(this, MainActivity::class.java))
    }

}
