package com.muhammedkocabas.kotlininstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import com.muhammedkocabas.kotlininstagram.databinding.ActivityUploadBinding
import java.util.*


class UploadActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore:FirebaseFirestore
    private lateinit var storage:FirebaseStorage

    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var permissionLauncher2: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()

        //initialize authentication
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

    }

    fun uploadClicked (view: View) {

        //create random uuid
        val uuid = UUID.randomUUID()

        //get reference from storage
        val reference = storage.reference
        val imageReference = reference.child("images/$uuid")



        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                //get download url and save to firestore

                imageReference.downloadUrl.addOnSuccessListener {

                    val downloadUrl = it.toString()

                    if (auth.currentUser != null) {
                        //Create the post model schema for save to firestore
                        val postmap = hashMapOf<String, Any>()
                        postmap.put("downloadUrl", downloadUrl)
                        postmap.put("userEmail", auth.currentUser!!.email!!)
                        postmap.put("comment",binding.commentText.text.toString())
                        postmap.put("date",Timestamp.now())

                        firestore.collection("Posts").add(postmap).addOnSuccessListener {
                            finish()

                        }.addOnFailureListener {
                            Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }

                    }
                }.addOnFailureListener{
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                }



            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }



    fun selectImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                (ContextCompat.checkSelfPermission(
                    this@UploadActivity,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(
                            this@UploadActivity,
                            Manifest.permission.READ_MEDIA_VIDEO
                        ) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(
                            this@UploadActivity,
                            Manifest.permission.READ_MEDIA_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED) -> {
                    // You can use the API that requires the permission.
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    //start activity for result
                    activityResultLauncher.launch(intentToGallery)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission") {
                            //request permission

                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_MEDIA_IMAGES,
                                        Manifest.permission.READ_MEDIA_VIDEO,
                                        Manifest.permission.READ_MEDIA_AUDIO
                                    )
                                )

                        }.show()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO) -> {

                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission") {
                            //request permission
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.READ_MEDIA_IMAGES,
                                    Manifest.permission.READ_MEDIA_VIDEO,
                                    Manifest.permission.READ_MEDIA_AUDIO
                                )
                            )
                        }.show()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO) -> {

                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission") {
                            //request permission
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.READ_MEDIA_IMAGES,
                                    Manifest.permission.READ_MEDIA_VIDEO,
                                    Manifest.permission.READ_MEDIA_AUDIO
                                )
                            )
                        }.show()
                }
                else -> {
                    // You can directly ask for the permission.
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO
                        )
                    )
                }
            }
        }

        //  API Version is BEFORE API 33 - Tiramisu ///
        if (Build.VERSION.SDK_INT < 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission") {
                            //request permission
                            permissionLauncher2.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                } else {
                    //request permission
                    permissionLauncher2.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //start activity for result
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }




        private fun registerLauncher() {

            activityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                    if (result.resultCode == RESULT_OK) {
                        val intentFromResult = result.data
                        if (intentFromResult != null) {
                            selectedPicture = intentFromResult.data
                            selectedPicture?.let {
                                binding.imageView.setImageURI(it)
                            }
                        }
                    }
                }


            permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->

                when {
                    (permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false) &&
                            permissions.getOrDefault(Manifest.permission.READ_MEDIA_VIDEO, false) &&
                            permissions.getOrDefault(Manifest.permission.READ_MEDIA_AUDIO, false)) -> {
                        //permission granted
                        val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(intentToGallery)
                    }

                    else -> {
                        Toast.makeText(this@UploadActivity, "Permission needed!", Toast.LENGTH_LONG)
                        .show()


                        // IMPORTANT ==> API > 30 - User denied permissions more than TWICE, display app settings
                        if (Build.VERSION.SDK_INT >= 30) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                    }

                }
            }

            permissionLauncher2 =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                    if (result) {
                        //permissions granted
                        val intentToGallery =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(intentToGallery)
                    } else {
                        //permission  denied
                        Toast.makeText(this@UploadActivity, "Permission needed!", Toast.LENGTH_LONG)
                            .show()

                        // IMPORTANT  API > 30 - User denied permissions more than TWICE, display app settings
                        if (Build.VERSION.SDK_INT >= 30) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                    }
                }
        }

}