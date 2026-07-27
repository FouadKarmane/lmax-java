package com.karmane.lmax;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LMAX Java Project Configuration Test Suite")
class SampleTest {

    @Test
    @DisplayName("Basic Assertion - Verify JUnit 5 Framework runs")
    void testBasicAssertion() {
        int expected = 42;
        int actual = Integer.sum(40, 2);

        assertEquals(expected, actual, "The math operations should match!");
    }

    @ParameterizedTest
    @ValueSource(strings = {"lmax", "java", "junit"})
    @DisplayName("Parameterized Test - Verify advanced JUnit 5 features work")
    void testWithParameters(String word) {
        assertTrue(word.length() > 0, "String should not be empty");
    }
}
