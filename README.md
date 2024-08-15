# Library Management System

## Description

This project is a library management system that allows users to add, update, and delete books. Users can borrow and return books and view a list of borrowed books.

## Features

- **Books:**
  - Add new books.
  - Update book details.
  - Delete books if they are not borrowed.
  - View the list of books.

- **Members:**
  - Add new library members.
  - Update member details.
  - Delete members if they do not have borrowed books.
  - Borrow and return books.

## Technologies

- Java 21
- Spring Boot
- PostgreSQL
- Swagger for API documentation

## Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/vrentati/library.git
   cd library
   ```

2. **Set up the database:**

   Ensure that PostgreSQL is installed and configured on your machine.

   Create the database:

   ```sql
   CREATE DATABASE library;
   ```

   Configure the database connection parameters in the `application.properties` or `application.yml` file.

3. **Run the application:**

   Use Maven to compile and run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   or:

   ```bash
   mvn spring-boot:run
   ```

4. **Access Swagger UI:**

   After starting the application, visit `http://localhost:8080/swagger-ui.html` to view the API documentation.

## Usage

### API Endpoints

- **Books**
  - `POST /books` - Add a new book.
  - `GET /books/{id}` - View information about a book by ID.
  - `PUT /books/{id}` - Update a book.
  - `DELETE /books/{id}` - Delete a book.

- **Members**
  - `POST /members` - Add a new member.
  - `GET /members/{id}` - View information about a member by ID.
  - `PUT /members/{id}` - Update a member.
  - `DELETE /members/{id}` - Delete a member.
  - `POST /members/{memberId}/borrow/{bookId}` - Borrow a book.
  - `POST /members/{memberId}/return/{bookId}` - Return a book.

## Testing

The project includes unit tests that can be run using Maven:

```bash
mvn test
```

## Authors

- **Kateryna Vynokurova** - [VrenTati](https://github.com/vrentati)
