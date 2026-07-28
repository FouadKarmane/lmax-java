package com.karmane.lmax.engine;

import com.karmane.lmax.api.Side;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    private static final long ORDER_ID = 101;
    private static final long PRICE_UNITS = 10_00;
    private static final long QUANTITY = 100;
    private static final long ARRIVAL_SEQUENCE = 1;
    private static final long RECEIVED_AT_EPOCH_NANOS = 1_785_193_200_000_000_000L;

    @Test
    void createsAcceptedOrderWithOriginalState() {
        Order order = newOrder();

        assertEquals(ORDER_ID, order.orderId());
        assertEquals(Side.BUY, order.side());
        assertEquals(PRICE_UNITS, order.priceUnits());
        assertEquals(QUANTITY, order.originalQuantity());
        assertEquals(QUANTITY, order.remainingQuantity());
        assertEquals(ARRIVAL_SEQUENCE, order.arrivalSequence());
        assertEquals(RECEIVED_AT_EPOCH_NANOS, order.receivedAtEpochNanos());
        assertFalse(order.isFilled());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveOrderId(long orderId) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(
                        orderId,
                        Side.BUY,
                        PRICE_UNITS,
                        QUANTITY,
                        ARRIVAL_SEQUENCE,
                        RECEIVED_AT_EPOCH_NANOS));
    }

    @Test
    void rejectsNullSide() {
        assertThrows(
                NullPointerException.class,
                () -> new Order(
                        ORDER_ID,
                        null,
                        PRICE_UNITS,
                        QUANTITY,
                        ARRIVAL_SEQUENCE,
                        RECEIVED_AT_EPOCH_NANOS));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositivePrice(long priceUnits) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(
                        ORDER_ID,
                        Side.BUY,
                        priceUnits,
                        QUANTITY,
                        ARRIVAL_SEQUENCE,
                        RECEIVED_AT_EPOCH_NANOS));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveQuantity(long quantity) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(
                        ORDER_ID,
                        Side.BUY,
                        PRICE_UNITS,
                        quantity,
                        ARRIVAL_SEQUENCE,
                        RECEIVED_AT_EPOCH_NANOS));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveArrivalSequence(long arrivalSequence) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(
                        ORDER_ID,
                        Side.BUY,
                        PRICE_UNITS,
                        QUANTITY,
                        arrivalSequence,
                        RECEIVED_AT_EPOCH_NANOS));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, Long.MIN_VALUE})
    void rejectsNegativeReceivedTimestamp(long receivedAtEpochNanos) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(
                        ORDER_ID,
                        Side.BUY,
                        PRICE_UNITS,
                        QUANTITY,
                        ARRIVAL_SEQUENCE,
                        receivedAtEpochNanos));
    }

    @Test
    void appliesPartialFillWithoutChangingOriginalState() {
        Order order = newOrder();

        order.applyFill(40);

        assertEquals(60, order.remainingQuantity());
        assertEquals(QUANTITY, order.originalQuantity());
        assertEquals(ORDER_ID, order.orderId());
        assertEquals(Side.BUY, order.side());
        assertEquals(PRICE_UNITS, order.priceUnits());
        assertEquals(ARRIVAL_SEQUENCE, order.arrivalSequence());
        assertFalse(order.isFilled());
    }

    @Test
    void exactFillCompletesOrder() {
        Order order = newOrder();

        order.applyFill(QUANTITY);

        assertEquals(0, order.remainingQuantity());
        assertTrue(order.isFilled());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveFillQuantity(long executedQuantity) {
        Order order = newOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> order.applyFill(executedQuantity));
        assertEquals(QUANTITY, order.remainingQuantity());
    }

    @Test
    void rejectsOverfillWithoutChangingRemainingQuantity() {
        Order order = newOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> order.applyFill(QUANTITY + 1));
        assertEquals(QUANTITY, order.remainingQuantity());
    }

    @Test
    void rejectsAdditionalFillAfterCompletion() {
        Order order = newOrder();
        order.applyFill(QUANTITY);

        assertThrows(IllegalArgumentException.class, () -> order.applyFill(1));
        assertEquals(0, order.remainingQuantity());
    }

    private static Order newOrder() {
        return new Order(
                ORDER_ID,
                Side.BUY,
                PRICE_UNITS,
                QUANTITY,
                ARRIVAL_SEQUENCE,
                RECEIVED_AT_EPOCH_NANOS);
    }
}
