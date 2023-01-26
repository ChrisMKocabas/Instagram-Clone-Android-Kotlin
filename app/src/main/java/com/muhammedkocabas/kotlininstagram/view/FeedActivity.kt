package com.muhammedkocabas.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muhammedkocabas.kotlininstagram.R
import com.muhammedkocabas.kotlininstagram.adapter.FeedRecyclerAdapter
import com.muhammedkocabas.kotlininstagram.databinding.ActivityFeedBinding
import com.muhammedkocabas.kotlininstagram.model.Post

class FeedActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFeedBinding
    private  lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    //create an arraylist to hold posts
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var feedAdapter: FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //initialize authentication
        auth = Firebase.auth
        db = Firebase.firestore

        //initialize arraylist of posts
        postArrayList = ArrayList<Post>()

        //read data from Firestore
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter
    }

    //read data from DB for feed activity
    private fun getData() {
        //read a snapshot of FireStore database
        db.collection("Posts").addSnapshotListener { value, error ->
            //if error is not null display the message
            if (error !=null) {
                Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {//if value is not null and not empty read documents
                if (value != null) {
                    if (!value.isEmpty) {
                        //read all documents
                        val documents = value.documents
                        // assign each document to a post object and add to the posts arraylist
                        for (document in documents) {
                            //comment was any type, so cast it as string
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String
//                            println(comment)
                            val post = Post(userEmail,comment,downloadUrl)
                            postArrayList.add(post)
                        }
                        //listen for data changes and upgate the feed adapter
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //connect options menu to activity
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // add functions to menu items

        //if add post is selected
        if (item.itemId == R.id.add_post) {
            val intent = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
        } //if sign out is selected
            else if (item.itemId == R.id.sign_out) {
            //sign out from app
            auth.signOut()
            //go back to main activity
            val intent = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}