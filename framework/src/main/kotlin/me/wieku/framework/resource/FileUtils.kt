package me.wieku.framework.resource

import java.io.File
import java.lang.StringBuilder
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipFile

fun FileHandle.md5() = hash("MD5")
fun FileHandle.sha1() = hash("SHA1")

fun FileHandle.hash(algorithm: String) : String {
    var messageDigest = MessageDigest.getInstance(algorithm)
    var digestStream = DigestInputStream(inputStream(), messageDigest)

    var buffer = ByteArray(4096)

    while (digestStream.read(buffer, 0, 4096) > -1) {}

    var digest = digestStream.messageDigest

    return bytes2Hex(digest.digest())
}

private fun bytes2Hex(bts: ByteArray): String {
    val des = StringBuilder()
    bts.forEach { des.append(String.format("%02X", it)) }
    return des.toString().toLowerCase()
}

fun FileHandle.unpack(directory: String = file.absolutePath.substringBefore(".${file.extension}"), removeAfter:Boolean = true) {
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