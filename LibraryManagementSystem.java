

import java.io.; import java.util.;



public Book(String title, String author) {
    this.title = title;
    this.author = author;
    this.isAvailable = true;
}

public String getTitle() { return title; }
public String getAuthor() { return author; }
public boolean isAvailable() { return isAvailable; }
public void setAvailable(boolean available) { this.isAvailable = available; }

public void displayInfo() {
    System.out.printf("%-30s %-20s %-10s\n", title, author, (isAvailable ? "Available" : "Issued"));
}

}



public User(String username) {
    this.username = username;
    this.issuedBooks = new ArrayList<>();
}

public String getUsername() { return username; }
public ArrayList<Book> getIssuedBooks() { return issuedBooks; }

public boolean issueBook(Book book) {
    if (issuedBooks.size() < MAX_ISSUED_BOOKS && book.isAvailable()) {
        issuedBooks.add(book);
        book.setAvailable(false);
        return true;
    }
    return false;
}

public boolean returnBook(Book book) {
    if (issuedBooks.remove(book)) {
        book.setAvailable(true);
        return true;
    }
    return false;
}

public boolean hasBook(Book book) {
    return issuedBooks.contains(book);
}

public void displayIssuedBooks() {
    if (issuedBooks.isEmpty()) {
        System.out.println("No books issued.");
    } else {
        System.out.printf("%-30s %-20s %-10s\n", "Title", "Author", "Status");
        for (Book book : issuedBooks) {
            book.displayInfo();
        }
    }
}

}


public LibraryDAO() {
    books = load(BOOK_FILE);
    users = load(USER_FILE);
}

public void addBook(Book book) {
    books.add(book);
    save(BOOK_FILE, books);
}

public void addUser(User user) {
    users.add(user);
    save(USER_FILE, users);
}

public Book findBook(String title) {
    return books.stream().filter(b -> b.getTitle().equalsIgnoreCase(title)).findFirst().orElse(null);
}

public User findUser(String username) {
    return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
}

public ArrayList<Book> getBooks() { return books; }

public void updateData() {
    save(BOOK_FILE, books);
    save(USER_FILE, users);
}

private <T> void save(String file, ArrayList<T> list) {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
        out.writeObject(list);
    } catch (IOException e) {
        System.out.println("[Error] Saving to " + file + ": " + e.getMessage());
    }
}

@SuppressWarnings("unchecked")
private <T> ArrayList<T> load(String file) {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
        return (ArrayList<T>) in.readObject();
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

}



public static void main(String[] args) {
    initializeSampleBooks();
    System.out.println("=== Welcome to Library Management System ===");
    while (true) {
        System.out.print("\nEnter your username (or 'exit' to quit): ");
        String username = scanner.nextLine().trim();
        if (username.equalsIgnoreCase("exit")) {
            System.out.println("Thank you. Goodbye!");
            dao.updateData();
            break;
        }
        if (username.isEmpty()) {
            System.out.println("[Error] Username cannot be empty.");
            continue;
        }
        User user = dao.findUser(username);
        if (user == null) {
            user = new User(username);
            dao.addUser(user);
            System.out.println("New user created: " + username);
        } else {
            System.out.println("Welcome back, " + username + "!");
        }
        userMenu(user);
    }
}

private static void userMenu(User user) {
    while (true) {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View All Books");
        System.out.println("2. View Available Books");
        System.out.println("3. Issue Book");
        System.out.println("4. Return Book");
        System.out.println("5. My Issued Books");
        System.out.println("6. Logout");
        System.out.print("Choose option: ");

        switch (scanner.nextLine().trim()) {
            case "1": displayAllBooks(); break;
            case "2": displayAvailableBooks(); break;
            case "3": issueBook(user); break;
            case "4": returnBook(user); break;
            case "5": user.displayIssuedBooks(); break;
            case "6":
                System.out.println("Logging out...");
                dao.updateData();
                return;
            default: System.out.println("[Error] Invalid choice. Try again.");
        }
    }
}

private static void displayAllBooks() {
    System.out.printf("%-30s %-20s %-10s\n", "Title", "Author", "Status");
    for (Book b : dao.getBooks()) b.displayInfo();
}

private static void displayAvailableBooks() {
    boolean found = false;
    System.out.printf("%-30s %-20s %-10s\n", "Title", "Author", "Status");
    for (Book b : dao.getBooks()) {
        if (b.isAvailable()) {
            b.displayInfo();
            found = true;
        }
    }
    if (!found) System.out.println("No available books.");
}

private static void issueBook(User user) {
    System.out.print("Enter book title to issue: ");
    String title = scanner.nextLine().trim();
    Book book = dao.findBook(title);
    if (book == null) {
        System.out.println("Book not found.");
    } else if (!book.isAvailable()) {
        System.out.println("Book is already issued.");
    } else if (user.issueBook(book)) {
        dao.updateData();
        System.out.println("Book issued successfully.");
    } else {
        System.out.println("Cannot issue more than 4 books.");
    }
}

private static void returnBook(User user) {
    System.out.print("Enter book title to return: ");
    String title = scanner.nextLine().trim();
    Book book = dao.findBook(title);
    if (book == null || !user.hasBook(book)) {
        System.out.println("You did not issue this book.");
    } else if (user.returnBook(book)) {
        dao.updateData();
        System.out.println("Book returned successfully.");
    } else {
        System.out.println("Error returning the book.");
    }
}

private static void initializeSampleBooks() {
    if (dao.findBook("The Great Gatsby") == null) dao.addBook(new Book("The Great Gatsby", "F. Scott Fitzgerald"));
    if (dao.findBook("1984") == null) dao.addBook(new Book("1984", "George Orwell"));
    if (dao.findBook("Moby Dick") == null) dao.addBook(new Book("Moby Dick", "Herman Melville"));
    if (dao.findBook("Pride and Prejudice") == null) dao.addBook(new Book("Pride and Prejudice", "Jane Austen"));
}

}
