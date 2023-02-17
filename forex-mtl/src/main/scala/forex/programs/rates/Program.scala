package forex.programs.rates

import cats.Functor
import cats.data.EitherT
import forex.domain._
import forex.programs.rates.errors._
import forex.services.RatesService

class Program[F[_]: Functor](
    ratesService: RatesService[F]
) extends Algebra[F] {

  private var cache: Map[Rate.Pair, Rate] = Map.empty
//  private val foo = EitherT(ratesService.getAllPairs(Currency.allCurrencyPairs())).leftMap(toProgramError(_)).value

  // can cache here, call getAllPairs once cache expires every 5 mins (probably best to use a Map)
  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {
    println(Currency.allCurrencyPairs())
    if (cache.isEmpty) {
      // call ratesService.getAllPairs() and then fill in the map
    }
   // check if cache is up to date

//    EitherT(ratesService.getAllPairs(Currency.allCurrencyPairs())).leftMap(toProgramError(_)).value
    EitherT(ratesService.get(Rate.Pair(request.from, request.to))).leftMap(toProgramError(_)).value
  }

}

object Program {
  def apply[F[_]: Functor](
      ratesService: RatesService[F]
  ): Algebra[F] = new Program[F](ratesService)
}
