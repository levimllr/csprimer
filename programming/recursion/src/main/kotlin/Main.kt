package org.odl

fun main() {
  println("Hello World!")
}

fun calculateFactorial(n: Int): Int {
  if (n == 1) return 1
  return n * calculateFactorial(n - 1)
}

fun isPalindrome(letters: String): Boolean {
  val chars = letters.filter { it.isLetter() }.map { it.lowercaseChar() }
  return if (chars.size == 1) {
    true
  } else if (chars.size == 2) {
    chars[0] == chars[1]
  } else if (chars[0] != chars[chars.lastIndex]) {
    false
  } else {
    isPalindrome(chars.joinToString("").slice(1 until chars.lastIndex))
  }
}

fun determineGreatestCommonDivisor(a: Int, b: Int): Int {
  val bigger = if (a > b) a else b
  val smaller = if (a < b) a else b
  val remainder = bigger % smaller
  return if (remainder == 0) {
    smaller
  } else {
    determineGreatestCommonDivisor(smaller, remainder)
  }
}

fun <T> List<T>.filterRecursive(filterFn: (T) -> Boolean): List<T> {
  if (this.isEmpty()) {
    return this
  }
  val subList = this.slice(0 until this.lastIndex).filterRecursive(filterFn).toMutableList()
  if (filterFn(this.last())) {
    subList.add(this.last())
  }
  return subList
}

fun <T, O> List<T>.reduceRecursive(
  operation: (element: T, accumulator: O) -> O,
  initialValue: O
): O =
  if (this.size == 1) {
    operation(this[0], initialValue)
  } else {
    operation(this[0], this.slice(1..this.lastIndex).reduceRecursive(operation, initialValue))
  }

fun <T> List<T>.reduceRecursive(operation: (element: T, accumulator: T) -> T): T =
  if (this.size == 2) {
    operation(this[0], this[1])
  } else {
    operation(this[0], this.slice(1..this.lastIndex).reduceRecursive(operation))
  }

fun calculateRabbitPairs(n: Int): Int =
  if (n < 3) {
    1
  } else {
    calculateRabbitPairs(n - 1) + calculateRabbitPairs(n - 2)
  }
