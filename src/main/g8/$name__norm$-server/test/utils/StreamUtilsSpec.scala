package utils

import akka.actor.ActorSystem
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.freespec.AnyFreeSpecLike
import utils.StreamUtils._

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

class StreamUtilsSpec extends ScalaTestWithActorTestKit with AnyFreeSpecLike {

  implicit val classicSystem: ActorSystem = system.classicSystem

  "A Flow returned by actorFlow" - {

    "should transform stream elements according to the inputTransform function" in {
      val flowUnderTest = actorFlow[Int, Int, Int](
        inputTransform = _ * 2,
        inputRef = ref =>
          Future.successful {
            testKit.spawn(Behaviors.receiveMessage[Int] {
              case m @ Integer.MAX_VALUE => ref ! m; Behaviors.stopped
              case m                     => ref ! m; Behaviors.same
            })
          },
        inputOnCompleteMessage = Integer.MAX_VALUE,
        inputOnFailureMessage = _ => Integer.MIN_VALUE,
        outputCompletionMatcher = { case Integer.MAX_VALUE => },
        outputFailureMatcher = PartialFunction.empty
      )

      Source(Seq(1, 2, 3))
        .via(Flow.futureFlow(flowUnderTest))
        .runWith(TestSink.probe[Int])
        .request(3)
        .expectNext(2, 4, 6)
    }

    "when the stream completes successfully" - {

      "should send the inputOnCompleteMessage to the actor created via inputRef" in {
        val flowUnderTest = actorFlow[Int, Int, Int](
          inputTransform = identity,
          inputRef = ref =>
            Future.successful {
              testKit.spawn(Behaviors.receiveMessage[Int] {
                case m @ Integer.MAX_VALUE => ref ! m; Behaviors.stopped
              })
            },
          inputOnCompleteMessage = Integer.MAX_VALUE,
          inputOnFailureMessage = _ => Integer.MIN_VALUE,
          outputCompletionMatcher = { case Integer.MAX_VALUE => },
          outputFailureMatcher = PartialFunction.empty
        )

        Source
          .empty[Int]
          .via(Flow.futureFlow(flowUnderTest))
          .runWith(TestSink.probe[Int])
          .expectSubscriptionAndComplete()
      }
    }

    "when the stream completes with failure" - {

      "should send a message created by inputOnFailureMessage to the actor created by inputRef" in {
        val exception = new Exception("Expected exception")
        val flowUnderTest = actorFlow[Int, Int, Int](
          inputTransform = identity,
          inputRef = ref =>
            Future.successful {
              testKit.spawn(Behaviors.receiveMessage[Int] {
                case m @ Integer.MIN_VALUE => ref ! m; Behaviors.stopped
              })
            },
          inputOnCompleteMessage = Integer.MAX_VALUE,
          inputOnFailureMessage = _ => Integer.MIN_VALUE,
          outputCompletionMatcher = { case Integer.MAX_VALUE => },
          outputFailureMatcher = _ => exception
        )

        Source
          .failed[Int](new Exception)
          .via(Flow.futureFlow(flowUnderTest))
          .runWith(TestSink.probe[Int])
          .expectSubscriptionAndError(exception)
      }
    }

    "when the output actor is sent a message that is matched by outputCompletionMatcher" - {

      "should successfully complete the stream" in {
        val flowUnderTest = actorFlow[Int, Int, Int](
          inputTransform = identity,
          inputRef = ref =>
            Future.successful(testKit.spawn(Behaviors.receiveMessage[Int] { _ =>
              ref ! Integer.MAX_VALUE; Behaviors.same
            })),
          inputOnCompleteMessage = Integer.MAX_VALUE,
          inputOnFailureMessage = _ => Integer.MIN_VALUE,
          outputCompletionMatcher = { case Integer.MAX_VALUE => },
          outputFailureMatcher = PartialFunction.empty
        )

        Source
          .single(1)
          .via(Flow.futureFlow(flowUnderTest))
          .runWith(TestSink.probe[Int])
          .expectSubscriptionAndComplete()
      }
    }

    "when the output actor is sent a message that is matched by outputFailureMatcher" - {

      "should complete the stream with an error" in {
        val exception = new Exception("Expected exception")
        val flowUnderTest = actorFlow[Int, Int, Int](
          inputTransform = identity,
          inputRef = ref =>
            Future.successful(testKit.spawn(Behaviors.receiveMessage[Int] { _ =>
              ref ! Integer.MIN_VALUE; Behaviors.same
            })),
          inputOnCompleteMessage = Integer.MAX_VALUE,
          inputOnFailureMessage = _ => Integer.MIN_VALUE,
          outputCompletionMatcher = { case Integer.MAX_VALUE => },
          outputFailureMatcher = { case Integer.MIN_VALUE    => exception }
        )

        Source
          .single(1)
          .via(Flow.futureFlow(flowUnderTest))
          .runWith(TestSink.probe[Int])
          .expectSubscriptionAndError(exception)
      }
    }
  }
}
