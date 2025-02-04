package forex.services.rates

import cats.data.NonEmptyList
import forex.domain.Rate
import errors._

trait Algebra[F[_]] {
  def get(pair: Rate.Pair): F[AppError Either Rate]
  def getAllPairs(pair: NonEmptyList[Rate.Pair]): F[AppError Either NonEmptyList[Rate]]
}
