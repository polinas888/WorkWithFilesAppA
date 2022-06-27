package com.example.workwithfilesappa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.workwithfilesappa.databinding.FragmentChooseActionBinding

class ChooseActionFragment : Fragment() {
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentChooseActionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseActionBinding.inflate(inflater, container, false)
        val view = binding.root

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermissionGranted =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: readPermissionGranted
                writePermissionGranted =
                    permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                        ?: writePermissionGranted
            }

        val hasReadPermission =
            StorageUtils.updateOrRequestReadPermission(requireContext(), readPermissionGranted, permissionsLauncher)
        readPermissionGranted = hasReadPermission
        val hasWritePermission = StorageUtils.updateOrRequestWritePermission(requireContext(), writePermissionGranted, permissionsLauncher)
        writePermissionGranted = hasWritePermission || StorageUtils.minSdk29

        binding.openPhotoInternalStorageFragmentButton.setOnClickListener {
            addFragmentToActivity((PhotoInternalStorageFragment()))
        }

        binding.openPhotoSharedStorageFragmentButton.setOnClickListener {
            addFragmentToActivity((PhotoExternalStorageFragment()))
        }

        binding.openFileInternalStorageFragmentButton.setOnClickListener {
            addFragmentToActivity((FileInternalStorageFragment()))
        }

        binding.openFileSharedStorageFragmentButton.setOnClickListener {
            addFragmentToActivity((FileExternalStorageFragment()))
        }
        return view
    }

    private fun addFragmentToActivity(fragment: Fragment?){
        if (fragment == null) return
        val tr = fragmentManager?.beginTransaction()
        tr?.replace(R.id.fragment_container_view, fragment)
        tr?.commit()
    }
}