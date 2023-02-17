package forex.services.rates.interpreters

import forex.services.rates.Algebra
import cats.Applicative
import cats.data.NonEmptyList
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.{Price, Rate, Timestamp}
import forex.services.rates.errors._

class OneFrameDummy[F[_]: Applicative] extends Algebra[F] {

  override def get(pair: Rate.Pair): F[AppError Either Rate] =
    Rate(pair, Price(BigDecimal(100)), Timestamp.now).asRight[AppError].pure[F]

  override def getAllPairs(allPairs: NonEmptyList[Rate.Pair]): F[Either[AppError, NonEmptyList[Rate]]] =
    allPairs.map(pair => Rate(pair, Price(BigDecimal(100)), Timestamp.now)).asRight[AppError].pure[F]
}
