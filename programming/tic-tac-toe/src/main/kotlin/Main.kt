package org.odl

import kotlin.random.Random

private const val rowDelimiter = "+---+---+---+"

private val moves = mutableListOf<Int>()
private val winningPlays = listOf(
  listOf(1, 2, 3),
  listOf(4, 5, 6),
  listOf(7, 8, 9),
  listOf(1, 4, 7),
  listOf(2, 5, 8),
  listOf(3, 6, 9),
  listOf(1, 5, 9),
  listOf(7, 5, 3),
)
private val isPlayerXFirst = Random.nextBoolean()

private var isWinner = false

fun main() {
  println("Welcome to Tic-Tac-Toe!\n")
  println("Player ${getCurrentPlayer()} goes first.")
  while (!isWinner) {
    println("Turn: Player ${determinePlayer(moves.size)}")
    printBoard()
    readCoordinate()
    isWinner = checkWinner()
    if (!isWinner) {
      println("Once more unto the breach...")
    }
  }
  printBoard()
  println("Thank you for playing.\n")
}

private fun printBoard() {
  println(rowDelimiter)
  for (i in 1..9) {
    if (moves.contains(i)) {
      print("| ${determinePlayer(moves.indexOf(i))} ")
    } else {
      print("| $i ")
    }
    if (i % 3 == 0) {
      println("|")
      print(rowDelimiter)
      println()
    }
  }
}

private fun determinePlayer(i: Int): String {
  return if (isPlayerXFirst && i % 2 == 0) {
    "X"
  } else if (!isPlayerXFirst && i % 2 == 0) {
    "O"
  } else if (!isPlayerXFirst && i % 2 == 1) {
    "X"
  } else {
    "O"
  }
}

private fun readCoordinate(): Int {
  println("Enter coordinate to play!")
  var isInputValid = false
  var input: Int? = 0
  while (!isInputValid) {
    input = readln().toIntOrNull()
    if (input == null || input < 1 || input > 9 || moves.contains(input)) {
      println("Invalid coordinate. Please enter a valid, available coordinate number between 1 and 9.")
    } else {
      moves.add(input)
      isInputValid = true
    }
  }
  return input!!
}

private fun checkWinner(): Boolean {
  val playerXMoves = if (isPlayerXFirst) {
    moves.filterIndexed { i, _ -> i % 2 == 0 }
  } else {
    moves.filterIndexed { i, _ -> (i + 1) % 2 == 0 }
  }
  val playerOMoves = if (isPlayerXFirst) {
    moves.filterIndexed { i, _ -> (i + 1) % 2 == 0 }
  } else {
    moves.filterIndexed { i, _ -> i % 2 == 0 }
  }
  for (play in winningPlays) {
    if (playerXMoves.containsAll(play)) {
      println("Player X won!!!")
      return true
    } else if (playerOMoves.containsAll(play)) {
      println("Player O won!!!")
      return true
    }
  }
  return false
}

private fun getCurrentPlayer() = if (isPlayerXFirst && moves.size % 2 == 0) "X" else "O"
