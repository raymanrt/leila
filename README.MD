leila
=====

*leila* is a tool to query and browse a [lucene](https://lucene.apache.org/) index written in Java.

Its functions are mostly inspired by [luke](https://code.google.com/archive/p/luke/) but *leila* has no GUI, and it
will never have it. There are some interesting use cases where a GUI is not required nor desired, for example a big
index on a remote server which should be inspected with minimum the tranfer of data with the client.

Since this tool is inspired by luke it is called [Leila](https://it.wikipedia.org/wiki/Principessa_Leila), which is
the italian name of Luke Skywalker's sister, in the Star Wars mythology.

Leila is a read-only tool, so use it without any fear of changing your index. Anyway if you have importand data stored
on the index, please use this software at your own risk.

Leila is based on lucene 4.10.4. Future versions could be available also with more recent dependencies, depending on the
user needs.

build from source
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

index overview
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