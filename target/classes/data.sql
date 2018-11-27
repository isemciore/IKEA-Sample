insert into customer (id, name, hidden) values (1000, 'Erik', false)
insert into shopping_list(id, name, customer_id) values (2000, 'Kitchen', 1000)
insert into item values (3000, '123-543-22', 'Fork')
insert into item values (3001, '123-543-99', 'Spoon')


insert into shopping_list_item (shopping_list_id, item_id) values (2000, 3000)