package forex.programs.rates

import cats.Monad
import cats.data.EitherT
import cats.implicits._
import forex.domain.Timestamp.toEpochSeconds
import forex.domain._
import forex.programs.rates.errors._
import forex.services.RatesService

// Turning F into a Monad because I need the flatMap method
class Program[F[_]: Monad](
    ratesService: RatesService[F]
) extends Algebra[F] {

  private var cache: Map[Rate.Pair, Rate] = Map.empty

  override def get(request: Protocol.GetRatesRequest): F[errors.Error Either Rate] = {

    // check if cache is empty which would only happen on first run
    if (cache.isEmpty) {
      println(s"Cache empty - ${Timestamp.now}")
      val result = for {
        rates <- EitherT(ratesService.getAllPairs(Currency.allCurrencyPairs())).leftMap(toProgramError(_))
        _ = rates.map { y => cache += (y.pair -> y) }
      } yield cache(Rate.Pair(request.from, request.to))
      result.value
    }
      // check if cache is up to date
    else {
      if (toEpochSeconds(Timestamp.now) >= (toEpochSeconds(cache(Rate.Pair(request.from, request.to)).timestamp) + 300)) {
        println(s"Cache expired - ${Timestamp.now}")
        val result = for {
          rates <- EitherT(ratesService.getAllPairs(Currency.allCurrencyPairs())).leftMap(toProgramError(_))
          _ = rates.map { y => cache += (y.pair -> y) }
        } yield cache(Rate.Pair(request.from, request.to))
        result.value
      }
      else {
        println(s"Cache up to date - ${Timestamp.now}")
        cache(Rate.Pair(request.from, request.to)).asRight[errors.Error].pure[F]
      }
    }
  }
}

object Program {
  def apply[F[_]: Monad](
      ratesService: RatesService[F]
  ): Algebra[F] = new Program[F](ratesService)
}
