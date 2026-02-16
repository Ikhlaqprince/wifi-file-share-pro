package com.wifisharepro.server

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream

class FileServer(port: Int, private val context: Context) : NanoHTTPD(port) {

    private val shareDir: File =
        File(context.getExternalFilesDir(null), "Share").apply { mkdirs() }

    override fun serve(session: IHTTPSession): Response {

        return when {

            session.method == Method.GET && session.uri == "/" -> {
                newFixedLengthResponse(generateHtml())
            }

            session.method == Method.GET && session.uri.startsWith("/download/") -> {
                val fileName = session.uri.removePrefix("/download/")
                val file = File(shareDir, fileName)

                if (file.exists()) {
                    newFixedLengthResponse(
                        Response.Status.OK,
                        "application/octet-stream",
                        FileInputStream(file),
                        file.length()
                    )
                } else {
                    newFixedLengthResponse(
                        Response.Status.NOT_FOUND,
                        MIME_PLAINTEXT,
                        "File not found"
                    )
                }
            }

            session.method == Method.POST && session.uri == "/upload" -> {
                val files = HashMap<String, String>()
                session.parseBody(files)

                val tempFilePath = files["file"]
                if (tempFilePath != null) {
                    val tempFile = File(tempFilePath)
                    tempFile.copyTo(File(shareDir, tempFile.name), overwrite = true)
                }

                newFixedLengthResponse("Uploaded Successfully")
            }

            else -> newFixedLengthResponse(
                Response.Status.NOT_FOUND,
                MIME_PLAINTEXT,
                "Not Found"
            )
        }
    }

    private fun generateHtml(): String {

        val fileList = shareDir.listFiles()?.joinToString("") {
            "<li><a href=\"/download/${it.name}\">${it.name}</a></li>"
        } ?: "<li>No files</li>"

        return """
            <html>
            <body>
                <h2>WiFi File Share</h2>
                <ul>$fileList</ul>
                <br/>
                <form action="/upload" method="post" enctype="multipart/form-data">
                    <input type="file" name="file"/>
                    <input type="submit" value="Upload"/>
                </form>
            </body>
            </html>
        """.trimIndent()
    }
}
