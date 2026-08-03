package com.myapp.pdfrender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myapp.pdfrender.data.PdfRepository
import com.myapp.pdfrender.ui.screen.PdfScreen
import com.myapp.pdfrender.ui.theme.PDFRenderTheme
import com.myapp.pdfrender.ui.viewmodel.PdfViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = PdfRepository(applicationContext)
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PdfViewModel(repository) as T
            }
        })[PdfViewModel::class.java]

        setContent {
            PDFRenderTheme {
                PdfScreen(viewModel = viewModel)
            }
        }
    }
}
