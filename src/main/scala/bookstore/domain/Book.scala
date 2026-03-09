package bookstore.domain

import bookstore.domain.BookId
import io.circe.Encoder
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder

final case class BookId(value: Long) extends AnyVal

object BookId:
  given Encoder[BookId] = Encoder[Long].contramap(_.value)
  given Decoder[BookId] = Decoder[Long].map(BookId(_))

final case class Book(id: BookId, title: String, description: String)

final case class CreateBook(title: String, description: String)
final case class UpdateBook(title: String, description: String)

object Book:
  given Encoder[Book] = deriveEncoder
  given Decoder[Book] = deriveDecoder

object CreateBook:
 given Decoder[CreateBook] = deriveDecoder
 given Encoder[CreateBook] = deriveEncoder

object UpdateBook:
  given Decoder[UpdateBook] = deriveDecoder
  given Encoder[UpdateBook] = deriveEncoder