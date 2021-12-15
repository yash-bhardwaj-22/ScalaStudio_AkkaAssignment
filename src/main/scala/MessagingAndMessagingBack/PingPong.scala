package MessagingAndMessagingBack

import akka.actor._
import akka.event.{Logging, LoggingAdapter}

object PingPong extends App{
  val system = ActorSystem("ActorSystem")
  val pinger = system.actorOf(Props[Ping](),"Ping")
  pinger ! "start"
}

class Ping extends Actor {
  val log: LoggingAdapter = Logging(context.system, this)
  var count = 0
  def receive: Receive = {
    case "start" =>
      val ponger = context.actorOf(Props(classOf[Pong]))
      ponger ! "ping"
      count += 1
    case "pong" =>
      count += 1
      if (count > 20) {
        sender() ! "stop"
      }
      else {
        log.info("pong")
        sender() ! "ping"
      }
  }
}
class Pong extends Actor{
  val log: LoggingAdapter = Logging(context.system, this)
  override def receive: Receive = {
    case "ping" => log.info("ping")
      sender() ! "pong"
    case "stop" =>
      context.stop(self)
  }
}
