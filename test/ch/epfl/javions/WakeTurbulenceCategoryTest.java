package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WakeTurbulenceCategoryTest {

    @Test
    void ofTrivial() {
        var expected1 = WakeTurbulenceCategory.LIGHT;
        var actual1 = WakeTurbulenceCategory.of("L");
        assertEquals(expected1, actual1);

        var expected2 = WakeTurbulenceCategory.MEDIUM;
        var actual2 = WakeTurbulenceCategory.of("M");
        assertEquals(expected2, actual2);

        var expected3 = WakeTurbulenceCategory.HEAVY;
        var actual3 = WakeTurbulenceCategory.of("H");
        assertEquals(expected3, actual3);

        var expected4 = WakeTurbulenceCategory.UNKNOWN;
        var actual4 = WakeTurbulenceCategory.of("BIPBAPBOP");
        assertEquals(expected4, actual4);
    }
}