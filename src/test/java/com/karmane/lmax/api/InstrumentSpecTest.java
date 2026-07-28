package com.karmane.lmax.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstrumentSpecTest {

    private static final String SYMBOL = "TEST";
    private static final int PRICE_SCALE = 2;
    private static final long TICK_SIZE_UNITS = 25;
    private static final long MINIMUM_PRICE_UNITS = 25;
    private static final long MAXIMUM_PRICE_UNITS = 10_000_00;
    private static final long MAXIMUM_ORDER_QUANTITY = 1_000_000;

    @Test
    void exposesConfiguredInstrumentRules() {
        InstrumentSpec spec = newSpec();

        assertEquals(SYMBOL, spec.symbol());
        assertEquals(PRICE_SCALE, spec.priceScale());
        assertEquals(TICK_SIZE_UNITS, spec.tickSizeUnits());
        assertEquals(MINIMUM_PRICE_UNITS, spec.minimumPriceUnits());
        assertEquals(MAXIMUM_PRICE_UNITS, spec.maximumPriceUnits());
        assertEquals(MAXIMUM_ORDER_QUANTITY, spec.maximumOrderQuantity());
    }

    @ParameterizedTest
    @ValueSource(longs = {25, 100, 4312_25, 1_000_00})
    void acceptsPricesWithinBoundsAndOnTickGrid(long priceUnits) {
        assertTrue(newSpec().isValidPrice(priceUnits));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 5, 24, 30, 4312_30, 10_000_25, Long.MAX_VALUE})
    void rejectsPricesOutsideBoundsOrOffTickGrid(long priceUnits) {
        assertFalse(newSpec().isValidPrice(priceUnits));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 50, 1_000_000})
    void acceptsPositiveQuantitiesUpToConfiguredMaximum(long quantity) {
        assertTrue(newSpec().isValidQuantity(quantity));
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1, 0, 1_000_001, Long.MAX_VALUE})
    void rejectsNonPositiveOrExcessiveQuantities(long quantity) {
        assertFalse(newSpec().isValidQuantity(quantity));
    }

    @Test
    void rejectsNullSymbol() {
        assertThrows(
                NullPointerException.class,
                () -> new InstrumentSpec(
                        null,
                        PRICE_SCALE,
                        TICK_SIZE_UNITS,
                        MINIMUM_PRICE_UNITS,
                        MAXIMUM_PRICE_UNITS,
                        MAXIMUM_ORDER_QUANTITY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void rejectsBlankSymbol(String symbol) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentSpec(
                        symbol,
                        PRICE_SCALE,
                        TICK_SIZE_UNITS,
                        MINIMUM_PRICE_UNITS,
                        MAXIMUM_PRICE_UNITS,
                        MAXIMUM_ORDER_QUANTITY));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 19, Integer.MAX_VALUE})
    void rejectsUnsupportedPriceScale(int priceScale) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentSpec(
                        SYMBOL,
                        priceScale,
                        TICK_SIZE_UNITS,
                        MINIMUM_PRICE_UNITS,
                        MAXIMUM_PRICE_UNITS,
                        MAXIMUM_ORDER_QUANTITY));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveTickSize(long tickSizeUnits) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentSpec(
                        SYMBOL,
                        PRICE_SCALE,
                        tickSizeUnits,
                        MINIMUM_PRICE_UNITS,
                        MAXIMUM_PRICE_UNITS,
                        MAXIMUM_ORDER_QUANTITY));
    }

    @Test
    void rejectsInvertedPriceBounds() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentSpec(
                        SYMBOL,
                        PRICE_SCALE,
                        TICK_SIZE_UNITS,
                        MAXIMUM_PRICE_UNITS,
                        MINIMUM_PRICE_UNITS,
                        MAXIMUM_ORDER_QUANTITY));
    }

    @ParameterizedTest
    @ValueSource(longs = {26, 49})
    void rejectsPriceBoundsThatAreNotOnTickGrid(long misalignedBound) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentSpec(
                        SYMBOL,
                        PRICE_SCALE,
                        TICK_SIZE_UNITS,
                        misalignedBound,
                        MAXIMUM_PRICE_UNITS,
                        MAXIMUM_ORDER_QUANTITY));

        assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentSpec(
                        SYMBOL,
                        PRICE_SCALE,
                        TICK_SIZE_UNITS,
                        MINIMUM_PRICE_UNITS,
                        misalignedBound,
                        MAXIMUM_ORDER_QUANTITY));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveMaximumOrderQuantity(long maximumOrderQuantity) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentSpec(
                        SYMBOL,
                        PRICE_SCALE,
                        TICK_SIZE_UNITS,
                        MINIMUM_PRICE_UNITS,
                        MAXIMUM_PRICE_UNITS,
                        maximumOrderQuantity));
    }

    private static InstrumentSpec newSpec() {
        return new InstrumentSpec(
                SYMBOL,
                PRICE_SCALE,
                TICK_SIZE_UNITS,
                MINIMUM_PRICE_UNITS,
                MAXIMUM_PRICE_UNITS,
                MAXIMUM_ORDER_QUANTITY);
    }
}
