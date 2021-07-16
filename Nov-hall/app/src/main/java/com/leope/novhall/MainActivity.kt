package com.leope.novhall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    companion object {
        const val FIREBASE_INCORRECT_EMAIL_OR_PASSWORD_ERROR_MESSAGE = "There is no user record corresponding to this identifier. The user may have been deleted."
        const val FIREBASE_INVALID_EMAIL_FORMAT_ERROR_MESSAGE = "The email address is badly formatted."
        const val FIREBASE_EMAIL_ALREADY_IN_USE_ERROR_MESSAGE = "The email address is already in use by another account."
        const val FIREBASE_WRONG_PASSWORD_ERROR_MESSAGE = "The password is invalid or the user does not have a password."
    }

    private lateinit var auth: FirebaseAuth

    private lateinit var loggedInUser: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var registerBtn: Button
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        loggedInUser = findViewById(R.id.textView_logged)
        emailEditText = findViewById(R.id.edit_txt_username)
        passwordEditText = findViewById(R.id.edit_txt_password)
        loginBtn = findViewById(R.id.btn_login)
        registerBtn = findViewById(R.id.btn_register_here)

        //Checks credentials are valid and logs user in on success
        loginBtn.setOnClickListener{
            try
            {
                auth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener {
                        Log.d(TAG, "onCreate: ${it.exception?.localizedMessage}")
                        if(it.exception?.localizedMessage == FIREBASE_INCORRECT_EMAIL_OR_PASSWORD_ERROR_MESSAGE)
                        {
                            showErrorMessage("Invalid username or password")
                            clearinputs()
                        }
                        if(it.exception?.localizedMessage == FIREBASE_WRONG_PASSWORD_ERROR_MESSAGE)
                        {
                            showErrorMessage("Invalid username or password")
                            clearinputs()
                        }
                        if(it.exception?.localizedMessage == FIREBASE_INVALID_EMAIL_FORMAT_ERROR_MESSAGE)
                        {
                            showErrorMessage("Please enter correctly formatted email")
                            clearinputs()
                        }
                    }
            }
            catch (e: IllegalArgumentException)
            {
                Log.d(TAG, "onCreate: Email/Username and Password cannot be empty.")
                showErrorMessage("Email/Username and Password cannot be empty.")
            }
        }

        auth.addAuthStateListener {
            if(it.currentUser != null)
            {
                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
                //Logged In
                Log.d(TAG, "onCreate: User is logged in")
                Log.d(TAG, "onCreate: Email -> ${it.currentUser?.email}")
            }
            else
            {
                //Not Logged in
                Log.d(TAG, "onCreate: User is NOT logged in")
            }
        }

        //Goes to RegisterActivity on click
        registerBtn.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun clearinputs()
    {
        emailEditText.setText("")
        passwordEditText.setText("")
    }

    private fun setUserLoggedInText(text: String?)
    {
        loggedInUser.text = text
    }

    private fun showErrorMessage(message: String)
    {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}