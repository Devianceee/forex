package forex.services.rates

object errors {
  // changed from Error to AppError so it doesn't clash with the default "Error" keyword (had many problems with this)
  sealed trait AppError
  object AppError {
    final case class OneFrameLookupFailed(msg: String) extends AppError
  }

}
