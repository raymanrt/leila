leila
=====

*leila* is a tool to query and browse a [lucene](https://lucene.apache.org/) index written in Java.

Its functions are mostly inspired by [luke](https://code.google.com/archive/p/luke/) but *leila* has no GUI, and it
will never have it. There are some interesting use cases where a GUI is not required nor desired, for example a big
index on a remote server which should be inspected with minimum tranfer of data with the client.

Since this tool is inspired by luke it is called [Leila](https://it.wikipedia.org/wiki/Principessa_Leila), which is
the italian name of Luke Skywalker's sister, in the Star Wars mythology.

Leila is a read-only tool, so use it without any fear of changing your index. Anyway if you have importand data stored
on the index, please use this software at your own risk.

Leila is based on lucene 4.10.4. Future versions could be available also with more recent dependencies, depending on the
user needs.

This software is open-source, released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0),
and made without any commercial intent. Please feel free to contribute, especially if you find *leila* usefull and
discover some bug or missing feature.
The non-exhaustive feature request list is at the bottom of this page.

Build from source
-----------------

*leila* source can be downloaded from this repository and can be build with maven. The requirements are a JVM compatible
with Java 1.8 version.

```
git clone https://github.com/raymanrt/leila.git
cd leila
mvn clean package
java -jar target/leila.jar target/demo-index
```

A portable fat jar is build which includes every dependency required to execute leila inside you JVM.

The build process will also create a small index which will be used as reference in this documentation.

Index overview
--------------

The index overview can be activated with the `-o` option.

The index overview has the following format:
```
:: 2 fields:
content (100 terms)
id (103 terms)
:: number of documents: 100
:: number of deleted documents: 0
:: index version: 3
:: directory implementation: org.apache.lucene.index.StandardDirectoryReader
:: commit: segments_1 ( generation: 1 segments: 1 )

```

The overview expose the following data:
* number of fields
* name of fields, and for each one the number of terms within all documents (also for deleted ones)
* number of deleted documents
* index [version](https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/index/DirectoryReader.html#getVersion())
* the Directory implementation used to read the index
* some infos related to the commit point opened for the index

Top terms list
--------------

With `-t` option it's possible to list the term vector for one or more field.

The parameters of this options should be provided in order and are:
* a mandatory comma separated list of fields
* an optional number which is the maximum number of terms returned

The terms are always returned sorted by the most frequent. Each row contains the term and its frequency.


Search documents
----------------

With `-q` option it's possible to search and list the documents within the index.
The default query is `*:*`, which will match every document.

To parse the query *leila* uses the lucene QueryParser, which provides a standard
[syntax](https://lucene.apache.org/core/4_0_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#package_description).
The default query analyzer is the [WhiteSpaceAnalyzer](https://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/core/WhitespaceAnalyzer.html).
It's not possible using custom query parser or analyzers, but it could be one of the next extension of this tool.

By default a maximum of 20 documents is returned, to change this limit the `-l` option could be used passing a number as
parameter.

By default documents are sorted by insertion order, to use a custom field the `-s` option could be used passing its name
as parameter.

To select only a subset of fields the `-f` option could be used, to ignore them the option is `-i`. Both of those options
require a comma separated list of field names. The `-f` and `-i` options are mutually exclusive.

Documents are formatted with the default toString method of [Document](http://lucene.apache.org/core/4_1_0/core/org/apache/lucene/document/Document.html#toString()) object.

Datatype declaration
--------------------

Lucene doesn't store in the index the datatype for its fields. The `-d` option can be used to give leila the information
of the datatype for each field. Those informations are required if the input query contains clauses for a field which is
not a string.

A comma separated list of fields, with their own datatype can be provided. Supported datatypes are:
* `integer` (shorcut `int`)
* `float`
* `long`
* `double`

For example, to declare and query the id field as integer:
```
java -jar target/leila.jar target/demo-index -d id:int -q 'id:[90 TO *]' -l 2
:: datatype infos: {id=int}
:: query: id:[90 TO *]
:: sortBy:
:: limit: 2
:: listing documents
:: total documents found: 10
:: query time: 34ms
Document<stored<id:90> stored,indexed,tokenized<content:random 73573e5d-5b68-43fd-9978-15654f2711b9> stored<double:4.9> stored<float:0.9>>
Document<stored<id:91> stored,indexed,tokenized<content:11303088-db62-4ff4-94bf-5c14ee7e9fc7> stored<double:4.91> stored<float:0.91>>
```

Sorting results
---------------

As said in the previous paragraph, the `-s` option can be used to specify the sorting field.

More sorting preferences could be specified with this option, which sort field to use
(default, [sortednumeric](https://lucene.apache.org/core/7_2_0/core/org/apache/lucene/search/SortedNumericSortField.html),
[sortedset](https://lucene.apache.org/core/7_2_0/core/org/apache/lucene/search/SortedSetSortField.html)),
which are the datatype for comparing fields (INT, FLOAT, DOUBLE, LONG)
or the direction of sort, which can be: ascending or descending (reverse).

For example to sort by a `long` field, with `SortedNumericSortField` in reverse order use:
```
java -jar target/leila.jar target/demo-index -s long:sortednumeric:LONG:DESC -l 2
:: query: *:*
:: sortBy: <sortednumeric: "long">! selector=MIN type=LONG
:: limit: 2
:: listing documents
:: total documents found: 100
:: query time: 12ms
Document<stored<id:99> stored,indexed,tokenized,termVector,indexOptions=DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS<id_str:99> stored,indexed,tokenized<content:1141e438-dab3-40f3-9993-495f44013d5f> stored<double:4.99> stored<float:0.99> stored<long:199> stored,indexed,tokenized,termVector,indexOptions=DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS<txt:some text for 99> stored,indexed,tokenized,termVector,indexOptions=DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS<allstored:some stored text for 99>>
Document<stored<id:98> stored,indexed,tokenized,termVector,indexOptions=DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS<id_str:98> stored,indexed,tokenized<content:random 24ed7bf1-7a5e-48f6-a1b1-a8014f84e344> stored<double:4.98> stored<float:0.98> stored<long:198> stored,indexed,tokenized,termVector,indexOptions=DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS<txt:some text for 98> stored,indexed,tokenized,termVector,indexOptions=DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS<allstored:some stored text for 98>>
```

Default sort field is [SortField](https://lucene.apache.org/core/7_2_0/core/org/apache/lucene/search/SortField.html),
so note that depending on how you indexed your field, this value won't always work and may give some error.

Default sorting type is String, so be ware that without specifying a type, documents will be sorted in lexicographically
 order:
```
➜  leila git:(master) ✗ java -jar target/leila.jar target/demo-index -s float:REVERSE -f float
:: 1 fields to select: float
:: query: *:*
:: sortBy: <string: "float">!
:: limit: 20
:: listing documents
:: total documents found: 100
:: query time: 27ms
Document<stored<float:0.0>>
Document<stored<float:0.99>>
[...]
Document<stored<float:0.91>>
Document<stored<float:0.9>>
Document<stored<float:0.89>>
```

<!-- TODO: top terms can be formatted as int? -->

Term vector
-----------

In lucene a field could also be associated with a term vector, which is a sequence of tokens.

With the `-v` option, and giving a comma separated list of field names, a line for each field will be printed to the
output, containing an ordered list of tokens with the relative span of text covered in the original text. The format is
as follows:
```
java -jar target/leila.jar target/demo-index -v content -l 1
:: 1 token streams to select: content
:: query: *:*
:: sortBy:
:: limit: 1
:: listing documents
:: total documents found: 100
Document<stored<id:0> stored,indexed,tokenized,omitNorms,indexOptions=DOCS_ONLY<content:random 61a7a3e5-8dc3-4e22-91e6-2fffe2529815>>
token stream [content]: [random <0-6>, 61a7a3e5-8dc3-4e22-91e6-2fffe2529815 <7-43>]
```
This option works executing a query as described in the previous section.

Output plugins
--------------

Sometimes it is useful having some custom output format to present the query results.

It would be impossible to cope with all the requirements and for this purpose *leila* implements a plugin based engine.

To create a output plugin it is sufficient define a Java class with one public method whith any name, receiving one
parameter of type `org.apache.lucene.document.Document`. The implementation of this method should always return a String.

Usually the String represents the document in some other custom format and will be printed by *leila* to standard
output.

If the String is empty the document won't be printed. This is useful if the custom plugin doesn't need to print to
standard output (e.g. it should write to some file or some other kind of output).

The Java class implementing a plugin could also have one builder with an argument which could be:
* a String
* a String array

This is the only way supported for passing arguments to a plugin (i.e. an array of strings).

If the Java class implements the `java.io.Closeable` interface, the `close` method will always be invoked at the
execution end. This can be useful in cases where the plugin should release some resources acquired during
the execution.

The use of a plugin is fired passing the full name of the Java class which contains the output logic,
with the `p` option:
```
java -cp 'target/leila.jar:../leila-json/target/leila-json.jar' com.github.raymanrt.leila.Main \
    target/demo-index \
    -p com.github.raymanrt.leila.json.JsonFormatter pprint
```

Please go to [leila-json](https://github.com/raymanrt/leila-json) for more documentation and for a reference
implementation.

Version History
---------------

Version 0.5.0
* many unit test which bring also minor fixes
* master branch uses lucene 7.2.0 (which can read also 6 and 5 lucene indexes)
* lucene 4 compatibility is mantained in another branch

Version 0.4.0
* added numeric sorting and reversed sorting

Version 0.3.0
* added querying for more field types: double, float, long

Version 0.2.0
* support for output plugins (e.g. document formatting)

Version 0.1.0
* support for querying correctly also other field types (i.e. numeric): int, integer

Version 0.0.1:
* index overview
* terms list
* search documents
* token stream
* documentation

Next releases
-------------

This project is developed in my spare time, there are no scheduled releases. I'll try to fix bugs and resolve opened
issues. Pull requests and suggestions are wellcome. That said there is a list of feature desired for the next releases:
* more unit test coverage
* support for custom query parser
* support for custom query analyzer
* aggregate functions (sum, max, min, avg, ...)
* multi-field sort
* support for other lucene versions