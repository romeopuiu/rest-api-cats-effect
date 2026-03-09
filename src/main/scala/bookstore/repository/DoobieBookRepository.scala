package bookstore.repository

import bookstore.domain.{Book, BookId, CreateBook, UpdateBook}
import cats.effect.Concurrent
import doobie.Transactor
import doobie.Meta
import doobie.implicits.given 
import fs2.Stream

class DoobieBookRepository[F[_]: Concurrent](xa: Transactor[F]) extends BookRepository[F]:

  private given Meta[BookId] = Meta[Long].imap(BookId(_))(_.value)

  override def create(book: CreateBook): F[Book] =
    sql"""
      INSERT INTO books (title, description)
      VALUES (${book.title}, ${book.description})
      RETURNING id, title, description
      """
      .query[(Long, String, String)]
      .unique
      .map { case (id, t, d) =>
      Book(BookId(id), t, d)
      }
      .transact(xa)

  override def findById(id: BookId): F[Option[Book]] =
    sql"""
      SELECT id, title, description
        FROM books
        WHERE id =${id.value}
      """
      .query[(Long, String, String)]
      .option
      .map(_.map { case (i, t, d) =>
      Book(BookId(i), t, d)
      })
      .transact(xa)

  override def findAll: Stream[F, Book] =
    sql"""
       SELECT id, title, description
       FROM books
     """
      .query[(Long, String, String)]
      .stream
      .map { case (i, t, d) =>
        Book(BookId(i), t, d)
      }
      .transact(xa)
  
  override def update(id: BookId, book: UpdateBook): F[Option[Book]] =
    sql"""
      UPDATE books SET title = ${book.title},
         description = ${book.description} WHERE id = ${id.value}
         RETURNING id, title, description
      """
      .query[(Long, String, String)]
      .option
      .map(_.map {
        case (i, t, d) =>
          Book(BookId(i), t, d)
      })
      .transact(xa)

  override def delete(id: BookId): F[Boolean] =
    sql"""
      DELETE FROM  books WHERE id = ${id.value}
    """
      .update
      .run
      .map(_ > 0)
      .transact(xa)