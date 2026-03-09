package bookstore.config

import cats.effect.{Async, Resource}
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor

object Database:

  def transactor[F[_] : Async]: Resource[F, HikariTransactor[F]] =
    for
      config <- Resource.eval(Async[F].delay {
        val hikariConfig = new HikariConfig()
        hikariConfig.setDriverClassName("org.postgresql.Driver")
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/book-cats-effect")
        hikariConfig.setUsername("postgres")
        hikariConfig.setPassword("admin1234")
        hikariConfig.setMaximumPoolSize(10)
        hikariConfig
      })
      transactor <- HikariTransactor.fromHikariConfig[F](config, None)
    yield transactor