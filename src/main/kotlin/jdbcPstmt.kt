import com.google.common.base.Stopwatch
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.TimeUnit


fun main() {
    val url = "jdbc:postgresql://127.0.0.1:5432/foo"
    val props = Properties()
    props.setProperty("user", "postgres")
    props.setProperty("password", "postgres")
    val conn: Connection = DriverManager.getConnection(url, props)
    println(conn)
    //
    val pstmt = conn.prepareStatement("INSERT INTO posts (title) VALUES (?)")
    val sw = Stopwatch.createUnstarted()
    //
    sw.start()
    for (x in 0..1_000) {
        pstmt.setString(1, "FOO");
        pstmt.execute()
    }
    sw.stop()
    println("${sw.elapsed(TimeUnit.MILLISECONDS)} milliseconds")
    // 8421 milliseconds
    // 8383 milliseconds
}