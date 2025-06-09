package aut.ap;

import aut.ap.model.User;
import aut.ap.service.UserService;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setProperty("org.hibernate.SQL", "OFF");
        System.setProperty("org.hibernate.type.descriptor.sql.BasicBinder", "OFF");

        Scanner scn = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print("[L]ogin, [S]ign up \n");
            String ans = scn.nextLine().trim().toLowerCase(Locale.ENGLISH);

            while (!ans.equals("s") && !ans.equals("l") &&
                    !ans.equals("login") && !ans.equals("sign up")) {
                System.err.print("Invalid Input, try again \n");
                ans = scn.nextLine().trim().toLowerCase(Locale.ENGLISH);
            }

            if (ans.equals("s") || ans.equals("sign up")) {
                System.out.print("Name:\n");
                String name = scn.nextLine();
                System.out.print("Email: \n");
                String email = scn.nextLine();
                if (!email.endsWith("@gmail.com")) {
                    email = email + "@gmail.com";
                }
                System.out.print("Password (min 8 characters): \n");
                String password = scn.nextLine();

                if (!UserService.register(name, email, password)) {
                    System.err.print("Registration failed, Invalid email or password \ntry again :\n");
                    continue;
                }

            } else if (ans.equals("l") || ans.equals("login")) {
                System.out.print("Email: \n");
                String email = scn.nextLine();
                if (!email.endsWith("@gmail.com")) {
                    email = email + "@gmail.com";
                }
                System.out.print("Password: \n");
                String password = scn.nextLine();

                User result = UserService.logIn(email, password);

                System.out.println("Do you want to continue? [Y/N]");
                String continueChoice = scn.nextLine().toLowerCase(Locale.ENGLISH);
                if (continueChoice.equals("n")) {
                    running = false;
                }

            } else if (!running) {
                System.out.println("Goodbye!");
            }
        }
    }
}