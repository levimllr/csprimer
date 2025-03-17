package org.odl

fun main() {
  println("Hello World!")
}

fun <T, O> List<T>.myReduce(operation: (element: T, accumulator: O) -> O, initialValue: O): O {
  val iterator = this.iterator()
  var accumulator = initialValue
  while (iterator.hasNext()) {
    accumulator = operation(iterator.next(), accumulator)
  }
  return accumulator
}

fun <T> List<T>.myReduce(operation: (element: T, accumulator: T) -> T): T {
  val iterator = this.iterator()
  var accumulator = iterator.next()
  while (iterator.hasNext()) {
    accumulator = operation(iterator.next(), accumulator)
  }
  return accumulator
}

fun product(nums: List<Double>) = nums.myReduce({ a, b -> a * b }, 1.0)

fun product(nums: List<Int>) = nums.myReduce({ a, b -> a * b }, 1)

fun <T> List<T>.myMap(op: (el: T) -> T): List<T> {
  val map = fun(el: T, list: MutableList<T>): MutableList<T> {
    list.add(op(el))
    return list
  }
  return this.myReduce(
    map,
    mutableListOf()
  )
}

fun <T> List<T>.myFilter(op: (el: T) -> Boolean): List<T> {
  val filter = fun(el: T, list: MutableList<T>): MutableList<T> {
    if (op(el)) list.add(el)
    return list
  }
  return this.myReduce(
    filter,
    mutableListOf()
  )
}

fun <T> List<T>.myZip(vararg lists: List<T>): List<List<T>> {
  val listsList = mutableListOf(this)
  for (list in lists) {
    if (list.size != this.size) {
      throw IllegalArgumentException("Can only zip lists of the same size.")
    }
    listsList.add(list)
  }
  val zippedLists = this.map { mutableListOf<T>() }

  val zip = fun(originalList: List<T>, zippedLists: List<MutableList<T>>): List<MutableList<T>> {
    originalList.forEachIndexed { index, element ->
      zippedLists[index].add(element)
    }
    return zippedLists
  }

  return listsList.myReduce(
    zip,
    zippedLists
  )
}
