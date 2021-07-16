package com.leope.novhall

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class BookDetailsFragment : AppCompatActivity() {

    private val TAG = "BookDetailsFragment"

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var titleEditText: EditText
    private lateinit var genreEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var stockEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var descriptionEditText: EditText

    private lateinit var deleteImageButton: ImageButton
    private lateinit var saveEditImageButton: ImageButton
    private lateinit var minusImageButton: ImageButton
    private lateinit var addImageButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_book_details)

        titleEditText = findViewById(R.id.txt_book_title)
        genreEditText = findViewById(R.id.txt_book_genre)
        priceEditText = findViewById(R.id.txt_book_price)
        stockEditText = findViewById(R.id.txt_book_stock)
        authorEditText = findViewById(R.id.txt_book_author)
        descriptionEditText = findViewById(R.id.editTextTextMultiLine)

        deleteImageButton = findViewById(R.id.img_delete)
        saveEditImageButton =findViewById(R.id.img_edit)
        minusImageButton = findViewById(R.id.img_minus)
        addImageButton = findViewById(R.id.img_add)

        //set buttons to INVISIBLE
        deleteImageButton.visibility = View.INVISIBLE
        saveEditImageButton.visibility = View.INVISIBLE
        minusImageButton.visibility = View.INVISIBLE
        addImageButton.visibility = View.INVISIBLE

        //set buttons to disabled
        deleteImageButton.isEnabled = false
        saveEditImageButton.isEnabled = false
        minusImageButton.isEnabled = false
        addImageButton.isEnabled = false


        val bookUid = intent.getStringExtra("uid")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        /*Check if current user isAdmin, if \"true\" then administrator priviledges are granted
        by setting the buttons to visible and enabled*/
        val user = auth.uid
        if (user != null) {
            // User is signed in
            firestore.collection("users").document(user.toString()).get()
                .addOnSuccessListener {
                    if(it != null)
                    {
                        val isAdmin: Boolean = it["admin"] as Boolean
                        if(isAdmin)
                        {
                            deleteImageButton.visibility = View.VISIBLE
                            saveEditImageButton.visibility = View.VISIBLE
                            minusImageButton.visibility = View.VISIBLE
                            addImageButton.visibility = View.VISIBLE

                            deleteImageButton.isEnabled = true
                            saveEditImageButton.isEnabled = true
                            minusImageButton.isEnabled = true
                            addImageButton.isEnabled = true

                            Toast.makeText(this@BookDetailsFragment,"Admin priviledges granted",Toast.LENGTH_SHORT).show()

                        }
                        else
                        {
                            Toast.makeText(this@BookDetailsFragment,"Here is a little bit about the book",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        else {
            // No user is signed in
        }

        if (bookUid != null)
        {
            //Code for admin to delete a book
            deleteImageButton.setOnClickListener {
                firestore.collection("books").document(bookUid).delete()
            }

            //Code to continually listen and update
            firestore.collection("books").document(bookUid).addSnapshotListener{document, error ->
                if(document != null)
                {
                    val uid: String = document.id
                    val author: HashMap<String, String> = document["author"] as HashMap<String, String>
                    val firstname: String = author["firstname"].toString()
                    val lastname: String = author["lastname"].toString()
                    val description: String = document["description"].toString()
                    val price: Int = document["price"] as Int
                    val stock: Int = document["stock"] as Int
                    val title: String = document["title"].toString()

                    val genre: ArrayList<String> = document["genre"] as ArrayList<String>
                    for(l in genre)
                    {
                        Log.d(TAG, "onCreate: Genre -> $l")
                    }
                    titleEditText.setText(title)
                    authorEditText.setText(firstname + " "+ lastname)
                    descriptionEditText.setText(description)
                    priceEditText.setText(price)
                    stockEditText.setText(stock)
                    genreEditText.setText(genre[0])
                }
            }

            //Displays book information to the Edit Text's of BookDetailsFragment
            firestore.collection("books").document(bookUid).get()
                .addOnSuccessListener {
                    if(it != null)
                    {
                        val uid: String = it.id
                        val author: HashMap<String, String> = it["author"] as HashMap<String, String>
                        val firstname: String = author["firstname"].toString()
                        val lastname: String = author["lastname"].toString()
                        val description: String = it["description"].toString()
                        val price: Int = it["price"] as Int
                        val stock: Int = it["stock"] as Int
                        val title: String = it["title"].toString()

                        val genre: ArrayList<String> = it["genre"] as ArrayList<String>
                        for(l in genre)
                        {
                            Log.d(TAG, "onCreate: Genre -> $l")
                        }
                        titleEditText.setText(title)
                        authorEditText.setText(firstname + " "+ lastname)
                        descriptionEditText.setText(description)
                        priceEditText.setText(price)
                        stockEditText.setText(stock)
                        genreEditText.setText(genre[0])
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@BookDetailsFragment,"Could not get book info",Toast.LENGTH_SHORT).show()
                }
            }

            //Saves any changes an admin might have made to the firestore
            saveEditImageButton.setOnClickListener{

                val description = descriptionEditText.text.toString()
                val price = priceEditText.text
                val stock = stockEditText.text
                val title = titleEditText.text.toString()
                val genre = genreEditText.text

                val data = hashMapOf(
                    "description" to description,
                    "price" to price,
                    "stock" to stock,
                    "title" to title,
                    "genre" to arrayListOf(genre)
                )
                firestore.collection("books").document(bookUid.toString()).set(data).addOnSuccessListener {
                    Toast.makeText(this@BookDetailsFragment, "Success",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this@BookDetailsFragment, "Failed",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }