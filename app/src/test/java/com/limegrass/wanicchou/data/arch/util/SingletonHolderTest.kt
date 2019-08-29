package com.limegrass.wanicchou.data.arch.util

import data.arch.util.SingletonHolder
import org.junit.Test
import kotlin.test.*

class SingletonHolderTest {
    @Test
    fun `getInstance returns the same object`() {
        val firstInstance = TestSingleton("Test")
        val secondInstance = TestSingleton("NotATest")
        assertSame(firstInstance, secondInstance)
    }


    private class TestSingleton {
        companion object : SingletonHolder<TestSingleton, String>({
            TestSingleton()
        }) {
            operator fun invoke(value : String) : TestSingleton {
                return getInstance(value)
            }
        }
    }
}