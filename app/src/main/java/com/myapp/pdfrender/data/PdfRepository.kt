package com.myapp.pdfrender.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class SearchResult(val pageIndex: Int, val bounds: List<List<RectF>>)

class PdfRepository(private val context: Context) {

    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null

    suspend fun openDocument(uri: Uri): Int = withContext(Dispatchers.IO) {
        closeDocument()
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_render.pdf")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor!!)
        pdfRenderer?.pageCount ?: 0
    }

    suspend fun renderPage(pageIndex: Int): Bitmap? = withContext(Dispatchers.IO) {
        pdfRenderer?.let { renderer ->
            if (pageIndex < 0 || pageIndex >= renderer.pageCount) return@withContext null
            val page = renderer.openPage(pageIndex)
            // High quality rendering: scale bitmap
            val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            bitmap
        }
    }

    suspend fun searchText(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        if (query.isBlank()) return@withContext results
        
        Log.d("PdfRepository", "Searching for: $query")
        pdfRenderer?.let { renderer ->
            for (i in 0 until renderer.pageCount) {
                val page = renderer.openPage(i)
                try {
                    // Try to use the search API directly if available
                    // Using reflection or direct check to ensure we catch any API issues
                    val matches = page.searchText(query)
                    if (matches.isNotEmpty()) {
                        Log.d("PdfRepository", "Found ${matches.size} matches on page $i")
                        results.add(SearchResult(i, matches.map { it.bounds }))
                    }
                } catch (e: NoSuchMethodError) {
                    Log.e("PdfRepository", "Search API not found on this device/version")
                } catch (e: Exception) {
                    Log.e("PdfRepository", "Error searching page $i", e)
                } finally {
                    page.close()
                }
            }
        }
        results
    }

    fun closeDocument() {
        pdfRenderer?.close()
        fileDescriptor?.close()
        pdfRenderer = null
        fileDescriptor = null
    }
}
