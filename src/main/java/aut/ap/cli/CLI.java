package aut.ap.cli;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.User;
import aut.ap.service.EmailService;
import aut.ap.service.UserService;

import java.util.Locale;
import java.util.Scanner;

public class CLI {
    public static void start(){
        Scanner scn = new Scanner(System.in);
        boolean running = true;

        while (running) {
            User loggedInUser;
            System.out.println("[L]ogin, [S]ign up, [E]xit");
            String ans = scn.nextLine().trim().toLowerCase(Locale.ENGLISH);

            while (!ans.equals("s") && !ans.equals("l") && !ans.equals("e") &&
                    !ans.equals("login") && !ans.equals("sign up") && !ans.equals("exit")) {
                System.err.print("Invalid Input, try again \n");
                ans = scn.nextLine().trim().toLowerCase(Locale.ENGLISH);
            }

            if (ans.equals("s") || ans.equals("sign up")) {
                System.out.print("Name:\n");
                String name = scn.nextLine();
                System.out.print("Email: \n");
                String email = scn.nextLine();
                System.out.print("Password (min 8 characters): \n");
                String password = scn.nextLine();

                if (!UserService.register(name, email, password)) {
                    System.err.print("Registration failed, Invalid email or password.\n");
                    continue;
                }
            }

            if (ans.equals("e") || ans.equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (ans.equals("l") || ans.equals("login")) {
                System.out.print("Email: \n");
                String email = scn.nextLine();
                if (!email.endsWith("@Milou.com")) {
                    email = email + "@Milou.com";
                }
                System.out.print("Password: \n");
                String password = scn.nextLine();

                loggedInUser = UserService.logIn(email, password);
                if (loggedInUser == null) {
                    System.err.println("Login failed. Please try again.");
                    continue;
                }

                boolean loggedIn = true;
                while (loggedIn) {
                    EmailService.unreadEmails(loggedInUser.getEmail());

                    System.out.print("[S]end, [V]iew, [R]eply, [F]orward, [L]ogout, E[x]it: \n");
                    String choice = scn.nextLine().trim().toLowerCase();

                    switch (choice) {
                        case "s", "send" -> {
                            System.out.print("Recipient(s)\n");
                            String recipientInput = scn.nextLine();
                            System.out.print("Subject: \n");
                            String subject = scn.nextLine();
                            String[] recipients = recipientInput.split(",");
                            for (int i = 0; i < recipients.length; i++) {
                                recipients[i] = recipients[i].trim();
                                if (!recipients[i].endsWith("@Milou.com")) {
                                    recipients[i] = recipients[i] + "@Milou.com";
                                }
                            }
                            System.out.print("Body: \n");
                            String body = scn.nextLine();

                            boolean sent = EmailService.sendEmail(recipients, subject, body, loggedInUser.getEmail());
                            if (sent) {
                                System.out.println("Successfully sent your email.\n Code: " + EmailService.outputCode);
                            } else {
                                System.err.println("Failed to send email. Please check recipient address or or try again");
                            }
                        }
                        case "r", "reply" -> {
                            System.out.print("Code\n");
                            String code = scn.nextLine();
                            System.out.print("Body: \n");
                            String body = scn.nextLine();
                            boolean replied = EmailService.reply(loggedInUser.getEmail(), code, body);
                            if (replied) {
                                System.out.println("Successfully sent your reply to email " + code + "\n Code: "+ EmailService.outputCode);
                            } else {
                                System.err.println("Failed to reply email. Please check the code or try again\n");
                            }
                        }
                        case "f", "forward" -> {
                            System.out.print("Code\n");
                            String code = scn.nextLine();
                            System.out.print("Recipient(s)\n");
                            String recipientInput = scn.nextLine();
                            String[] recipients = recipientInput.split(",");
                            for (int i = 0; i < recipients.length; i++) {
                                recipients[i] = recipients[i].trim();
                                if (!recipients[i].endsWith("@Milou.com")) {
                                    recipients[i] = recipients[i] + "@Milou.com";
                                }
                            }
                            boolean forwarded = EmailService.forward(code, recipients, loggedInUser.getEmail());
                            if (forwarded) {
                                System.out.println("Successfully forwarded your email. \n Code: " + EmailService.outputCode);
                            } else {
                                System.err.println("Failed to forward email. Please check the code or try again");
                            }
                        }
                        case "v", "view" -> {
                            System.out.println("[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode:");
                            String viewChoice = scn.nextLine().trim().toLowerCase();
                            switch (viewChoice) {
                                case "a", "all emails" -> EmailService.AllEmails(loggedInUser.getEmail());
                                case "u", "unread emails" -> EmailService.unreadEmails(loggedInUser.getEmail());
                                case "s", "sent emails" -> EmailService.sentEmails(loggedInUser.getEmail());
                                case "c", "read by [c]ode" -> {
                                    System.out.print("Code\n");
                                    String code = scn.nextLine();
                                    EmailService.getEmailByCode(code, loggedInUser.getEmail());
                                }
                            }
                        }
                        case "l", "logout" -> {
                            loggedIn = false;
                            System.out.println("Logged out.");
                        }
                        case "x", "exit" -> {
                            loggedIn = false;
                            running = false;
                            System.out.println("Goodbye!");
                        }
                        default -> System.err.println("Invalid choice. Try again.");
                    }
                }
            }
        }
        SingletonSessionFactory.close();
    }
}

