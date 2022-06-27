package com.example.workwithfilesappa

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.workwithfilesappa.databinding.FragmentPhotoExternalStorageBinding

class PhotoExternalStorageFragment : Fragment() {
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String
    lateinit var binding: FragmentPhotoExternalStorageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoExternalStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraButton.setOnClickListener {
            //takePictureShared()
            takePictureSharedPreview()
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

    private fun getPhotoUri(name: String): Uri? {
        val imageCollection = StorageUtils.getImageCollectionUri()
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpeg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/*jpeg")
        }
        return requireContext().contentResolver.insert(imageCollection, contentValues)
    }

    private fun takePictureSharedPreview() {
        takePhotoPreview.launch(null)
    }

    private val takePhotoPreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            try {
                binding.photoImageView.setImageBitmap(it)
                Log.i("Camera", "Photo taken")
            } catch (e: Exception) {
                Log.i("Camera", "Photo not taken")
            }
        }
}