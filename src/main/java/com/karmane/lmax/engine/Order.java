package com.karmane.lmax.engine;

import com.karmane.lmax.api.Side;

import java.util.Objects;

/**
 * Engine-owned state for an accepted limit order.
 *
 * <p>Prices are represented as normalized integer units. For the Milestone 1
 * instrument, one unit is one valid tick. Time priority is defined by
 * {@code arrivalSequence}, not by the informational arrival timestamp.</p>
 */
final class Order {

    private final long orderId;
    private final Side side;
    private final long priceUnits;
    private final long originalQuantity;
    private final long arrivalSequence;
    private final long receivedAtEpochNanos;

    private long remainingQuantity;

    Order(
            long orderId,
            Side side,
            long priceUnits,
            long quantity,
            long arrivalSequence,
            long receivedAtEpochNanos) {

        this.orderId = requirePositive(orderId, "orderId");
        this.side = Objects.requireNonNull(side, "side");
        this.priceUnits = requirePositive(priceUnits, "priceUnits");
        this.originalQuantity = requirePositive(quantity, "quantity");
        this.remainingQuantity = quantity;
        this.arrivalSequence = requirePositive(arrivalSequence, "arrivalSequence");
        this.receivedAtEpochNanos =
                requireNonNegative(receivedAtEpochNanos, "receivedAtEpochNanos");
    }

    long orderId() {
        return orderId;
    }

    Side side() {
        return side;
    }

    long priceUnits() {
        return priceUnits;
    }

    long originalQuantity() {
        return originalQuantity;
    }

    long remainingQuantity() {
        return remainingQuantity;
    }

    long arrivalSequence() {
        return arrivalSequence;
    }

    long receivedAtEpochNanos() {
        return receivedAtEpochNanos;
    }

    boolean isFilled() {
        return remainingQuantity == 0;
    }

    void applyFill(long executedQuantity) {
        if (executedQuantity <= 0) {
            throw new IllegalArgumentException("executedQuantity must be positive");
        }
        if (executedQuantity > remainingQuantity) {
            throw new IllegalArgumentException(
                    "executedQuantity must not exceed remainingQuantity");
        }

        remainingQuantity -= executedQuantity;
    }

    private static long requirePositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }

    private static long requireNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return value;
    }
}
