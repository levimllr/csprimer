package org.odl

fun main(digits: String) {
  val isValid = isValidLuhn(digits)
  if (isValid) {
    println("$digits is a valid Luhn number.")
  } else {
    println("$digits is not a valid Luhn number.")
  }
}

fun isValidLuhn(digits: String): Boolean {
  val expectedCheckDigit = digits.last()

  val actualCheckDigit = digits
    .dropLast(1)
    .reversed()
    .map(Char::digitToInt)
    .mapIndexed { i, num ->
      val factor = 2 - i % 2
      num * factor / 10 + num * factor % 10
    }
    .sum()
    .run{ 10 - this % 10 }

  return expectedCheckDigit.digitToInt() == actualCheckDigit
}
