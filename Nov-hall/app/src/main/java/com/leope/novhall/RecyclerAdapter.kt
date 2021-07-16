package com.leope.novhall

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var books: ArrayList<Books> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_layout_, parent ,false)
        )
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is BookViewHolder -> {
                holder.bindValuesToTextView(books[position])
                holder.addOnClickListener(books[position])
            }
        }
    }

    fun setBooks(newBooks: ArrayList<Books>)
    {
        this.books = newBooks
    }

    class BookViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTxt: TextView = itemView.findViewById(R.id.titleTextView)
        val author_nameTxt:  TextView = itemView.findViewById(R.id.firstnameTextView)
        val author_surnameTxt: TextView = itemView.findViewById(R.id.authorSurnameTextView)
        val genreTxt: TextView = itemView.findViewById(R.id.genreTextView)
        val priceTxt: TextView = itemView.findViewById(R.id.priceTextView)
        val stockTxt: TextView = itemView.findViewById(R.id.stockNumTextView)

        fun bindValuesToTextView(book: Books)
        {
            titleTxt.text = book.title
            author_nameTxt.text = book.author_name
            author_surnameTxt.text = book.author_surname
            genreTxt.text = book.genre
            priceTxt.text = book.price.toString()
            //sets num books to various colours based on stock amount
            val bookStock = book.stock
            if(bookStock < 3) {
                stockTxt.text = book.stock.toString()
                stockTxt.setTextColor(Color.RED)
            }
            else if (bookStock<=5 && bookStock >=4)
            {
                stockTxt.text = book.stock.toString()
                stockTxt.setTextColor(0xffa500) //setColor ORANGE
            }
            else if (bookStock > 5)
            {
                stockTxt.text = book.stock.toString()
                stockTxt.setTextColor(Color.GREEN)
            }
        }

        //adds OnClickListner to a book item
        fun addOnClickListener(book: Books)
        {
            val firestore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()

            itemView.setOnClickListener{
                if (book.uid != "")
                {
                    //gets the current users ID
                    val user = auth.currentUser
                    val userId = user?.uid
                    firestore.collection("users").document(userId.toString()).get()
                        .addOnSuccessListener {
                            //if checks if it can find document for current user
                            if(it != null)
                            {
                                //gets the admin boolean value from the users document
                                //if user is admin then it will open the admin side of BookDetails
                                //if user is NOT admin then it will open the normal users BookDetailsActivity
                                val isAdmin: Boolean = it["admin"] as Boolean
                                if(isAdmin)
                                {
                                    val intent = Intent(itemView.context, BookDetailsFragment::class.java).apply {
                                        putExtra("uid", book.uid)
                                    }
                                    itemView.context.startActivity(intent)

                                }
                                else if(!isAdmin)
                                {
                                    val intent2 = Intent(itemView.context, UserBookDetailsActivity::class.java).apply {
                                        putExtra("uid", book.uid)
                                    }
                                    itemView.context.startActivity(intent2)
                                }
                            }
                        }
                }
            }
        }
    }
}