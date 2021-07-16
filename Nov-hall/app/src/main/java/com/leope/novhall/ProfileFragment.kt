package com.leope.novhall

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val TAG = "ProfileFragment"

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        initObjView(view)

        //When Edit button is clicked, firestore specific users document will be updated to fields below
        val buttonProfileEdit = view.findViewById<Button>(R.id.buttonProfileEdit)
        buttonProfileEdit.setOnClickListener {

            val user = auth.currentUser
            val userId = user?.uid

            val editTextFavAuthor = view.findViewById<EditText>(R.id.editTextFavAuthor)
            val editTextFavGenre = view.findViewById<EditText>(R.id.editTextFavGenre)

            val data = hashMapOf(
                "favourite_genre" to editTextFavGenre.text.toString(),
                "favourite_author" to editTextFavAuthor.text.toString()
            )
            firestore.collection("books").document(userId.toString()).set(data).addOnSuccessListener {
                Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    //loads users information to profile fragment for display
    fun initObjView(view: View) {
        val editTextFavAuthor = view.findViewById<EditText>(R.id.editTextFavAuthor)
        val editTextFavGenre = view.findViewById<EditText>(R.id.editTextFavGenre)
        val plainTextUid = view.findViewById<TextView>(R.id.plainTxtUid)

        val user = auth.currentUser

        if (user != null) {
            //User signed in
            val userId = user.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener {
                    if (it != null) {
                        val uid: String = it["uid"].toString()
                        val favouriteGenre: String = it["favourite_genre"].toString()
                        val favouriteAuthor: String = it["favourite_author"].toString()

                        Log.d(TAG, "onCreate: uid -> $uid")
                        Log.d(TAG, "onCreate: favourite genre -> $favouriteGenre")
                        Log.d(TAG, "onCreate: favourite author -> $favouriteAuthor")

                        plainTextUid.setText(uid)
                        editTextFavAuthor.setText(favouriteAuthor)
                        editTextFavGenre.setText(favouriteGenre)
                    }
                }
        } else {
            //No User is signed in
        }

    }
}