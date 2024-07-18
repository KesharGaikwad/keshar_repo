package com.ex;

import java.sql.*;
import java.util.Scanner;

import java.sql.ResultSet;

public class library {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection connection = null;
        try {
            // Step 1: Loading the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 2: Establishing a database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "root");

            int operation;
            do {
                // Display menu for CRUD operations
                System.out.println("\nLibrary Management System");
                System.out.println("1. Add Book");
                System.out.println("2. Add Member");
                System.out.println("3. Borrow Book");
                System.out.println("4. View All Books");
                System.out.println("5. View All Members");
                System.out.println("6. View Borrow Records");
                System.out.println("7. Exit");
                System.out.print("Enter operation number: ");
                operation = sc.nextInt();
                sc.nextLine(); // Consume newline

                switch (operation) {
                    case 1:
                        // Add Book operation
                        addBook(connection, sc);
                        break;

                    case 2:
                        // Add Member operation
                        addMember(connection, sc);
                        break;

                    case 3:
                        // Borrow Book operation
                        borrowBook(connection, sc);
                        break;

                    case 4:
                        // View All Books operation
                        viewAllBooks(connection);
                        break;

                    case 5:
                        // View All Members operation
                        viewAllMembers(connection);
                        break;

                    case 6:
                        // View Borrow Records operation
                        viewBorrowRecords(connection);
                        break;

                    case 7:
                        System.out.println("Exiting program.");
                        break;

                    default:
                        System.out.println("Invalid operation. Please enter a valid operation number.");
                        break;
                }
            } while (operation != 7);

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error:");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                sc.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection:");
                e.printStackTrace();
            }
        }
    }

    // Method to add a book
    private static void addBook(Connection connection, Scanner sc) throws SQLException {
        System.out.print("Enter book title: ");
        String title = sc.nextLine();
        System.out.print("Enter author: ");
        String author = sc.nextLine();

        String insertSql = "INSERT INTO books (title, author) VALUES (?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        insertStatement.setString(1, title);
        insertStatement.setString(2, author);

        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Book added successfully.");
        } else {
            System.out.println("Failed to add book.");
        }
    }

    // Method to add a member
    private static void addMember(Connection connection, Scanner sc) throws SQLException {
        System.out.print("Enter member name: ");
        String name = sc.nextLine();
        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        String insertSql = "INSERT INTO members (name, phone_number) VALUES (?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        insertStatement.setString(1, name);
        insertStatement.setString(2, phone);

        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Member added successfully.");
        } else {
            System.out.println("Failed to add member.");
        }
    }

    // Method to borrow a book
    private static void borrowBook(Connection connection, Scanner sc) throws SQLException {
        viewAllBooks(connection); // Display all books for user reference
        System.out.print("Enter book ID to borrow: ");
        int bookId = sc.nextInt();
        sc.nextLine(); // Consume newline

        viewAllMembers(connection); // Display all members for user reference
        System.out.print("Enter member ID to borrow book: ");
        int memberId = sc.nextInt();
        sc.nextLine(); // Consume newline

        System.out.print("Enter borrow date (YYYY-MM-DD): ");
        String borrowDate = sc.nextLine();

        String insertSql = "INSERT INTO borrows (book_id, member_id, borrow_date) VALUES (?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        insertStatement.setInt(1, bookId);
        insertStatement.setInt(2, memberId);
        insertStatement.setString(3, borrowDate);

        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Failed to borrow book.");
        }
    }

    // Method to view all books
    private static void viewAllBooks(Connection connection) throws SQLException {
        String retrieveSql = "SELECT * FROM books";
        try (Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(retrieveSql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                System.out.println("ID: " + id + ", Title: " + title + ", Author: " + author);
            }
        }
    }

    // Method to view all members
    private static void viewAllMembers(Connection connection) throws SQLException {
        String retrieveSql = "SELECT * FROM members";
        try (Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(retrieveSql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("member_id");
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone_number");
                System.out.println("ID: " + id + ", Name: " + name + ", Phone Number: " + phone);
            }
        }
    }

    // Method to view all borrow records
    private static void viewBorrowRecords(Connection connection) throws SQLException {
        String retrieveSql = "SELECT b.borrow_id, bk.title AS book_title, m.name AS member_name, b.borrow_date " +
                             "FROM borrows b " +
                             "INNER JOIN books bk ON b.book_id = bk.book_id " +
                             "INNER JOIN members m ON b.member_id = m.member_id";
        try (Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(retrieveSql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("borrow_id");
                String bookTitle = resultSet.getString("book_title");
                String memberName = resultSet.getString("member_name");
                String borrowDate = resultSet.getString("borrow_date");
                System.out.println("ID: " + id + ", Book: " + bookTitle + ", Member: " + memberName + ", Date: " + borrowDate);
            }
        }
    }
}

