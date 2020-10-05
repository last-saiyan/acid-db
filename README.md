# acid-db
simple acid database made using java. Uses ARIES for atomicity and durablity of transaction and strict 2pl for isolation of transactions

this is a toy database built to understand the techniques used in transactional database

###### operations:

1. transaction commit, abort.
2. insert
3. projection(select) supports predicate
4. delete
5. update
6. recovery

abort transactions on exceptions, (todo: create custom exceptions)

check [query.md](./src/main/java/Db/Query/query.md) for example queries
append query with `;` 


[bufferpool manager](./src/main/java/Db/bufferManager/buffermager.md)

[transaction manager](./src/main/java/Db/Tx/transaction.md)

[iterators](./src/main/java/Db/iterator/iterator.md)

[query.md](./src/main/java/Db/Query/query.md)


