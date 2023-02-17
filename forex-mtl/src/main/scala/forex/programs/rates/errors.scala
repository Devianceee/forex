package forex.programs.rates

import forex.services.rates.errors.{ AppError => RatesServiceError }

object errors {
  // changed from Error to AppError so it doesn't clash with the default "Error" keyword (had many problems with this)
  sealed trait AppError extends Exception
  object AppError {
    final case class RateLookupFailed(msg: String) extends AppError
  }

  def toProgramError(error: RatesServiceError): AppError = error match {
    case RatesServiceError.OneFrameLookupFailed(msg) => AppError.RateLookupFailed(msg)
  }
}
