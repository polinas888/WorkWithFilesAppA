package com.example.workwithfilesappa

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var photoImageView: ImageView
    lateinit var cameraButton: Button
    lateinit var currentPhotoPath: String
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoImageView = findViewById(R.id.photo_image_view)
        cameraButton = findViewById(R.id.camera_button)

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermissionGranted =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: readPermissionGranted
                writePermissionGranted =
                    permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                        ?: writePermissionGranted
            }

        updateOrRequestPermission()

        cameraButton.setOnClickListener {
          //  takePictureShared()
         //   takePictureSharedPreview()
         //   takePictureInternal1()
            takePictureInternal2()
        }

        photoImageView.setOnClickListener {
            val launchIntent =
                packageManager.getLaunchIntentForPackage("com.example.workwithfileappb")?.apply {
                    putExtra("photoPath", currentPhotoPath)
                }
            launchIntent?.let { startActivity(it) }
        }
    }

    private fun takePictureShared() {
        val savePhotoToExtStorageUri = getPhotoUri("myPhoto")
        takePhoto.launch(savePhotoToExtStorageUri)
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (true == it) {
            Log.i("Camera", "Image has taken")
        } else {
            Log.i("Camera", "Couldn't get image")
        }
    }

    private fun takePictureSharedPreview() {
        takePhotoPreview.launch(null)
        }

    private val takePhotoPreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        try {
            photoImageView.setImageBitmap(it)
            Log.i("Camera", "Photo taken")
        } catch (e: Exception) {
            Log.i("Camera", "Photo not taken")
        }
    }

    private fun getPhotoUri(name: String): Uri? {
        val imageCollection = getImageCollectionUri()
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpeg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/*jpeg")
        }
            return contentResolver.insert(imageCollection, contentValues)
    }

    private fun takePictureInternal1() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFileInternal()
                } catch (ex: IOException) {
                    Log.i("Camera", "Could not create a file")
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this, "com.example.android.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private val takePictureInternal2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                photoImageView.setImageBitmap(bitmap)
            }
        }

    private fun takePictureInternal2() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFileInternal()
        } catch (ex: IOException) {
            Log.i("Camera", "Could not create a file")
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureInternal2.launch(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
        photoImageView.setImageBitmap(bitmap)
    }

    @Throws(IOException::class)
    private fun createImageFileInternal(): File {
        val storageDirInternal: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("myPhoto_", ".jpg", storageDirInternal).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun getImageCollectionUri()= sdk29AndUp {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    private inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            onSdk29()
        } else null
    }

    private fun updateOrRequestPermission() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted)
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!readPermissionGranted)
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionsToRequest.isNotEmpty())
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
    }
}
