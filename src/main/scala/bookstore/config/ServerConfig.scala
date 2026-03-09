package bookstore.config

import com.comcast.ip4s.{Host, Port, host, port}

object ServerConfig:
  val Host: Host = host"0.0.0.0"
  val Port: Port = port"8080"
