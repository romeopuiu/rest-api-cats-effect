import bookstore.config.{DatabaseConfig, ServerConfig}
import bookstore.repository.DoobieBookRepository
import bookstore.routes.BookRoutes
import bookstore.service.LiveBookService
import cats.effect.{ExitCode, IOApp}
import cats.effect.IO
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router

object BookApplication extends IOApp:

  def run(args: List[String]): IO[ExitCode] =
    DatabaseConfig.transactor[IO].use { transactor =>

      val repository = DoobieBookRepository[IO](transactor)
      val service = LiveBookService[IO](repository)
      val httpApp = Router(
        "api/books" -> BookRoutes[IO](service).routes
      ).orNotFound

      EmberServerBuilder
        .default[IO]
        .withHost(ServerConfig.Host)
        .withPort(ServerConfig.Port)
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    }