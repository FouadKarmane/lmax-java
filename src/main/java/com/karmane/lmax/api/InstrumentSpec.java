package com.karmane.lmax.api;

import java.util.Objects;

/**
 * Immutable price and quantity rules for one instrument.
 *
 * <p>Prices are represented as normalized integer units. The scale controls
 * their decimal display representation, while {@code tickSizeUnits} controls
 * which representable prices are tradable.</p>
 */
public final class InstrumentSpec {

    private static final int MAX_PRICE_SCALE = 18;

    private final String symbol;
    private final int priceScale;
    private final long tickSizeUnits;
    private final long minimumPriceUnits;
    private final long maximumPriceUnits;
    private final long maximumOrderQuantity;

    public InstrumentSpec(
            String symbol,
            int priceScale,
            long tickSizeUnits,
            long minimumPriceUnits,
            long maximumPriceUnits,
            long maximumOrderQuantity) {

        this.symbol = requireNonBlank(symbol, "symbol");
        this.priceScale = requireValidScale(priceScale);
        this.tickSizeUnits = requirePositive(tickSizeUnits, "tickSizeUnits");
        this.minimumPriceUnits = requirePositive(minimumPriceUnits, "minimumPriceUnits");
        this.maximumPriceUnits = requirePositive(maximumPriceUnits, "maximumPriceUnits");
        this.maximumOrderQuantity =
                requirePositive(maximumOrderQuantity, "maximumOrderQuantity");

        if (minimumPriceUnits > maximumPriceUnits) {
            throw new IllegalArgumentException(
                    "minimumPriceUnits must not exceed maximumPriceUnits");
        }
        if (!isOnTickGrid(minimumPriceUnits, tickSizeUnits)) {
            throw new IllegalArgumentException(
                    "minimumPriceUnits must be aligned to tickSizeUnits");
        }
        if (!isOnTickGrid(maximumPriceUnits, tickSizeUnits)) {
            throw new IllegalArgumentException(
                    "maximumPriceUnits must be aligned to tickSizeUnits");
        }
    }

    public String symbol() {
        return symbol;
    }

    public int priceScale() {
        return priceScale;
    }

    public long tickSizeUnits() {
        return tickSizeUnits;
    }

    public long minimumPriceUnits() {
        return minimumPriceUnits;
    }

    public long maximumPriceUnits() {
        return maximumPriceUnits;
    }

    public long maximumOrderQuantity() {
        return maximumOrderQuantity;
    }

    public boolean isValidPrice(long priceUnits) {
        return priceUnits >= minimumPriceUnits
                && priceUnits <= maximumPriceUnits
                && isOnTickGrid(priceUnits, tickSizeUnits);
    }

    public boolean isValidQuantity(long quantity) {
        return quantity > 0 && quantity <= maximumOrderQuantity;
    }

    private static boolean isOnTickGrid(long priceUnits, long tickSizeUnits) {
        return priceUnits % tickSizeUnits == 0;
    }

    private static String requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    private static int requireValidScale(int priceScale) {
        if (priceScale < 0 || priceScale > MAX_PRICE_SCALE) {
            throw new IllegalArgumentException(
                    "priceScale must be between 0 and " + MAX_PRICE_SCALE);
        }
        return priceScale;
    }

    private static long requirePositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }
}
