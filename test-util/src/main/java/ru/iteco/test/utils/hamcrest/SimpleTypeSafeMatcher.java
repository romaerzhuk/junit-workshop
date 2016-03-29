package ru.iteco.test.utils.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Extends org.hamcrest.TypeSafeMatcher<T>
 * and stubs TypeSafeMatcher.describeTo(Description) method
 * to simplify matcher reading without implemented describeTo method
 * <p/>
 * Created by shchipalkin on 04.08.2015.
 */
public abstract class SimpleTypeSafeMatcher<T> extends TypeSafeMatcher<T> {

    @Override
    public void describeTo(Description description) {
        // stub
    }
}
