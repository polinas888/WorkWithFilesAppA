package com.example.workwithfilesappa

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            val uri = Uri.fromFile(File(path, "myFile2.txt"))
            StorageUtils.writeToFile(content, requireContext(), uri)
        }

        binding.readTextButton.setOnClickListener {
            val path = requireContext().filesDir
            val uri = Uri.fromFile(File(path, "myFile2.txt"))
            val content = StorageUtils.readFromUri(requireContext(), uri)
            binding.textFromFile.text = content
        }
    }
}