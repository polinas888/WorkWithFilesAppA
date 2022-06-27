package com.example.workwithfilesappa

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.workwithfilesappa.databinding.FragmentFileExternalStorageBinding

class FileExternalStorageFragment : Fragment() {
    lateinit var binding: FragmentFileExternalStorageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileExternalStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.writeTextButton.setOnClickListener {
            getIntentToWriteFileInSharedStorage()
        }

        binding.readTextButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
            saveExternalFileContent.launch(intent)
        }
    }

    private val saveExternalFileContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data
                if (uri != null) {
                    val text = StorageUtils.readFromUri(requireContext(), uri)
                    binding.textFromFile.text = text
                } else {
                    Log.i("FileError", "Can't find the file")
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIntentToWriteFileInSharedStorage() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "Myapp.txt")
        }
        writeExternalFileContent.launch(intent)
    }

    private val writeExternalFileContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val content = binding.editTextForTextFile.text.toString()
                val uri = it.data?.data
                if (uri != null) {
                    StorageUtils.writeToFile(content, requireContext(), uri)
                } else {
                    Log.i("FileWriter", "Cant open activity to write the file")
                }
            } else {
                Log.i("ErrorFile", "Cant open file")
            }
        }
}

