package bookstore.service

import bookstore.domain.{Book, BookId, CreateBook, DomainError, UpdateBook}
import fs2.Stream
import cats.data.EitherT

trait BookService[F[_]]:
  def create(book: CreateBook): EitherT[F, DomainError, Book]
  def findById(id: BookId): EitherT[F, DomainError, Book]
  def findAll: Stream[F, Book]
  def update(id: BookId, book: UpdateBook): EitherT[F, DomainError, Book]
  def delete(id: BookId): EitherT[F, DomainError, Unit]