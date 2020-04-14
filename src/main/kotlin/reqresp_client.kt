import com.google.gson.Gson
import io.rsocket.Payload
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import java.util.function.Consumer


fun main() {
    val transport = TcpClientTransport.create("127.0.0.1", 7979)

    val client = RSocketFactory.connect().transport(transport)
            .start().block()

    val gson = Gson()

    client!!.requestResponse(DefaultPayload.create(
            gson.toJson(EchoMessageWithDelay("hello1", 5))
    ))
            .map(Payload::getDataUtf8)
            .doOnNext(Consumer { println("RECEIVED: $it") })
            .subscribe()

    client!!.requestResponse(DefaultPayload.create(
            gson.toJson(EchoMessageWithDelay("hello2", 1))
    ))
            .map(Payload::getDataUtf8)
            .doOnNext(Consumer { println("RECEIVED: $it") })
            .subscribe()
    //.block()

    Thread.sleep(10000L)

    client.dispose()

}