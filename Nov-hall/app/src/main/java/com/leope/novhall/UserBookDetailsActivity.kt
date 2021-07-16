package com.leope.novhall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserBookDetailsActivity : AppCompatActivity() {
    private  val TAG = "UserBookDetailsActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usertitleEditText: EditText
    private lateinit var usergenreEditText: EditText
    private lateinit var userpriceEditText: EditText
    private lateinit var userstockEditText: EditText
    private lateinit var userauthorEditText: EditText
    private lateinit var userdescriptionEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_book_details)

        usertitleEditText = findViewById(R.id.txtView_book_author)
        usergenreEditText = findViewById(R.id.txtView_book_genre)
        userpriceEditText = findViewById(R.id.txtView_book_price)
        userstockEditText = findViewById(R.id.txtView_book_stock)
        userauthorEditText = findViewById(R.id.txtView_book_author)
        userdescriptionEditText = findViewById(R.id.textViewTextMultiLine)

        val bookUid = intent.getStringExtra("uid")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (bookUid != null)
        {

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
                    usertitleEditText.setText(title)
                    userauthorEditText.setText(firstname + " "+ lastname)
                    userdescriptionEditText.setText(description)
                    userpriceEditText.setText(price)
                    userstockEditText.setText(stock)
                    usergenreEditText.setText(genre[0])
                }
            }

            //Displays book information to the Edit Text's of UserBookDetailsActivity
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
                        usertitleEditText.setText(title)
                        userauthorEditText.setText(firstname + " "+ lastname)
                        userdescriptionEditText.setText(description)
                        userpriceEditText.setText(price)
                        userstockEditText.setText(stock)
                        usergenreEditText.setText(genre[0])
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@UserBookDetailsActivity,"Could not get book info", Toast.LENGTH_SHORT).show()
                }
        }


    }
}