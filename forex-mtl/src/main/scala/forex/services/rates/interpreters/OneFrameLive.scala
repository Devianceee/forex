package forex.services.rates.interpreters

import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import forex.config.OneFrameConfig
import forex.domain.Rate
import forex.services.rates.Protocol.{OneFrame, OneFrameResponse}
import forex.services.rates.{Algebra, errors}
import org.http4s.Method.GET
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client

class OneFrameLive[F[_]: Sync](config: OneFrameConfig, client: Client[F]) extends Algebra[F] {

  implicit val oneFrameEntityDecoder: EntityDecoder[F, List[OneFrame]] = jsonOf[F, List[OneFrame]]

  override def get(pair: Rate.Pair): F[Either[errors.Error, Rate]] = {
    // Uri.fromString gets a ParseResult which gives an Either,
    // meaning we have to use liftTo to convert from Either[ParseError, A] -> F[Uri] which allows us to work with it
//    Uri.fromString("http://" + config.host + ":" + config.port).liftTo[F].flatMap { uri =>
    Uri.fromString("http://localhost:8080").liftTo[F].flatMap { uri =>
      val uriWithParams = uri.withPath("/rates").withQueryParam("pair", pair.from.toString + pair.to.toString)
      val tokenHeader = Headers.of(Header("token", config.token))
      val request = Request[F](method = GET, uri = uriWithParams, headers = tokenHeader)

      // used to using "expect" then "run" so this took me a while, wasn't use how to use "expect" here as the either needed to be manually handled
      client.run(request).use { response =>
        for {
          oneFrame <- response.asJsonDecode[OneFrameResponse] // this took ages to fix because i accidently renamed timeStamp to timestamp which messed up the json
        } yield Either.right[errors.Error, Rate](oneFrame.rates.map(_.rate).head)
      }
    }
  }

  override def getAllPairs(pairs: NonEmptyList[Rate.Pair]): F[Either[errors.Error, NonEmptyList[Rate]]] = {
    Uri.fromString("http://localhost:8080").liftTo[F].flatMap { uri =>
      val uriWithParams = uri.withPath("/rates").withMultiValueQueryParams(Map("pair" -> pairs.map(x => x.from.toString + x.to.toString).toList))
      val tokenHeader = Headers.of(Header("token", config.token))
      val request = Request[F](method = GET, uri = uriWithParams, headers = tokenHeader)

      // used to using "expect" then "run" so this took me a while, wasn't use how to use "expect" here as the either needed to be manually handled
      //      client.run(request).use { _ =>
      client.run(request).use { response =>
        for {
          oneFrame <- response.asJsonDecode[NonEmptyList[OneFrame]]
        } yield Either.right[errors.Error, NonEmptyList[Rate]](oneFrame.map(_.rate))
      }
    }
  }
}
