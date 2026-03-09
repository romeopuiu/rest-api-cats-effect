package bookstore.domain


sealed trait DomainError extends Product with Serializable

object DomainError:
  case object BookNotFound extends DomainError
  case object InvalidInput extends DomainError
  case object DatabaseError extends DomainError