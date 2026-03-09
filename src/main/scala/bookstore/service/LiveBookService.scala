package bookstore.service

import bookstore.domain.{Book, BookId, CreateBook, DomainError, UpdateBook}
import bookstore.repository.BookRepository
import cats.data.EitherT
import cats.effect.Async
import cats.syntax.all.given
import fs2.Stream

class LiveBookService[F[_]: Async](bookRepository: BookRepository[F]) extends BookService[F] {

  def create(book: CreateBook): EitherT[F, DomainError, Book] =
    if book.title.isEmpty || book.description.isEmpty then
      EitherT.leftT(DomainError.InvalidInput)
    else
      EitherT(
        bookRepository.create(book)
          .map(_.asRight[DomainError])
          .handleError(_ => DomainError.DatabaseError.asLeft)
      )

  def findById(id: BookId): EitherT[F, DomainError, Book] =
    EitherT {
      bookRepository.findById(id)
        .map {
          case Some(book) => book.asRight[DomainError]
          case None => DomainError.BookNotFound.asLeft[Book]
        }
        .handleError(_ => DomainError.DatabaseError.asLeft[Book])
    }

  def findAll: Stream[F, Book] =
    bookRepository.findAll
      .handleErrorWith(_ =>
        Stream.raiseError(new RuntimeException("Database failure"))
      )

  def update(id: BookId, book: UpdateBook): EitherT[F, DomainError, Book] =
    EitherT(
      bookRepository.update(id, book)
        .map {
          case Some(updated) => updated.asRight
          case None          => DomainError.BookNotFound.asLeft
        }
        .handleError(_ => DomainError.DatabaseError.asLeft)
    )

  def delete(id: BookId): EitherT[F, DomainError, Unit] =
    EitherT(
      bookRepository.delete(id)
        .map {
          case true  => ().asRight
          case false => DomainError.BookNotFound.asLeft
        }
        .handleError(_ => DomainError.DatabaseError.asLeft)
    )
}