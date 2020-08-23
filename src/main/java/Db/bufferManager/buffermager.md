bufferpool manager

bufferpool is the array of pages in memory. Buffer pool manager manages the buffer pool. Gets the pages from the disk manager to buffer pool and evicts page.


page eviction algorithm determines which page to be evicted. Implemented LRU page eviction algorithm
the page eviction algorithm has to extend replacer interface.


pages that the query uses have to be pinned and pinned pages cant be evicted

last committed lsn in recovery manger has to be greater than page lsn of victim

transaction manager provides lock based on permission level before accessing the page


resources:

[CMU database group buffer pool manager lecture](https://www.youtube.com/watch?v=uZ3-aeFYE5k)

