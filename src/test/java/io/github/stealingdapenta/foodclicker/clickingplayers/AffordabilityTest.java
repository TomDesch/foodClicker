package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AffordabilityTest {

    @Test
    void acceptsNumericallyEqualAmountsWithDifferentScales() {
        assertTrue(ClickingPlayer.hasAtLeast(new BigDecimal("10.0"), new BigDecimal("10.00")));
    }

    @Test
    void acceptsLargerBalanceAndRejectsSmallerBalance() {
        assertTrue(ClickingPlayer.hasAtLeast(new BigDecimal("10.01"), new BigDecimal("10.00")));
        assertFalse(ClickingPlayer.hasAtLeast(new BigDecimal("9.99"), new BigDecimal("10.00")));
    }
}
