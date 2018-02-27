/*
 * Copyright 2017 Riccardo Tasso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.raymanrt.leila.queryparser;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

public interface QueryLogic {
    public Query newRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive);

    public Query newTermQuery(Term term);

    public static QueryLogic NULL = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            return null;
        }

        @Override
        public Query newTermQuery(final Term term) {
            return null;
        }
    };

    public static QueryLogic INTEGER = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            Integer num1 = Integer.MIN_VALUE;
            try {
                num1 = Integer.parseInt(part1);
                num1 = startInclusive ? num1: Math.incrementExact(num1);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            Integer num2 = Integer.MAX_VALUE;
            try {
                num2 = Integer.parseInt(part2);
                num2 = endInclusive ? num2 : Math.decrementExact(num2);
            } catch(NumberFormatException|NullPointerException ex) {

            }

            return IntPoint.newRangeQuery(field, num1, num2);
        }

        @Override
        public Query newTermQuery(final Term term) {
            return IntPoint.newExactQuery(term.field(), Integer.parseInt(term.text()));
        }
    };

    public static QueryLogic FLOAT = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            Float num1 = Float.MIN_VALUE;
            try {
                num1 = Float.parseFloat(part1);
                num1 = startInclusive ? num1 : FloatPoint.nextUp(num1);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            Float num2 = Float.MAX_VALUE;
            try {
                num2 = Float.parseFloat(part2);
                num2 = endInclusive ? num2 : FloatPoint.nextDown(num2);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            return FloatPoint.newRangeQuery(field, num1, num2);
        }

        @Override
        public Query newTermQuery(final Term term) {
            return FloatPoint.newExactQuery(term.field(), Float.parseFloat(term.text()));
        }
    };

    public static QueryLogic LONG = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            Long num1 = Long.MIN_VALUE;
            try {
                num1 = Long.parseLong(part1);
                num1 = startInclusive ? num1 : Math.incrementExact(num1);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            Long num2 = Long.MAX_VALUE;
            try {
                num2 = Long.parseLong(part2);
                num2 = endInclusive ? num2 : Math.decrementExact(num2);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            return LongPoint.newRangeQuery(field, num1, num2);
        }

        @Override
        public Query newTermQuery(final Term term) {
            return LongPoint.newExactQuery(term.field(), Long.parseLong(term.text()));
        }
    };

    public static QueryLogic DOUBLE = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            Double num1 = Double.MIN_VALUE;
            try {
                num1 = Double.parseDouble(part1);
                num1 = startInclusive ? num1 : DoublePoint.nextUp(num1);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            Double num2 = Double.MAX_VALUE;
            try {
                num2 = Double.parseDouble(part2);
                num2 = endInclusive ? num2 : DoublePoint.nextDown(num2);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            return DoublePoint.newRangeQuery(field, num1, num2);
        }

        @Override
        public Query newTermQuery(final Term term) {
            return DoublePoint.newExactQuery(term.field(), Double.parseDouble(term.text()));
        }
    };
}
