package com.example.simplecameraapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                CameraScreen()
            }
        }
    }
}

@Composable
fun CameraScreen() {

    val context = LocalContext.current

    var photoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var cameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    // Open the phone camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {
            photoUri = cameraUri
        }
    }

    // Share the captured photograph
    fun sharePhoto(uri: Uri) {

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = android.content.ClipData.newRawUri(
                "Captured Photo", uri
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Share Photo")
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Simple Camera App",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(25.dp))

            if (photoUri != null) {

                AsyncImage(
                    model = photoUri,
                    contentDescription = "Captured Photograph",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Fit
                )

            } else {

                Text(
                    text = "No photograph captured yet",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {

                    try {
                        val imageFile = File.createTempFile(
                            "captured_photo_",
                            ".jpg",
                            context.cacheDir
                        )

                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            imageFile
                        )

                        cameraUri = uri
                        cameraLauncher.launch(uri)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Camera")
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    photoUri?.let { sharePhoto(it) }
                },
                enabled = photoUri != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share Photo")
            }
        }
    }
}