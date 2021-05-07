package cinnamon.asynctrace

object AsyncLocation {
  val skipPrefixes = Seq("cinnamon.asynctrace.", "java.", "scala.", "akka.")

  // TODO: use StackWalker --- full stack traces captured currently
  def capture(operator: String = null): AsyncLocation =
    AsyncLocation(Option(operator), new Exception().getStackTrace)

  def locationIndex(stackTrace: Array[StackTraceElement]): Int =
    stackTrace.indexWhere(element => !skipPrefixes.exists(prefix => element.getClassName.startsWith(prefix)))

  def codeLocation(operator: Option[String], stackTrace: Array[StackTraceElement]): String = {
    val index = locationIndex(stackTrace)
    if (index > 0)
      operator.getOrElse(stackTrace(index - 1).getMethodName) + " @ " + stackTrace(index).toString
    else ""
  }

  def stackTraceFromLocation(stackTrace: Array[StackTraceElement]): IndexedSeq[String] =
    stackTrace.drop(locationIndex(stackTrace)).map(_.toString)
}

case class AsyncLocation(operator: Option[String], stackTrace: Array[StackTraceElement]) {
  def location: String = AsyncLocation.codeLocation(operator, stackTrace)
  def traceFromLocation: IndexedSeq[String] = AsyncLocation.stackTraceFromLocation(stackTrace)
}

case class AsyncTrace(locations: IndexedSeq[AsyncLocation]) {
  def isEmpty: Boolean = locations.isEmpty
  def add(location: AsyncLocation): AsyncTrace = AsyncTrace(location +: locations)
  def trace: Seq[String] = locations.map(_.location).filter(_.nonEmpty) ++ locations.lastOption.fold(Seq.empty[String])(_.traceFromLocation)
  def print(message: String): Unit = println(message + trace.mkString("\n  at ", "\n  at ", ""))
}

object AsyncTrace {
  val empty: AsyncTrace = AsyncTrace(IndexedSeq.empty)

  private val localTrace = new ThreadLocal[AsyncTrace]()

  def current: AsyncTrace = {
    val trace = localTrace.get
    if (trace eq null) empty else trace
  }

  def activate(trace: AsyncTrace): Unit = {
    if (trace.isEmpty) localTrace.remove() else localTrace.set(trace)
  }

  def enter(trace: AsyncTrace): AsyncTrace = {
    val prior = current
    activate(trace)
    prior // instrumentation should reactivate the prior thread-local trace at end of scope
  }

  def enter(location: AsyncLocation, trace: AsyncTrace): AsyncTrace = {
    enter(trace.add(location))
  }
}
