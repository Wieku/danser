package me.wieku.framework.resource

import java.io.File
import java.lang.StringBuilder
import java.security.DigestInputStream
import java.security.MessageDigest

fun File.md5() = hash("MD5")
fun File.sha1() = hash("SHA1")

fun File.hash(algorithm: String) : String {
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