package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EventChanceTest {

    @Test
    void supportsFractionalPercentChances() {
        assertTrue(ClickingPlayer.shouldTriggerEvent(0.17, 0.0016));
        assertFalse(ClickingPlayer.shouldTriggerEvent(0.17, 0.0018));
    }

    @Test
    void respectsZeroAndFullPercentBoundaries() {
        assertFalse(ClickingPlayer.shouldTriggerEvent(0, 0));
        assertTrue(ClickingPlayer.shouldTriggerEvent(100, 0.999999));
    }
}
