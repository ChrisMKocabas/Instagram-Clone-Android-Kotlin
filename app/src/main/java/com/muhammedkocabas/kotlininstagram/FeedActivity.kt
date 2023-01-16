package com.muhammedkocabas.kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muhammedkocabas.kotlininstagram.databinding.ActivityFeedBinding
import com.muhammedkocabas.kotlininstagram.databinding.ActivityMainBinding

class FeedActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFeedBinding
    private  lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //initialize authentication
        auth = Firebase.auth
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