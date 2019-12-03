package me.wieku.framework.resource

interface Parsable {
    fun parseToString(): String
    fun parseFrom(data: String)
}