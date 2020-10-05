
the iterators use volcano iterator model to get records of database.
the iterators iterates the records in the table

all iterators implements DbIterator.

the TupleIterator filters tuples based on predicate, predicate parsing is handled by antlr check [Expression.g4](../../../antlr4/Db/query/Expression.g4)
 for grammer

resources related to iterators:

https://www.youtube.com/watch?v=L5NhM7kw6Eg
