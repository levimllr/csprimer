import org.odl.myFilter
import org.odl.myMap
import org.odl.myReduce
import org.odl.myZip
import org.odl.product
import kotlin.test.Test
import kotlin.test.assertEquals

class MainTests {

  @Test
  fun reduceAdd() {
    assertEquals(
      10,
      listOf(1, 2, 3, 4).myReduce { a: Int, b: Int -> a + b }
    )
  }

  @Test
  fun reduceMap() {
    val fn = fun(a: String, b: MutableMap<String, String>): MutableMap<String, String> {
      b[a] = a
      return b
    }
    assertEquals(
      mapOf(Pair("foo", "foo"), Pair("bar", "bar")),
      listOf("foo", "bar").myReduce(fn, mutableMapOf())
    )
  }

  @Test
  fun product1() {
    assertEquals(
      6,
      product(listOf(2, 3))
    )
  }

  @Test
  fun product2() {
    assertEquals(
      -1.0,
      product(listOf(-1.0, -1.0, -1.0))
    )
  }

  @Test
  fun product3() {
    assertEquals(
      1,
      product(listOf<Int>())
    )
  }

  @Test
  fun mapInt() {
    assertEquals(
      listOf(1, 4, 9, 16),
      listOf(1, 2, 3, 4).myMap { a: Int -> a * a }
    )
  }

  @Test
  fun mapStr() {
    assertEquals(
      listOf("A", "B", "C"),
      listOf("a", "b", "c").myMap { a: String -> a.uppercase() }
    )
  }

  @Test
  fun filterInt() {
    assertEquals(
      listOf(3, 5),
      listOf(-1, 3, -2, 5).myFilter { a: Int -> a > 0 }
    )
  }

  @Test
  fun filterFalse() {
    assertEquals(
      listOf(),
      listOf(1, 2).myFilter { false }
    )
  }

  @Test
  fun zip() {
    assertEquals(
      listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)),
      listOf(1, 4, 7).myZip(listOf(2, 5, 8), listOf(3, 6, 9))
    )
  }
}