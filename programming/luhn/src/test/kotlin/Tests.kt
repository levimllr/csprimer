import org.junit.jupiter.api.Test
import org.odl.isValidLuhn
import org.odl.toDigits
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class Tests {

  @Test
  fun luhn_true() {
    assertTrue(isValidLuhn(49927398716))
  }

  @Test
  fun luhn_false() {
    assertFalse(isValidLuhn(49927398717))
  }

  @Test
  fun toDigits() {
    val number: Long = 1234567890

    val actual = number.toDigits()

    val expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
    assertEquals(expected, actual)
  }
}