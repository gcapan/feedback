This is intended to create a feedback db, a HBase backed user history storage to efficiently write [user, item] feedback, and query:
- users' feedback on items, 
- users' and items' candidate neighbors
- most popular users
- most popular items
- all items rated by a user
- all users who rated an item

and such, as described in Mahout's DataModel interface for recommenders.
