/*
 * Moresby Coffee Bean
 *
 * Copyright (c) 2012, Barnabas Sudy (barnabas.sudy@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.moresby.jettyspring.util.converter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This utility class contains a couple of predefined {@link Converter}s and
 * a couple of method to help the conversion.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public final class Converters {


    /**
     * Trims a string using {@link String#trim()} method.
     */
    public static final Converter<String, String> TRIM = new Converter<String, String>() {

        @Override
        public String convert(final String from) throws ConverterException {
            if (from == null) {
                return null;
            }
            return from.trim();
        }

    };

    /**
     * Converts an array of object by the converter.
     *
     * @param <F> The type of the object which will be converted.
     * @param <T> The type of the object which will be converted to.
     * @param from The array of object to convert.
     * @param converter The converter which converts all the array elements
     * @return The new array.
     */
    public static <F, T> T[] convertArray(final F[] from, final Converter<F, T> converter, final Class<T> clazz) {
        if (from == null) {
            return null;
        }
        final List<T> result = new ArrayList<T>(from.length);
        for (int i = 0; i < from.length; i++) {
            result.add(converter.convert(from[i]));
        }

        Array.newInstance(clazz, from.length);
        @SuppressWarnings("unchecked")
        final T[] resultArray = result.toArray((T[]) Array.newInstance(clazz, from.length) );
        return resultArray;

    }


    /**
     * Converts a list of objects. The result will be in an ArrayList.
     *
     * TODO create ConvertList object and use that!
     *
     * @param <F> The type of the object which will be converted.
     * @param <T> The type of the object which will be converted to.
     * @param from The list of the convertable object.
     * @param converter The converter which converts the elements of the list.
     * @return The ArrayList of the converted objects.
     */
    public static <F, T> ArrayList<T> convertList(final Collection<F> from, final Converter<F, T> converter) {
        if (from == null) {
            return null;
        }

        final ArrayList<T> results = new ArrayList<T>(from.size());
        for (final F fromEntity : from) {
            results.add(converter.convert(fromEntity));
        }

        return results;

    }

    /** Hidden constructor of utility class. */
    private Converters() { /* NOP */ }

}
