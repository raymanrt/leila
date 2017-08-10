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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;

import java.util.HashMap;
import java.util.Map;

public class LeilaQueryParser extends QueryParser {

    private static final Map<String, QueryLogic> datatypeToQueryParser = new HashMap<>();
    static {
        datatypeToQueryParser.put("int", QueryLogic.INTEGER);
        datatypeToQueryParser.put("integer", QueryLogic.INTEGER);
    }

    private final Map<String, String> fieldNameToDatatype;

    public LeilaQueryParser(final String f, final Analyzer a, final Map<String, String> fieldNameToDatatype) {
        super(f, a);
        this.fieldNameToDatatype = fieldNameToDatatype;
    }

    protected Query newRangeQuery(String field, String part1, String part2, boolean startInclusive,
                                  boolean endInclusive) {

        QueryLogic queryLogic = datatypeToQueryParser.getOrDefault(fieldNameToDatatype.getOrDefault(field,"default"), QueryLogic.NULL);
        if(queryLogic.equals(QueryLogic.NULL)) {
            return (TermRangeQuery) super.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
        }

        return queryLogic.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }


    protected Query newTermQuery(Term term) {
        QueryLogic queryLogic = datatypeToQueryParser.getOrDefault(fieldNameToDatatype.getOrDefault(term.field(),"default"), QueryLogic.NULL);
        if(queryLogic.equals(QueryLogic.NULL)) {
            return super.newTermQuery(term);
        }
        return queryLogic.newTermQuery(term);

    }
}