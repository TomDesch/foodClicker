package io.github.stealingdapenta.foodclicker.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FireworkCommandTest {

    @Test
    void acceptsOnlyBoundedFireworkCounts() {
        assertTrue(FireworkCommand.isValidAmount(1));
        assertTrue(FireworkCommand.isValidAmount(100));
        assertFalse(FireworkCommand.isValidAmount(0));
        assertFalse(FireworkCommand.isValidAmount(101));
    }

    @Test
    void acceptsOnlyBukkitFireworkPowerRange() {
        assertTrue(FireworkCommand.isValidPower(0));
        assertTrue(FireworkCommand.isValidPower(128));
        assertFalse(FireworkCommand.isValidPower(-1));
        assertFalse(FireworkCommand.isValidPower(129));
    }
}
