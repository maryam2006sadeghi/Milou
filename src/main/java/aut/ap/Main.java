package aut.ap;

import aut.ap.model.User;
import aut.ap.service.EmailService;
import aut.ap.service.UserService;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
                if (result != null) {
                    EmailService.unreadEmails(email);
                    System.out.print("[S]end, [V]iew, [R]eply, [F]orward: \n");
                    String choice = scn.nextLine().trim().toLowerCase();

                    while (!choice.equals("s") && !choice.equals("v") &&
                            !choice.equals("r") && !choice.equals("f") &&
                            !choice.equals("q") && !choice.equals("send") &&
                            !choice.equals("view") && !choice.equals("reply") &&
                            !choice.equals("forward")) {
                        System.err.println("Invalid input, please try again");
                        choice = scn.nextLine().trim().toLowerCase();
                    }

                    switch (choice) {
                        case "s", "send" -> {
                            System.out.print("Recipient(s)\n");
                            String recipientInput = scn.nextLine();
                            System.out.print("Subject: \n");
                            String subject = scn.nextLine();
                            String[] recipients = recipientInput.split(",");
                            for (int i = 0; i < recipients.length; i++) {
                                recipients[i] = recipients[i].trim();
                                if (!recipients[i].endsWith("@gmail.com")) {
                                    recipients[i] = recipients[i] + "@gmail.com";
                                }
                            }
                            System.out.print("Body: \n");
                            String Body = scn.nextLine();

                            boolean result1 = EmailService.sendEmail(recipients, subject, Body, email);
                            if (result1) {
                                System.out.println("Successfully sent your email.\n");
                                System.out.print("Code: " + EmailService.code);
                            } else {
                                System.err.println("Failed to send email. Please check recipient addresses or try again.");
                            }
                        }
                    }
                }

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