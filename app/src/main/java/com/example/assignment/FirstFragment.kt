package com.example.assignment

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.assignment.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var requestCamera: ActivityResultLauncher<String>? = null
    private var imageUri: Uri? = null

    private var _binding: FragmentFirstBinding? = null
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)


            } else {
                Toast.makeText(requireContext(), "Permission Not Granted", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        activityResultLauncher(requireContext())

        return binding.root

    }

    private fun activityResultLauncher(context: Context) {

        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (imageUri != null) {
                    try {
                        binding.imageView.setImageURI(imageUri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        binding.buttonFirst.setOnClickListener {
            requestCamera?.launch(android.Manifest.permission.CAMERA)
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.captureImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraOpen(requireContext())
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission Required Open Setting and allow Permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun cameraOpen(context: Context) {

        val values = ContentValues()
        values.put(
            MediaStore.Images.Media.TITLE,
            context.resources.getString(R.string.newPicture)
        )
        values.put(
            MediaStore.Images.Media.DESCRIPTION,
            context.resources.getString(R.string.fromYourCamera)
        )
        imageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        takePhotoLauncher.launch(cameraIntent)

    }


    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                101
            )
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}