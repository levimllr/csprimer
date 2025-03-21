import org.junit.jupiter.api.Test
import org.odl.calculateFactorial
import org.odl.determineGreatestCommonDivisor
import org.odl.filterRecursive
import org.odl.isPalindrome
import org.odl.calculateRabbitPairs
import org.odl.reduceRecursive
import kotlin.test.assertEquals

class Tests {

  @Test
  fun factorial() {
    assertEquals(6, calculateFactorial(3))
    assertEquals(3628800, calculateFactorial(10))
  }

  @Test
  fun palindrome() {
    assertEquals(true, isPalindrome("racecar"))
    assertEquals(false, isPalindrome("hello"))
    assertEquals(true, isPalindrome("A man, a plan, a canal — Panama!"))
  }

  @Test
  fun gcd() {
    assertEquals(21, determineGreatestCommonDivisor(1071, 462))
    assertEquals(3, determineGreatestCommonDivisor(15, 9))
    assertEquals(3, determineGreatestCommonDivisor(9, 15))
    assertEquals(1, determineGreatestCommonDivisor(3, 5))
  }

  @Test
  fun filterRecursive() {
    assertEquals(listOf(3, 5), listOf(-1, 3, -2, 5).filterRecursive { x: Int -> x > 0 })
    assertEquals(listOf(), listOf(1, 2).filterRecursive({ _ -> false }))
  }

  @Test
  fun reduce() {
    assertEquals(10, listOf(1, 2, 3, 4).reduceRecursive { a, b -> a + b })
    assertEquals(24, listOf(1, 2, 3, 4).reduceRecursive { a, b -> a * b })
    assertEquals("abcd", listOf("a", "b", "c", "d").reduceRecursive { a, b -> a.plus(b) })
    val mirror = fun(a: String, b: MutableMap<String, String>): MutableMap<String, String> {
      b[a] = a
      return b
    }
    assertEquals(mapOf(Pair("foo", "foo"), Pair("bar", "bar")),
      listOf("foo", "bar").reduceRecursive(mirror, mutableMapOf()))
  }

  @Test
  fun rabbitPairs() {
    assertEquals(1, calculateRabbitPairs(1))
    assertEquals(1, calculateRabbitPairs(2))
    assertEquals(2, calculateRabbitPairs(3))
    assertEquals(3, calculateRabbitPairs(4))
    assertEquals(5, calculateRabbitPairs(5))
    assertEquals(8, calculateRabbitPairs(6))
    assertEquals(6765, calculateRabbitPairs(20))
  }
}