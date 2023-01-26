package com.muhammedkocabas.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muhammedkocabas.kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //sign in button clicked
    fun signInClicked(view : View) {
        val email = binding.emailText.text.toString()
        val password = binding.passswordText.text.toString()

        if (email.equals("") || password.isEmpty()) {
            Toast.makeText(this,"Enter email and password!", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                //success
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                //failure
                Toast.makeText(this@MainActivity, it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }

    //sign up button clicked
    fun signUpClicked(view : View) {
        val email = binding.emailText.text.toString()
        val password = binding.passswordText.text.toString()

        if (email.equals("") || password.isEmpty()) {
            Toast.makeText(this,"Enter email and password!", Toast.LENGTH_LONG).show()
        } else {
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                //success
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                //failure
                Toast.makeText(this@MainActivity, it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }



    }


}