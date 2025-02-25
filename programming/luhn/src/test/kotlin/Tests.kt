import org.junit.jupiter.api.Test
import org.odl.isValidLuhn
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class Tests {

  @Test
  fun luhn_true() {
    assertTrue(isValidLuhn("17893729974"))
  }

  @Test
  fun luhn_false() {
    assertFalse(isValidLuhn("17893729975"))
  }
}