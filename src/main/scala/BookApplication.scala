import bookstore.config.Database
import bookstore.repository.DoobieBookRepository
import bookstore.routes.BookRoutes
import bookstore.service.LiveBookService
import cats.effect.{ExitCode, IOApp}
import cats.effect.IO
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import com.comcast.ip4s.host
import com.comcast.ip4s.port

object BookApplication extends IOApp:

  def run(args: List[String]): IO[ExitCode] =
    Database.transactor[IO].use { xa =>

      val repository = DoobieBookRepository[IO](xa)
      val service = LiveBookService[IO](repository)
      val httpApp = Router(
        "api/books" -> BookRoutes[IO](service).routes
      ).orNotFound

      EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    }