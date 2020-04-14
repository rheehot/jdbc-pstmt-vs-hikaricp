import com.google.common.base.Stopwatch
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.PrintWriter
import java.util.*
import java.util.concurrent.TimeUnit


fun main() {
    val props = Properties()
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.user", "postgres")
    props.setProperty("dataSource.password", "postgres")
    props.setProperty("dataSource.databaseName", "foo")
    props.setProperty("dataSource.portNumber", "5432")
    props.setProperty("dataSource.serverName", "127.0.0.1")
    props.put("dataSource.logWriter", PrintWriter(System.err))
    val config = HikariConfig(props)
    val ds = HikariDataSource(config)
    val sw = Stopwatch.createUnstarted()
    //
    sw.start()
    for (x in 0..1_000) {
        val conn = ds.connection
        val pstmt = conn.prepareStatement("INSERT INTO posts (title) VALUES (?)")
        pstmt.setString(1, "FOO");
        pstmt.execute()
        pstmt.close()
        conn.close()
    }
    sw.stop()
    println("${sw.elapsed(TimeUnit.MILLISECONDS)} milliseconds")
}
// 8897 milliseconds
// 8411 milliseconds
// 8395 milliseconds
