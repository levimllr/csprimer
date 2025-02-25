package org.odl

import kotlin.random.Random

enum class BoardState(val visual: String) {
  EMPTY(" "),
  X("X"),
  O("O")
}

val rowMap = mapOf("3", 0, "2", 1, "1", 2)
val columnMap = mapOf("A", 0, "B", 1, "C", 2)

// TODO: Is there a more functional way to do this? And, if so, what's the advantage?
val board = List(3) { List(3) { BoardState.EMPTY } }
val rowCoordinates = listOf("3", "2", "1")
val columnCoordinates = listOf("A", "B", "C")

var isPlayerXTurn = true
var isWinner = false

fun main() {
  println("Welcome to Tic-Tac-Toe!")
  determineWhoGoesFirst()
  while (!isWinner) {
    printBoard()
    println("Enter coordinate to play!")
    println("Ex: \"A1\"")
    val coordinate = readln()
    println("Turn: $coordinate")
    println("Player ${getCurrentPlayer()} claimed $coordinate")
    updateBoard()
    println()
  }
}

fun determineWhoGoesFirst() {
  isPlayerXTurn = Random.nextBoolean() == isPlayerXTurn
  val firstPlayer = if (isPlayerXTurn) {
    "X"
  } else {
    "O"
  }
  println("Player $firstPlayer goes first.")
}

fun printBoard() {
  println("Turn: Player ${getCurrentPlayer()}")
  println(rowDelimiter)
  print("|{${getCurrentPlayer()}}")
  for (coordinate in columnCoordinates) {
    print("| $coordinate ")
  }
  println("|")
  println(rowDelimiter)
  for ((y, row) in board.withIndex()) {
    print("| ${rowCoordinates[y]} ")
    for (column in row) {
      print("| ${column.visual} ")
    }
    println("|")
    print(rowDelimiter)
    println()
  }
}

fun getCurrentPlayer() = if (isPlayerXTurn) "X" else "O"

private val rowDelimiter = "+---+---+---+---+"