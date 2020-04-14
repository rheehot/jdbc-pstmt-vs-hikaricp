import io.rsocket.Payload
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import java.util.function.Consumer


fun main() {
    val transport = TcpClientTransport.create(7878)

    val client = RSocketFactory.connect().transport(transport)
            .start().block()

    client!!.fireAndForget(DefaultPayload.create("hello1"))
            .block()

    client.dispose()

}