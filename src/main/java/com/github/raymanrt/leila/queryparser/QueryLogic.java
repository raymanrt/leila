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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;

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
            Integer num1 = null;
            try {
                num1 = Integer.parseInt(part1);
            } catch(NumberFormatException ex) {

            }
            Integer num2 = null;
            try {
                num2 = Integer.parseInt(part2);
            } catch(NumberFormatException ex) {

            }
            return NumericRangeQuery.newIntRange(field, num1, num2, startInclusive, endInclusive);
        }

        @Override
        public Query newTermQuery(final Term term) {
            BytesRefBuilder byteRefBuilder = new BytesRefBuilder();
            NumericUtils.intToPrefixCoded(Integer.parseInt(term.text()), 0, byteRefBuilder);
            return new TermQuery(new Term(term.field(), byteRefBuilder.get()));

        }
    };

    public static QueryLogic FLOAT = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            Float num1 = null;
            try {
                num1 = Float.parseFloat(part1);
            } catch(NumberFormatException ex) {

            }
            Float num2 = null;
            try {
                num2 = Float.parseFloat(part2);
            } catch(NumberFormatException ex) {

            }
            return NumericRangeQuery.newFloatRange(field, num1, num2, startInclusive, endInclusive);
        }

        @Override
        public Query newTermQuery(final Term term) {
            BytesRefBuilder byteRefBuilder = new BytesRefBuilder();
            NumericUtils.intToPrefixCoded(NumericUtils.floatToSortableInt(Float.parseFloat(term.text())), 0, byteRefBuilder);
            return new TermQuery(new Term(term.field(), byteRefBuilder.get()));
        }
    };

    public static QueryLogic LONG = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            Long num1 = null;
            try {
                num1 = Long.parseLong(part1);
            } catch(NumberFormatException ex) {

            }
            Long num2 = null;
            try {
                num2 = Long.parseLong(part2);
            } catch(NumberFormatException ex) {

            }
            return NumericRangeQuery.newLongRange(field, num1, num2, startInclusive, endInclusive);
        }

        @Override
        public Query newTermQuery(final Term term) {
            BytesRefBuilder byteRefBuilder = new BytesRefBuilder();
            NumericUtils.longToPrefixCoded(Long.parseLong(term.text()), 0, byteRefBuilder);
            return new TermQuery(new Term(term.field(), byteRefBuilder.get()));
        }
    };

    public static QueryLogic DOUBLE = new QueryLogic() {
        @Override
        public Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
            Double num1 = null;
            try {
                num1 = Double.parseDouble(part1);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            Double num2 = null;
            try {
                num2 = Double.parseDouble(part2);
            } catch(NumberFormatException|NullPointerException ex) {

            }
            return NumericRangeQuery.newDoubleRange(field, num1, num2, startInclusive, endInclusive);
        }

        @Override
        public Query newTermQuery(final Term term) {
            BytesRefBuilder byteRefBuilder = new BytesRefBuilder();
            NumericUtils.longToPrefixCoded(NumericUtils.doubleToSortableLong(Double.parseDouble(term.text())), 0, byteRefBuilder);
            return new TermQuery(new Term(term.field(), byteRefBuilder.get()));
        }
    };
}
