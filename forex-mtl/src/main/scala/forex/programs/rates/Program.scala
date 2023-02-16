package forex.programs.rates

import cats.Functor
import cats.data.EitherT
import errors._
import forex.domain._
import forex.services.RatesService

class Program[F[_]: Functor](
    ratesService: RatesService[F]
) extends Algebra[F] {

  protected val cache: Map[Rate.Pair, Rate] = Map.empty

  // can cache here, call getAllPairs once cache expires every 5 mins (probably best to use a Map)
  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {
    if (cache.isEmpty) {
      cache
    }
    EitherT(ratesService.get(Rate.Pair(request.from, request.to))).leftMap(toProgramError(_)).value
  }

}

object Program {

  def apply[F[_]: Functor](
      ratesService: RatesService[F]
  ): Algebra[F] = new Program[F](ratesService)

}
