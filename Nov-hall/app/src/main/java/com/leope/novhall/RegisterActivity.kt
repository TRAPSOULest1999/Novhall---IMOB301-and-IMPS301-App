package com.leope.novhall

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.IllegalArgumentException

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"

    companion object
    {
        const val FIREBASE_EMAIL_ALREADY_IN_USE_ERROR_MESSAGE = "The email address is already in use by another account."
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    private lateinit var registerEmailEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var editRegistrationTxtFavAuthor: EditText
    private lateinit var editRegistrationTxtFavGenre: EditText

    private lateinit var registerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        registerBtn = findViewById(R.id.btn_register)
        registerEmailEditText = findViewById(R.id.edit_reg_txt_username)
        registerPasswordEditText = findViewById(R.id.edit_reg_txt_password)


        registerBtn.setOnClickListener {

            try
            {
                auth.createUserWithEmailAndPassword(registerEmailEditText.text.toString(), registerPasswordEditText.text.toString())
                    .addOnCompleteListener {
                        if(it.exception?.localizedMessage == FIREBASE_EMAIL_ALREADY_IN_USE_ERROR_MESSAGE) {
                            showErrorMessage("Email/Username is taken")
                        }
                        //Adds user document to users collection
                        else
                        {
                            val user = auth.currentUser

                            if(user != null)
                            {
                                val userId = user.uid
                                val fav_author = editRegistrationTxtFavAuthor.text.toString()
                                val fav_genre = editRegistrationTxtFavGenre.text.toString()

                                val data = hashMapOf(
                                    "admin" to false,
                                    "favourite_author" to fav_author,
                                    "favourite_genre" to fav_genre,
                                    "uid" to userId
                                )
                                firestore.collection("users").document(userId).set(data)
                            }
                        }
                    }
            }
            catch (e: IllegalArgumentException)
            {
                Log.d(TAG, "onCreate: Email/Username, Password, Favourite Author and Genre cannot be empty.")
                showErrorMessage("Email/Username, Password, Favourite Author and Genre cannot be empty.")
            }
        }
    }

    private fun showErrorMessage(message: String)
    {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

}