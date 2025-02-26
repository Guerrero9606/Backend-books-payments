-- INSERTs para la tabla "purchases" en ms-payments

INSERT INTO purchases (book_isbn, purchase_date, quantity, buyer, status)
VALUES ('9780307389732', '2025-02-07 10:30:00', 2, 'user1@example.com', 'PENDING');

INSERT INTO purchases (book_isbn, purchase_date, quantity, buyer, status)
VALUES ('9780156013987', '2025-02-06 15:45:00', 1, 'user2@example.com', 'COMPLETED');

INSERT INTO purchases (book_isbn, purchase_date, quantity, buyer, status)
VALUES ('9780451524935', '2025-02-05 12:00:00', 3, 'user3@example.com', 'CANCELLED');

INSERT INTO purchases (book_isbn, purchase_date, quantity, buyer, status)
VALUES ('9780143126396', '2025-02-04 18:20:00', 1, 'user4@example.com', 'PENDING');

INSERT INTO purchases (book_isbn, purchase_date, quantity, buyer, status)
VALUES ('9780061122415', '2025-02-03 09:10:00', 5, 'user5@example.com', 'COMPLETED');
