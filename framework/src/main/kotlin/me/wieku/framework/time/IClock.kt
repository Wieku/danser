package me.wieku.framework.time

interface IClock {
    var currentTime: Float
    var clockRate: Float
    var isRunning: Boolean
}