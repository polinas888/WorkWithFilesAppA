package com.example.workwithfilesappa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import com.example.workwithfilesappa.databinding.FragmentFileInternalStorageBinding
import java.io.File

class FileInternalStorageFragment : Fragment() {
    lateinit var binding: FragmentFileInternalStorageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileInternalStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.writeTextButton.setOnClickListener {
            val content = binding.editTextForTextFile.text.toString()
            val path = context?.filesDir
            val uri: Uri = getUriForFile(
                requireContext(), "com.example.android.fileprovider", File(path, "myAppFile.txt"))
            StorageUtils.writeToFile(content, requireContext(), uri)
        }

        binding.readTextButton.setOnClickListener {
            val path = requireContext().filesDir
            val uri = Uri.fromFile(File(path, "myAppFile.txt"))
            val content = StorageUtils.readFromUri(requireContext(), uri)
            binding.textFromFile.text = content
        }

        binding.sentFileButton.setOnClickListener {
            val fileUri: Uri = getUriForFile(
                requireContext(), "com.example.android.fileprovider", File(context?.filesDir, "myAppFile.txt"))
            val launchIntent =
                requireContext().packageManager.getLaunchIntentForPackage("com.example.workwithfileappb")?.apply {
                    data = fileUri
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            startActivity(launchIntent)
        }
    }
}