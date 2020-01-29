package me.wieku.framework.resource

import java.io.File
import java.lang.StringBuilder
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipFile

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
    File(directory).mkdirs()
    ZipFile(file).use { zip ->
        println("Unpacking ${file.name}")
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { inStream ->
                File(directory + File.separator + entry.name).outputStream().use { outStream ->
                    inStream.copyTo(outStream)
                }
            }
        }
    }

    if (removeAfter)
        file.delete()
}