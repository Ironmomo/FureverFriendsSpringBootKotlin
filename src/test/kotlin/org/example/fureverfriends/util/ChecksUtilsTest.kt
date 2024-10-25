package org.example.fureverfriends.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigInteger

class ChecksUtilsTest {

    @Nested
    inner class CheckNullTests {
        @Test
        fun `should throw error if not null`() {
            val obj = BigInteger.valueOf(1)

            val throwable = catchThrowable { checkExpectNull(obj) {"Is not null"} }

            assertThat(throwable).isExactlyInstanceOf(IllegalStateException::class.java).hasMessageContaining("Is not null")
        }

        @Test
        fun `should not throw error if null`() {
            val obj = null

            val throwable = catchThrowable { checkExpectNull(obj) {"Is not null"} }

            assertThat(throwable).isNull()
        }
    }
}