package forex.config

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(
    http: HttpConfig,
    oneFrame: OneFrameConfig
)

case class OneFrameConfig(
    host: String,
    port: Int,
    token: String
)

case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration
)
