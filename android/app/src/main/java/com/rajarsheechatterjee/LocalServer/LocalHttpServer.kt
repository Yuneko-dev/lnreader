package com.rajarsheechatterjee.LocalServer

import android.util.Log
import android.webkit.MimeTypeMap
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream

/**
 * A lightweight HTTP server that serves files from NOVEL_STORAGE.
 */
class LocalHttpServer(port: Int, private val basePath: String) : NanoHTTPD("127.0.0.1", port) {
    companion object {
        private const val TAG = "LocalHttpServer"
    }

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri ?: return newFixedLengthResponse(
            Response.Status.BAD_REQUEST, "text/plain", "Bad request"
        )

        // Decode URI and build file path
        val requestedPath = uri.trimStart('/')
        if (requestedPath.isEmpty()) {
            return newFixedLengthResponse(
                Response.Status.NOT_FOUND, "text/plain", "Not found"
            )
        }

        val file = File(basePath, requestedPath)

        // Canonicalize and verify path stays within basePath
        val canonicalBase = File(basePath).canonicalPath
        val canonicalFile = file.canonicalPath
        if (!canonicalFile.startsWith(canonicalBase)) {
            Log.w(TAG, "Path traversal attempt blocked: $uri")
            return newFixedLengthResponse(
                Response.Status.FORBIDDEN, "text/plain", "Access denied"
            )
        }

        if (!file.exists() || !file.isFile) {
            return newFixedLengthResponse(
                Response.Status.NOT_FOUND, "text/plain", "File not found: $requestedPath"
            )
        }

        // Detect MIME type
        val mimeType = getMimeType(file.name)
        val fileLength = file.length()

        return try {
            val fis = FileInputStream(file)
            val response = newFixedLengthResponse(Response.Status.OK, mimeType, fis, fileLength)
            // Add CORS and cache headers
            // response.addHeader("Access-Control-Allow-Origin", "*")
            // response.addHeader("Cache-Control", "public, max-age=3600")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error serving file: $canonicalFile", e)
            newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR, "text/plain", "Internal server error"
            )
        }
    }

    private fun getMimeType(fileName: String): String {
        val ext = MimeTypeMap.getFileExtensionFromUrl(fileName) ?: fileName.substringAfterLast('.', "")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.lowercase()) ?: when (ext.lowercase()) {
            "css" -> "text/css"
            "js" -> "application/javascript"
            "html", "htm", "xhtml" -> "text/html"
            "json" -> "application/json"
            "xml", "opf", "ncx" -> "application/xml"
            "svg" -> "image/svg+xml"
            "woff" -> "font/woff"
            "woff2" -> "font/woff2"
            "ttf" -> "font/ttf"
            "otf" -> "font/otf"
            else -> "application/octet-stream"
        }
    }
}
