package forex.domain

import cats.Show
import cats.data.NonEmptyList

sealed trait Currency

object Currency {

  case object AUD extends Currency
  case object CAD extends Currency
  case object CHF extends Currency
  case object EUR extends Currency
  case object GBP extends Currency
  case object NZD extends Currency
  case object JPY extends Currency
  case object SGD extends Currency
  case object USD extends Currency


  implicit val show: Show[Currency] = Show.show {
    case AUD => "AUD"
    case CAD => "CAD"
    case CHF => "CHF"
    case EUR => "EUR"
    case GBP => "GBP"
    case NZD => "NZD"
    case JPY => "JPY"
    case SGD => "SGD"
    case USD => "USD"
  }

  def allCurrencyPairs(): NonEmptyList[Rate.Pair] = {
    val list = List(Currency.AUD, Currency.CAD, Currency.CHF, Currency.EUR, Currency.GBP, Currency.NZD, Currency.JPY, Currency.SGD, Currency.USD)
    val pairedList = for {
      (x, idxX) <- list.zipWithIndex
      (y, idxY) <- list.zipWithIndex
      if idxX < idxY
    } yield (x, y)

    val y = for {x <- pairedList} yield Rate.Pair(x._1, x._2)
    NonEmptyList.fromList(y).get
  }

  def fromString(s: String): Currency = s.toUpperCase match {
    case "AUD" => AUD
    case "CAD" => CAD
    case "CHF" => CHF
    case "EUR" => EUR
    case "GBP" => GBP
    case "NZD" => NZD
    case "JPY" => JPY
    case "SGD" => SGD
    case "USD" => USD
  }

}
