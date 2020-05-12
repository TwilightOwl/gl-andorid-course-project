package com.glandroidcourse.tanks.game

import eac.network.Connection
import eac.network.PackageReceiver
import eac.network.PackageSender
import eac.network.Tcp

class NetworkPlayer {

    var address: String = "212.75.210.227"
    var port: Int = 3456
    private var connection: Connection? = null
    private var sender = PackageSender()
    private var receiver = PackageReceiver()

    fun start() {
        var tcp = Tcp(address, port).setOnDisconnected<Tcp> { disconnect() }
        connection = tcp
        tcp.start()
        sender.register(tcp)
        receiver.register(tcp) { _, bytes -> onMessage(String(bytes)) }

    }

    private fun send(message: String, call: ((Boolean) -> Unit)? = null) {
        println("send: $message")
        sender.send(message, object : Connection.Call() {
            override fun onSuccess(data: Connection.Data) { call?.invoke(true) }
            override fun onError(data: Connection.Data) { call?.invoke(false) }
        })
    }

    fun onMessage(message: String) {
        println("onMessage: $message")
    }

    fun res(bool: Boolean) {
        println("result $bool")
    }

    fun ready() {
        send("READY") { bool -> res(bool) }
    }

    fun disconnect() {
        println("disconnect")
    }
}