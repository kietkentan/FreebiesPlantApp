package com.khtn.freebies.listener

interface AuthListener {
    fun onExit()
    fun onStarted()
    fun onSuccess()
    fun onFailure(message : String)
}