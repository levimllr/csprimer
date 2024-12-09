package org.odl

fun main(number: Long) {
  val isValid = isValidLuhn(number)
  if (isValid) {
    println("$number is a valid Luhn number.")
  } else {
    println("$number is not a valid Luhn number.")
  }
}

fun isValidLuhn(number: Long): Boolean {
  val numberList = number.toDigits()
  val expectedCheckDigit = numberList.last()
  val otherDigits = numberList.subList(0, numberList.size - 2).reversed().toMutableList()

  for (i in 0..otherDigits.size step 2) {
    otherDigits[i] = otherDigits[i] * 2
  }
  val sum = otherDigits.reduce { a, b -> a + b }
  val actualCheckDigit = (10 - sum % 10) % 10

  return expectedCheckDigit == actualCheckDigit
}

fun Long.toDigits(): List<Int> = toString().map { it.toString().toInt() }
