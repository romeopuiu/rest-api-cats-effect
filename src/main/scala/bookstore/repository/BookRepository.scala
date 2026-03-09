package bookstore.repository

import bookstore.domain.{Book, BookId, CreateBook, UpdateBook}

trait BookRepository[F[_]]:
  def create(book: CreateBook): F[Book]
  def findById(id: BookId): F[Option[Book]]
  def findAll: fs2.Stream[F, Book]
  def update(id: BookId, book: UpdateBook): F[Option[Book]]
  def delete(id: BookId): F[Boolean]