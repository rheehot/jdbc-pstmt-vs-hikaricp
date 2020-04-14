import com.google.gson.Gson
import io.rsocket.*
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.DefaultPayload
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

fun main() {
    val server = RSocketFactory.receive()
            .errorConsumer { println("ERR: $it") }
            .frameDecoder(PayloadDecoder.ZERO_COPY)
            .acceptor(PingHandler())
            .transport(TcpServerTransport.create("127.0.0.1", 7878))
            .start()
            .block()
    while (true) {
        Thread.sleep(10L)
    }
}

class PingHandler : SocketAcceptor {
    private val gson = Gson()
    private val executors = Executors.newFixedThreadPool(10)

    override fun accept(setup: ConnectionSetupPayload?, sendingSocket: RSocket?): Mono<RSocket> {
        return Mono.just(
                object : AbstractRSocket() {
                    override fun requestResponse(payload: Payload?): Mono<Payload> {
                        val req = gson.fromJson(payload!!.dataUtf8, EchoMessageWithDelay::class.java)
                        val future = CompletableFuture<Payload>()
                        executors.submit {
                            // TODO cancel 하면 흥미롭게도 관련된 클라이언트측 socket이 삐꾸가 나는 것 같은데?
                            //if (req.delaySeconds > 3) future.cancel(false)
                            Thread.sleep(req.delaySeconds * 1000L)
                            future.complete(DefaultPayload.create("echo: " + payload!!.dataUtf8))
                        }
                        return Mono.fromFuture(future)
                    }

                    override fun fireAndForget(payload: Payload?): Mono<Void> {
                        println("Got FNF: " + payload!!.dataUtf8)
                        return Mono.empty()
                    }
                })
    }
}

