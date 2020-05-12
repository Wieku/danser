package me.wieku.framework.resource

import me.wieku.framework.logging.Logging
import java.io.File
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream

fun FileHandle.md5() = hash("MD5")
fun FileHandle.sha1() = hash("SHA1")

fun FileHandle.hash(algorithm: String): String {
    val messageDigest = MessageDigest.getInstance(algorithm)

    DigestInputStream(inputStream(), messageDigest).use {
        val buffer = ByteArray(4096)

        var bytesRead = 0
        while (bytesRead > -1) {
            bytesRead = it.read(buffer, 0, 4096)
        }
    }

    return bytes2Hex(messageDigest.digest())
}

private fun bytes2Hex(bts: ByteArray): String {
    return bts.joinToString(separator = ""){ "%02x".format(it) }
}

fun FileHandle.unpack(
    directory: String = file.absolutePath.substringBefore(".${file.extension}"),
    removeAfter: Boolean = true
) {
    val logger = Logging.getLogger("runtime")

    ZipInputStream(fileURL.openStream()).use { zipStream ->
        logger.info("Unpacking \"${file.name}\"...")

        var next = zipStream.nextEntry
        while (next != null) {
            val file = File(directory + File.separator + next.name)
            file.parentFile.mkdirs()

            file.outputStream().use { outStream ->
                zipStream.copyTo(outStream)
            }

            next = zipStream.nextEntry
        }
    }

    if (removeAfter) {
        logger.info("Removing \"${file.name}\"...")
        file.delete()
    }
}