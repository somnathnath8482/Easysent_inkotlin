package com.easy.kotlintest.Helper

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.easy.kotlintest.Interface.Messages.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MethodClass {
    private var job: Job = Job()
    private var scope: CoroutineScope = CoroutineScope(job)

        fun getThumbnail(contentResolver:ContentResolver, uri:Uri, thumbnail: Item<Bitmap>){
            scope.launch {
                try {
                    contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->

                        val pdfRenderer = PdfRenderer(parcelFileDescriptor).openPage(0)
                        val bitmap = Bitmap.createBitmap(
                            pdfRenderer.width,
                            pdfRenderer.height,
                            Bitmap.Config.ARGB_8888
                        )
                        pdfRenderer.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        pdfRenderer.close()

                        thumbnail.onItem(bitmap);
                }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

        }


}