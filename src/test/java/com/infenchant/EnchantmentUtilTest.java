package com.infenchant;

import com.infenchant.gui.EnchantmentCategory;
import com.infenchant.util.EnchantmentUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnchantmentUtilTest {

    @Test
    @DisplayName("Level formatting correctly formats Roman numerals and numbers")
    void testFormatLevel() {
        assertEquals("I", EnchantmentUtil.formatLevel(1));
        assertEquals("V", EnchantmentUtil.formatLevel(5));
        assertEquals("X", EnchantmentUtil.formatLevel(10));
        assertEquals("50", EnchantmentUtil.formatLevel(50));
        assertEquals("225", EnchantmentUtil.formatLevel(225));
        assertEquals("255", EnchantmentUtil.formatLevel(255));
    }

    @Test
    @DisplayName("Category next and previous cycle properly")
    void testCategoryCycle() {
        EnchantmentCategory all = EnchantmentCategory.ALL;
        EnchantmentCategory next = all.next();
        assertEquals(EnchantmentCategory.WEAPONS, next);

        EnchantmentCategory prev = all.previous();
        assertEquals(EnchantmentCategory.MISC, prev);
    }
}

