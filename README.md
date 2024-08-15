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

## Setup Instructions

### Prerequisites

1. **Java Development Kit (JDK) 21:** Ensure you have JDK 21 installed.
2. **Maven:** Maven is used for managing dependencies and building the application.
3. **PostgreSQL:** The application uses PostgreSQL as the database.
4. **Git:** Git is required to clone the repository.
5. **IDE:** An IDE like IntelliJ IDEA, Eclipse, or VS Code is recommended.

### Setup Instructions

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/vrentati/library.git
   cd library
   ```

2. **Set Up Environment Variables:**

   Create a `.env` file or update `application.properties` to include the necessary environment variables:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/library
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   ```

3. **Build the Application:**

   Navigate to the project directory and run:

   ```bash
   mvn clean install
   ```

4. **Run the Application:**

   After building, you can run the application using:

   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`.

6. **Access Swagger Documentation:**

   The API documentation is available via Swagger at `http://localhost:8080/swagger-ui.html`.

7. **Running Tests:**

   To run the unit tests, use:

   ```bash
   mvn test
   ```

## API Endpoints Overview

### Books Management:

- `POST /books` - Create or update a book.
- `GET /books/{id}` - Get book by ID.
- `PUT /books/{id}` - Update a book.
- `DELETE /books/{id}` - Delete a book.

### Members Management:

- `POST /members` - Create a member.
- `GET /members/{id}` - Get member by ID.
- `PUT /members/{id}` - Update a member.
- `DELETE /members/{id}` - Delete a member.

### Borrowing and Returning Books:

- `POST /members/{memberId}/borrow/{bookId}` - Borrow a book.
- `POST /members/{memberId}/return/{bookId}` - Return a book.

### Additional Endpoints:

- `GET /members/books/borrowed?memberName={name}` - Get books borrowed by member name.
- `GET /members/books/borrowed/distinct` - Get distinct borrowed book names.
- `GET /members/books/borrowed/count` - Get borrowed book names with count.

## Testing

The project includes unit tests that can be run using Maven:

```bash
mvn test
```

## Authors

- **Kateryna Vynokurova** - [VrenTati](https://github.com/vrentati)
