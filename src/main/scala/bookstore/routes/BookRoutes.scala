package bookstore.routes

import bookstore.domain.{Book, BookId, CreateBook, DomainError, UpdateBook}
import bookstore.service.BookService
import cats.data.EitherT
import cats.syntax.all.given
import org.http4s.Response
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.given
import cats.effect.Async
import org.http4s.circe.CirceEntityCodec._

class BookRoutes[F[_]: Async](service: BookService[F]) extends Http4sDsl[F] {

  private def handleError(error: DomainError): F[Response[F]] =
    error match
      case DomainError.BookNotFound  => NotFound("Book not found")
      case DomainError.InvalidInput  => BadRequest("Invalid input")
      case DomainError.DatabaseError => InternalServerError("Database error")

  private def resolve[A](
                          program: EitherT[F, DomainError, A]
                        )(success: A => F[Response[F]]): F[Response[F]] =
    program.value.flatMap {
      case Right(value) => success(value)
      case Left(error)  => handleError(error)
    }

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // CREATE
    case req @ POST -> Root =>
      resolve {
        for {
          input <- EitherT.liftF(req.as[CreateBook])
          book  <- service.create(input)
        } yield book
      }(Created(_))

    // GET ALL
    case GET -> Root =>
      Ok(service.findAll)

    // GET BY ID
    case GET -> Root / LongVar(id) =>
      resolve(service.findById(BookId(id)))(Ok(_))

    // UPDATE
    case req @ PUT -> Root / LongVar(id) =>
      resolve {
        for {
          input <- EitherT.liftF(req.as[UpdateBook])
          book  <- service.update(BookId(id), input)
        } yield book
      }(Ok(_))

    // DELETE
    case DELETE -> Root / LongVar(id) =>
      resolve(service.delete(BookId(id)))(_ => NoContent())
  }
}