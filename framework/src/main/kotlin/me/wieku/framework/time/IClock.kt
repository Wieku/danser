package me.wieku.framework.time

interface IClock {
    var currentTime: Double
    var clockRate: Double
    var isRunning: Boolean
}