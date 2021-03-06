
package org.jetbrains.kotlin.jupyter.test

import org.jetbrains.kotlin.jupyter.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.zeromq.ZMQ

class KernelServerTest : KernelServerTestsBase() {

    @Test
    fun testHeartbeat() {
        val context = ZMQ.context(1)
        with (ClientSocket(context, JupyterSockets.hb)) {
            try {
                connect()
                send("abc")
                val msg = recvStr()
                assertEquals("abc", msg)
            } finally {
                close()
                context.term()
            }
        }
    }

    @Test
    fun testStdin() {
        val context = ZMQ.context(1)
        with (ClientSocket(context, JupyterSockets.stdin)) {
            try {
                connect()
                sendMore("abc")
                sendMore("def")
                send("ok")
            } finally {
                close()
                context.term()
            }
        }
    }

    @Test
    fun testShell() {
        val context = ZMQ.context(1)
        with (ClientSocket(context, JupyterSockets.control)) {
            try {
                connect()
                sendMessage(Message(id = messageId, header = makeHeader("kernel_info_request")), hmac)
                val msg = receiveMessage(recv(), hmac)
                assertEquals("kernel_info_reply", msg!!.header!!["msg_type"])
            } finally {
                close()
                context.term()
            }
        }
    }
}