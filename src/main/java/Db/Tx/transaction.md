###### transaction manger consists of 

1. concurrency control protocol -strict 2pl (different from  2pc - 2 phase commit) with deadlock detection
2. ARIES recovery manger handles durability and atomicity of transactions, recovery of database from failed state


###### resources that helped me:

[aries simulator](https://mwhittaker.github.io/aries/)

[aries paper](https://web.stanford.edu/class/cs345d-01/rl/aries.pdf)

[deadlock detection, prevention](http://tutorials.jenkov.com/java-concurrency/deadlock-prevention.html)

[2pl wikipedia](https://en.wikipedia.org/wiki/Two-phase_locking)

[alternate to 2pl, optimistic concurrency control protocol called MVCC](https://www.youtube.com/watch?v=ZxhBkBNxvR0)

[CS-186 Transactions and Concurrency II Lecture 19](https://www.youtube.com/playlist?list=PLzzVuDSjP25T_5nRkp-QDjoqGTGIH_XOq)

[concurrency control protocols, isolation levels,.. general concepts related to transactions](https://www.youtube.com/watch?v=onYjxRcToto)

###### todo:

1. the 2pl protocol locks the pages for all transactions. Need to introduce granular locks for rows
2. implement undo function in Aries
2. in logrecord get XOR of prev, next. use the result to get prev, next
