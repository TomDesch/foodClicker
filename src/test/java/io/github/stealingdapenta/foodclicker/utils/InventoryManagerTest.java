package io.github.stealingdapenta.foodclicker.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InventoryManagerTest {

    private final InventoryManager inventoryManager = InventoryManager.getInstance();

    @Test
    void formatsNovemdecillionBoundary() {
        assertEquals(
                "1 Novemdecillion",
                inventoryManager.truncateNumber(new BigDecimal("1000000000000000000000000000000000000000000000000000000000000"))
        );
    }

    @Test
    void formatsVigintillionBoundary() {
        assertEquals(
                "1 Vigintillion",
                inventoryManager.truncateNumber(new BigDecimal("1000000000000000000000000000000000000000000000000000000000000000"))
        );
    }
}
