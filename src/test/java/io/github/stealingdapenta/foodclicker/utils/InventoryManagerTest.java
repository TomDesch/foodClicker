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
    void formatsEveryNamedMagnitudeAtItsBoundary() {
        String[] values = {
                "1000000",
                "1000000000",
                "1000000000000",
                "1000000000000000",
                "1000000000000000000",
                "1000000000000000000000",
                "1000000000000000000000000",
                "1000000000000000000000000000",
                "1000000000000000000000000000000",
                "1000000000000000000000000000000000",
                "1000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000000000000000000000",
                "1000000000000000000000000000000000000000000000000000000000000000"
        };
        String[] expected = {
                "1 Million",
                "1 Billion",
                "1 Trillion",
                "1 Quadrillion",
                "1 Quintillion",
                "1 Hextillion",
                "1 Septillion",
                "1 Octillion",
                "1 Nonillion",
                "1 Decillion",
                "1 Undecillion",
                "1 Duodecillion",
                "1 Tredecillion",
                "1 Quattuordecillion",
                "1 Quindecillion",
                "1 Hexdecillion",
                "1 Septendecillion",
                "1 Octodecillion",
                "1 Novemdecillion",
                "1 Vigintillion"
        };

        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i], inventoryManager.truncateNumber(new BigDecimal(values[i])));
        }
    }

    @Test
    void leavesNegativeAndSubMillionValuesUntruncated() {
        BigDecimal negative = new BigDecimal("-1000000000000000000000000000000000000000000000000000000000000000");
        BigDecimal subMillion = new BigDecimal("999999");

        assertEquals(inventoryManager.makeNumbersPretty(negative), inventoryManager.truncateNumber(negative));
        assertEquals(inventoryManager.makeNumbersPretty(subMillion), inventoryManager.truncateNumber(subMillion));
    }

    @Test
    void formatsVigintillionBoundary() {
        assertEquals(
                "1 Vigintillion",
                inventoryManager.truncateNumber(new BigDecimal("1000000000000000000000000000000000000000000000000000000000000000"))
        );
    }
}
