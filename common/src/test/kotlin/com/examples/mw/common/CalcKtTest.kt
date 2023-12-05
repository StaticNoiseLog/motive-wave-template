package com.examples.mw.common

import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class CalcKtTest {

    @Test
    fun max() {
        assertSame(0, mymax(0, 0))
    }
}