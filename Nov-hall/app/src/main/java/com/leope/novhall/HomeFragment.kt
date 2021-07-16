package com.leope.novhall

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG = "HomeFragment"

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var bookRecyclerViewAdapter: RecyclerAdapter

    private var books: ArrayList<Books> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //getView()?.findViewById<RecyclerView>(R.id.bookRecyclerView)?. = "foo"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //var view: View? = null
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initRecyclerView(view)
        return view
    }

    fun initRecyclerView(view: View)
    {
        val bookRecyclerView = view.findViewById<RecyclerView>(R.id.bookRecyclerView)
        bookRecyclerView.apply {
            bookRecyclerView.layoutManager = LinearLayoutManager(activity)
            bookRecyclerViewAdapter = RecyclerAdapter()
            adapter = bookRecyclerViewAdapter
        }

        firestore.collection("books").get().addOnSuccessListener {

            if(it != null)
            {
                for(document in it.documents)
                {
                    val uid: String = document.id
                    val author: HashMap<String, String> = document["author"] as HashMap<String, String>
                    val firstname: String = author["firstname"].toString()
                    val lastname: String = author["lastname"].toString()
                    val price: Int = document["price"] as Int
                    val stock: Int = document["stock"] as Int
                    val title: String = document["title"].toString()

                    val genre: ArrayList<String> = document["genre"] as ArrayList<String>
                    for(l in genre)
                    {
                        Log.d(TAG, "onCreate: Genre -> $l")
                    }

                    books.add(Books(title, firstname, lastname, genre = genre[0],
                        price = price, stock = stock))
                }
                bookRecyclerViewAdapter.setBooks(books)
                bookRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }
}