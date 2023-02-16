package forex.services.rates

import cats.Applicative
import cats.effect._
import interpreters._
import forex.config._
import org.http4s.client.Client

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F]()
  def live[F[_]: Sync](oneFrameConfig: OneFrameConfig, client: Client[F]): Algebra[F] = new OneFrameLive[F](oneFrameConfig, client)
}
