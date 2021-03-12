package utils

import akka.actor.typed.ActorRef
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.stream.{Materializer, OverflowStrategy}

import scala.concurrent.ExecutionContext

object StreamUtils {

  def actorFlow[In, B, Out](
      inputTransform: In => B,
      inputRef: ActorRef[Out] => ActorRef[B],
      inputOnCompleteMessage: B,
      inputOnFailureMessage: Throwable => B,
      outputCompletionMatcher: PartialFunction[Out, Unit] = PartialFunction.empty,
      outputFailureMatcher: PartialFunction[Out, Throwable] = PartialFunction.empty,
      outputBufferSize: Int = 16,
      outputOverflowStrategy: OverflowStrategy = OverflowStrategy.dropNew
  )(implicit mat: Materializer, ec: ExecutionContext): Flow[In, Out, _] = {
    val (outputChannel, publisher) = ActorSource
      .actorRef[Out](outputCompletionMatcher, outputFailureMatcher, outputBufferSize, outputOverflowStrategy)
      .toMat(Sink.asPublisher(false))(Keep.both)
      .run()

    Flow.fromSinkAndSourceCoupled(
      Flow
        .fromFunction[In, B](inputTransform)
        .to(ActorSink.actorRef[B](inputRef(outputChannel), inputOnCompleteMessage, inputOnFailureMessage)),
      Source.fromPublisher(publisher)
    )
  }
}
