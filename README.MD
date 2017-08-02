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

By default documents are sorted by insertion order, to use a custom field the `-o` option could be used passing its name
as parameter.

To select only a subset of fields the `-f` option could be used, to ignore them the option is `-i`. Both of those options
require a comma separated list of field names. The `-f` and `-i` options are mutually exclusive.