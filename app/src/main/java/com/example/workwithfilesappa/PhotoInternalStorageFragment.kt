package com.example.workwithfilesappa

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.workwithfilesappa.databinding.FragmentPhotoInternalStorageBinding
import java.io.File
import java.io.IOException

class PhotoInternalStorageFragment : Fragment() {

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var binding: FragmentPhotoInternalStorageBinding
    lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoInternalStorageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraButton.setOnClickListener {
            //takePictureInternal1()
            takePictureInternal2()
        }

        binding.photoImageView.setOnClickListener {

            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.android.fileprovider",
                File(currentPhotoPath)
            )
            val launchIntent =
                requireContext().packageManager.getLaunchIntentForPackage("com.example.workwithfileappb")?.apply {
                    data = photoURI
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            startActivity(launchIntent)
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
            val photoURI: Uri =
                FileProvider.getUriForFile(requireContext(), "com.example.android.fileprovider", it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureInternal2.launch(intent)
        }
    }

    private val takePictureInternal2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                binding.photoImageView.setImageBitmap(bitmap)
            }
        }

    private fun takePictureInternal1() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFileInternal()
                } catch (ex: IOException) {
                    Log.i("Camera", "Could not create a file")
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(), "com.example.android.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
        binding.photoImageView.setImageBitmap(bitmap)
    }

    @Throws(IOException::class)
    private fun createImageFileInternal(): File {
        val storageDirInternal: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("myPhoto_", ".jpg", storageDirInternal).apply {
            currentPhotoPath = absolutePath
        }
    }
}