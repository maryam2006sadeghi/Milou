package aut.ap.service;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.User;

import java.util.List;

public class UserService {
    public static boolean register(String name, String email, String password) {
        try {
            if (name == null || name.trim().isEmpty()) {
                System.err.println("Registration failed: Name cannot be empty");
                return false;
            }

            if (email == null || email.trim().isEmpty()) {
                System.err.println("Registration failed: Email cannot be empty");
                return false;
            }

            if (!email.endsWith("@Milou.com")) {
                email = email + "@Milou.com";
            }

            if (emailExists(email)) {
                System.err.println("An account with this email already exists");
                return false;
            }

            if (password.length() < 8) {
                System.err.println("weak password");
                return false;
            }

            User newUser = new User(name, email, password);
            SingletonSessionFactory.get().inTransaction(session -> session.persist(newUser));

            System.out.print("Your new account is created. \nGo ahead and login!\n");
            return true;

        } catch (Exception e) {
            System.err.println("An unexpected error occurred during registration: " + e.getMessage());
            return false;
        }
    }

    public static User logIn(String email, String password) {
        try {
            if (email == null || email.trim().isEmpty()) {
                System.err.println("Login failed: Email cannot be empty");
                return null;
            }

            if (password == null || password.trim().isEmpty()) {
                System.err.println("Login failed: Password cannot be empty");
                return null;
            }

            User user = SingletonSessionFactory.get().fromTransaction(session ->
                    session.createNativeQuery(
                                    "select * from users where email = :email and password = :password",
                                    User.class)
                            .setParameter("email", email)
                            .setParameter("password", password)
                            .getSingleResult()
            );

            if (user == null) {
                System.err.println("Login failed: Invalid email or password");
                return null;
            }

            System.out.println("Welcome back, " + user.getName() + "!");
            return user;


        } catch (Exception e) {
            System.err.println("An unexpected error occurred during login: " + e.getMessage());
            return null;
        }
    }

    public static boolean emailExists(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                System.err.println("Error: Email cannot be null or empty");
                return false;
            }

            List<Integer> ids = SingletonSessionFactory.get().fromTransaction(session ->
                    session.createNativeQuery(
                                    "select id from users where email = :email",
                                    Integer.class)
                            .setParameter("email", email)
                            .getResultList()
            );

            return !ids.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return false;
        }
    }
}