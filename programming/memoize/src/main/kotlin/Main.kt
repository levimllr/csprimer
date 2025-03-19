package org.odl

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.time.measureTime

fun main() {
  val fibNum = 45
  val fibTimeTaken = measureTime {
    println("Fibonacci Number ${fibNum}: ${fib(fibNum)}")
  }
  println("fib took $fibTimeTaken to determine Fibonacci number $fibNum.")
  val memoFibTimeTaken = measureTime {
    println("Fibonacci Number ${fibNum}: ${memoizedFib(fibNum)}")
  }
  println("memoizedFib took $memoFibTimeTaken to determine Fibonacci number $fibNum.")

  val url = "http://www.google.com/"
  val numFetch = 10
  val fetchTimeTaken = measureTime {
    for (i in 0..numFetch) {
      fetch(url)
    }
  }
  println("fetch took $fetchTimeTaken to access $url $numFetch times.")
  val memoFetchTimeTaken = measureTime {
    for (i in 0..numFetch) {
      memoizedFetch(url)
    }
  }
  println("memoizedFetch took $memoFetchTimeTaken to access $url $numFetch times.")
}

fun fetch(url: String): String {
  val client = HttpClient.newHttpClient()
  val request = HttpRequest.newBuilder()
    .uri(URI.create(url))
    .GET()
    .build()
  val response = client.send(request, HttpResponse.BodyHandlers.ofString())
  return response.body()
}

fun fib(n: Int): Int {
  if (n <= 1) {
    return n
  }
  return fib(n - 1) + fib(n - 2)
}

val memoizedFetch = memo<String,String> { url ->
  val client = HttpClient.newHttpClient()
  val request = HttpRequest.newBuilder()
    .uri(URI.create(url))
    .GET()
    .build()
  val response = client.send(request, HttpResponse.BodyHandlers.ofString())
  return@memo response.body()
}

val memoizedFib = memo<Int, Int> { n ->
  if (n <= 1) {
    return@memo n
  }
  return@memo this(n - 1) + this( n - 2)
}

interface MemoScope<I, O> {
  operator fun invoke(i: I): O
}

abstract class MemoizedFunction<I, O> {
  private val cache = mutableMapOf<I, O>()
  private val memoScope = object : MemoScope<I, O> {
    override fun invoke(i: I): O = cache.getOrPut(i) { function(i) }
  }

  protected abstract fun MemoScope<I, O>.function(i: I): O

  fun execute(i: I): O = memoScope.invoke(i)
}

fun <I, O> (MemoScope<I, O>.(I) -> O).memoize(): (I) -> O {
  val memoizedFunction = object : MemoizedFunction<I, O>() {
    override fun MemoScope<I, O>.function(i: I): O = this@memoize(i)
  }
  return { i -> memoizedFunction.execute(i) }
}

fun <I, O> memo(fn: MemoScope<I, O>.(I) -> O): (I) -> O = fn.memoize()
