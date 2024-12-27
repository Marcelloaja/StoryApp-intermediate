package com.example.subduaintermediate.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.subduaintermediate.R
import com.example.subduaintermediate.call.ResultCall
import com.example.subduaintermediate.databinding.ActivityUploadBinding
import com.example.subduaintermediate.utils.getImageUri
import com.example.subduaintermediate.utils.reduceFileImage
import com.example.subduaintermediate.utils.uriToFile
import com.example.subduaintermediate.view.ViewModelFactory
import com.example.subduaintermediate.view.main.MainActivity
import com.yalantis.ucrop.UCrop
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast("Permission request granted by user")
        } else {
            showToast("Permission request denied by user")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestCameraPermission()

        binding.galleryUploadButton.setOnClickListener { startGallery() }
        binding.cameraUploadButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun requestCameraPermission() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            startCrop(uri)
        } else {
            Log.d("Photo", "No Photo Selected")
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .start(this)
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            Toast.makeText(this, "Pengambilan gambar dibatalkan.", Toast.LENGTH_SHORT).show()
            currentImageUri = null
            binding.previewImageView.setImageResource(R.drawable.photo_add)
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        if (currentImageUri == null) {
            showToast(getString(R.string.error_image_not_selected))
            return
        }

        val description = binding.uploadEdtText.text.toString()
        if (description.isEmpty()) {
            showToast(getString(R.string.error_description_not_provided))
            return
        }

        val imageFile = uriToFile(currentImageUri!!, this).reduceFileImage()
        Log.d("Image File", "showImage: ${imageFile.path}")

        viewModel.uploadImage(imageFile, description).observe(this) { result ->
            when (result) {
                is ResultCall.Loading -> showLoading(true)
                is ResultCall.Success -> {
                    showToast(result.data.message)
                    showLoading(false)
                    showMain()
                }

                is ResultCall.Error -> {
                    showToast(result.error)
                    showLoading(false)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(data!!)
            error?.let {
                Log.e("UCrop", "Crop error: ${it.localizedMessage}")
            }
        }
    }

    private fun showMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}