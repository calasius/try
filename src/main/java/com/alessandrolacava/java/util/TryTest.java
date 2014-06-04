package com.alessandrolacava.java.util;

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

public class TryTest {

    @Test
    public void testIsSuccess() {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        assertTrue("result must be a success", result.isSuccess());
    }

    @Test
    public void testIsFailure() {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        assertTrue("result must be a failure", result.isFailure());
    }

    @Test
    public void testGetAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        int intResult = result.get();
        assertEquals("intResult must be 42", intResult, 42);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        int intResult = result.get();
        fail("intResult must fail with NumberFormatException");
    }

    @Test
    public void testForEachAgainstASuccess() {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        result.forEach(
                (Integer i) -> assertEquals("i must be 42", (int) i, 42)
        );
    }

    @Test(expected = NumberFormatException.class)
    public void testForEachAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        result.forEach(
                (Integer i) -> System.out.println("Since it's a failure it does not even get here. As a matter of fact this won't be printed")
        );

        // Conversely, calling get mush throw the NumberFormatException captured in result
        result.get();
    }

    @Test
    public void testMapAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        Try<String> stringResult = result.map(
                (Integer i) -> i.toString() + ", Hello World!"
        );
        assertEquals("stringResult must be '42, Hello World!'", stringResult.get(), "42, Hello World!");

    }

    @Test(expected = NumberFormatException.class)
    public void testMapAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        Try<String> stringResult = result.map(
                (Integer i) -> {
                    String out = i.toString() + ", Hello World!";
                    System.out.println("Since it's a failure it does not even get here. As a matter of fact this won't be printed");
                    return out;
                }
        );

        // Conversely, calling get mush throw the NumberFormatException captured in result
        result.get();
    }

    @Test
    public void testFlatMapAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );

        Try<String> chainedResult = result.flatMap(
                (Integer i) -> Try.apply(
                        () -> i + ", " + anotherSuccess()
                )
        );

        assertEquals("chainedResult must be '42, Hello World!'", chainedResult.get(), "42, Hello World!");
    }

    @Test(expected = NumberFormatException.class)
    public void testFlatMapAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );

        if (result instanceof Failure) System.out.println("YES");

        Try<String> chainedResult = result.flatMap(
                (Integer i) -> Try.apply(
                        () -> {
                            String out = i + ", " + anotherSuccess();
                            System.out.println("Since it's a failure it does not even get here. As a matter of fact this won't be printed");
                            return out;
                        }
                )
        );

        // Conversely, calling get mush throw the NumberFormatException captured in result
        chainedResult.get();
    }

    @Test
    public void testFilterAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );

        Try<Integer> filteredResult = result.filter((Integer i) -> i == 42);
        assertEquals("filteredResult must be 42", (int) filteredResult.get(), 42);
    }

    @Test(expected = NumberFormatException.class)
    public void testFilterAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );

        Try<Integer> filteredResult = result.filter(
                (Integer i) -> {
                    System.out.println("Since it's a failure it does not even get here. As a matter of fact this won't be printed");
                    return i == 42;
                }
        );

        // Conversely, calling get mush throw the NumberFormatException captured in result
        filteredResult.get();
    }

    @Test(expected = NoSuchElementException.class)
    public void testNonMatchingFilter() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );

        Try<Integer> filteredResult = result.filter(
                (Integer i) -> i != 42
        );

        // In this case calling get mush throw a NoSuchElementException since the Predicate in filter does not hold
        filteredResult.get();
    }

    @Test
    public void testRecoverAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        Try<Integer> recoveredResult = result.recover(
                (Exception e) -> {
                    if (e instanceof NumberFormatException) {
                        return new Integer(84);
                    } else {
                        return new Integer(0);
                    }
                }
        );
        assertEquals("recoveredResult must be 42", (int) recoveredResult.get(), 42);
    }

    @Test
    public void testRecoverAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        Try<Integer> recoveredResult = result.recover(
                (Exception e) -> {
                    if (e instanceof NumberFormatException) {
                        return new Integer(84);
                    } else {
                        return new Integer(0);
                    }
                }
        );
        assertEquals("recoveredResult must be 84", (int) recoveredResult.get(), 84);
    }

    @Test
    public void testRecoverWithAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        Try<Integer> recoveredResult = result.recoverWith(
                (Exception e) -> {
                    if (e instanceof NumberFormatException) {
                        return Try.apply(() -> new Integer(84));
                    } else {
                        return Try.apply(() -> new Integer(0));
                    }
                }
        );
        assertEquals("recoveredResult must be 42", (int) recoveredResult.get(), 42);
    }

    @Test
    public void testRecoverWithAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        Try<Integer> recoveredResult = result.recoverWith(
                (Exception e) -> {
                    if (e instanceof NumberFormatException) {
                        return Try.apply(() -> new Integer(84));
                    } else {
                        return Try.apply(() -> new Integer(0));
                    }
                }
        );
        assertEquals("recoveredResult must be 84", (int) recoveredResult.get(), 84);
    }

    @Test
    public void testFailedAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        Try<Exception> failedOnASuccessProducesFailure = result.failed();
        assertEquals("failedOnASuccessProducesFailure is a Failure", failedOnASuccessProducesFailure.isFailure(), true);
    }

    @Test
    public void testFailedAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        Try<Exception> failedOnAFailureProducesSuccess = result.failed();
        assertEquals("failedOnAFailureProducesSuccess is a Success", failedOnAFailureProducesSuccess.isSuccess(), true);
    }

    @Test
    public void testToOptionalAgainstASuccess() throws Exception {
        Try<Integer> result = Try.apply(
                () -> success()
        );
        assertEquals("successful result.toOptional() must be Optional.of(42)", result.toOptional(), Optional.of(42));
    }

    @Test
    public void testToOptionalAgainstAFailure() throws Exception {
        Try<Integer> result = Try.apply(
                () -> failure()
        );
        assertEquals("failed result.toOptional() must be Optional.empty()", result.toOptional(), Optional.empty());
    }

    @Test
    public void testGetOrElse() throws Exception {

    }

    @Test
    public void testOrElse() throws Exception {

    }

    @Test
    public void testTransform() throws Exception {

    }

    private int success() {
        return 42;
    }

    private String anotherSuccess() {
        return "Hello World!";
    }

    private int failure() throws NumberFormatException {
        throw new NumberFormatException("Number not valid");
    }
}