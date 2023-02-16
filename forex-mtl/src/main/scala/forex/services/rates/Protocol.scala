package forex.services.rates

import cats.data.NonEmptyList
import forex.domain.{Currency, Price, Rate, Timestamp}
import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveUnwrappedDecoder}

object Protocol {
  // circe decoder to be put here
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val currencyDecoder: Decoder[Currency] = Decoder.decodeString.map(Currency.fromString)
  implicit val timestampDecoder: Decoder[Timestamp] = deriveUnwrappedDecoder[Timestamp]
  implicit val priceDecoder: Decoder[Price] = deriveUnwrappedDecoder[Price]
  implicit val oneFrameDecoder: Decoder[OneFrame] = deriveConfiguredDecoder[OneFrame]
  implicit val oneFrameResponseDecoder: Decoder[OneFrameResponse] = deriveUnwrappedDecoder[OneFrameResponse]

  case class OneFrameResponse(rates: NonEmptyList[OneFrame])

  case class OneFrame(from: Currency, to: Currency, bid: BigDecimal, ask: BigDecimal, price: Price, timeStamp: Timestamp) {
    def rate: Rate = Rate(Rate.Pair(from, to), price, timeStamp)
  }
}
