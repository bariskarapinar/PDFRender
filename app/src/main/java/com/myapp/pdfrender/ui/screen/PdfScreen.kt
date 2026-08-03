package com.myapp.pdfrender.ui.screen

import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myapp.pdfrender.data.SearchResult
import com.myapp.pdfrender.ui.components.*
import com.myapp.pdfrender.ui.theme.*
import com.myapp.pdfrender.ui.viewmodel.PdfUiState
import com.myapp.pdfrender.ui.viewmodel.PdfViewModel

@Composable
fun PdfScreen(viewModel: PdfViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val pages by viewModel.pages.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val fileName = getFileName(context, it)
                viewModel.loadPdf(it, fileName)
            }
        }
    )

    AnimatedGradientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val query by viewModel.searchQuery.collectAsState()

            GlassyTopBar(title = (uiState as? PdfUiState.Success)?.fileName ?: "PDF Render")

            if (uiState is PdfUiState.Success) {
                NeonSearchBar(
                    query = query,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is PdfUiState.Idle -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NeonGlowBox {
                                NeonButton(text = "Open PDF Document") {
                                    launcher.launch(arrayOf("application/pdf"))
                                }
                            }
                        }
                    }
                    is PdfUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = NeonCyan
                        )
                    }
                    is PdfUiState.Success -> {
                        PdfPageList(
                            pageCount = state.pageCount,
                            pages = pages,
                            searchResults = searchResults,
                            onLoadPage = { viewModel.renderPage(it) }
                        )
                    }
                    is PdfUiState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPageList(
    pageCount: Int,
    pages: Map<Int, Bitmap>,
    searchResults: List<SearchResult>,
    onLoadPage: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(pageCount) { index ->
            val bitmap = pages[index]
            val pageSearchResults = searchResults.filter { it.pageIndex == index }

            if (bitmap == null) {
                LaunchedEffect(index) {
                    onLoadPage(index)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Page ${index + 1}",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )

                        // Search Highlights
                        Canvas(modifier = Modifier.matchParentSize()) {
                            // The bitmap is scaled 2x in the repository (renderPage method)
                            // So we need to scale our coordinates correctly
                            val scaleX = size.width / (bitmap.width / 2f)
                            val scaleY = size.height / (bitmap.height / 2f)

                            pageSearchResults.forEach { result ->
                                result.bounds.forEach { matchBoundsList ->
                                    matchBoundsList.forEach { rect ->
                                        drawRect(
                                            color = (if (result.pageIndex % 2 == 0) NeonPink else NeonCyan).copy(alpha = 0.5f),
                                            topLeft = Offset(rect.left * scaleX, rect.top * scaleY),
                                            size = Size(
                                                (rect.right - rect.left) * scaleX,
                                                (rect.bottom - rect.top) * scaleY
                                            )
                                        )
                                        drawRect(
                                            color = if (result.pageIndex % 2 == 0) NeonPink else NeonCyan,
                                            topLeft = Offset(rect.left * scaleX, rect.top * scaleY),
                                            size = Size(
                                                (rect.right - rect.left) * scaleX,
                                                (rect.bottom - rect.top) * scaleY
                                            ),
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getFileName(context: android.content.Context, uri: Uri): String {
    var name = "Document.pdf"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name
}
