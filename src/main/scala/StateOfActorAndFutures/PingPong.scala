package StateOfActorAndFutures

import akka.actor._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

case class End(receivedPings: Int)
case class GetPongSum(sum: Option[Int])
case class ThrowException()

object PingPong extends App{
  val system = ActorSystem("ActorSystem")
  val pinger = system.actorOf(Props[Ping](),"Ping")
  pinger ! "start"
}

class Ping extends Actor {
  var sum = 0
  val ponger = context.actorOf(Props(classOf[Pong]))
  def receive: Receive = {
    case "start" =>
      for (x <- 1 to 10000){
      ponger ! "ping"
      }
    case "pong" => sum += 1
    case End(s) => println("Sum: "+ s)
    case GetPongSum(s) =>
      ponger ! GetPongSum(None)
      sender() ! End(sum)
      ponger ! ThrowException
      ponger ! GetPongSum(None)

  }
}

class Pong extends Actor{
  var sum = 0
  def doWork(): Int = {
      1
    }
  def receive: Receive = {
    case ThrowException => Try(throw new Exception)
    case End(counter) =>
      println("Count: " + counter)
    case GetPongSum(sum) => println(sum)
    case "ping" =>   val future: Future[Int] = Future {
      sum += doWork()
      sum
    }
    Await.result(future, Duration.Inf)
    if( sum < 10000)
    sender() ! "pong"
    else{
      sender() ! End(sum)
      sender() ! GetPongSum(Some(sum))
    }
  }
}
