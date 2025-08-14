package aut.ap.gui;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.Email;
import aut.ap.model.User;
import aut.ap.service.EmailService;
import aut.ap.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;
import java.util.List;

public class GUI {
    private static User loggedInUser;
    private static JFrame frame;
    private static JPanel mainPanel;

    public static void start(){
        frame = new JFrame("Email Client");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        showInitialScreen();

        frame.add(mainPanel);
        frame.setVisible(true);
    }
    private static void showInitialScreen() {
        mainPanel.removeAll();

        JButton loginButton = new JButton("Log in");
        JButton signUpButton = new JButton("Sign Up");
        JButton exitButton = new JButton("Exit");

        loginButton.addActionListener(e -> login());
        signUpButton.addActionListener(e -> signUp());
        exitButton.addActionListener(e -> {
            SingletonSessionFactory.close();
            System.exit(0);
        });

        mainPanel.add(loginButton);
        mainPanel.add(signUpButton);
        mainPanel.add(exitButton);

        frame.revalidate();
        frame.repaint();
    }

    private static void showLoggedInScreen() {
        mainPanel.removeAll();

        JButton sendEmailButton = new JButton("Send Email");
        JButton viewEmailsButton = new JButton("View Emails");
        JButton replyButton = new JButton("Reply");
        JButton forwardButton = new JButton("Forward");
        JButton logoutButton = new JButton("Logout");
        JButton exitButton = new JButton("Exit");

        sendEmailButton.addActionListener(e -> sendEmail());
        viewEmailsButton.addActionListener(e -> viewEmails());
        replyButton.addActionListener(e -> replyEmail());
        forwardButton.addActionListener(e -> forwardEmail());
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            showInitialScreen();
            JOptionPane.showMessageDialog(frame, "Logged out successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        exitButton.addActionListener(e -> {
            SingletonSessionFactory.close();
            System.exit(0);
        });

        mainPanel.setLayout(new GridLayout(3, 2, 10, 10));
        mainPanel.add(sendEmailButton);
        mainPanel.add(viewEmailsButton);
        mainPanel.add(replyButton);
        mainPanel.add(forwardButton);
        mainPanel.add(logoutButton);
        mainPanel.add(exitButton);

        frame.revalidate();
        frame.repaint();
    }

    private static void login() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (!email.endsWith("@Milou.com")) {
                    email = email + "@Milou.com";
                }

                loggedInUser = UserService.logIn(email, password);
                if (loggedInUser != null) {
                    JOptionPane.showMessageDialog(frame,
                            "Welcome back, " + loggedInUser.getName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    showLoggedInScreen();
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Login failed. Please check your credentials.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame,
                        "Error during login: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void signUp() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Register",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                boolean success = UserService.register(name, email, password);
                if (success) {
                    JOptionPane.showMessageDialog(frame,
                            "Registration successful! You can now login.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Registration failed. Email may already exist or password is too weak.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame,
                        "Error during registration: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void sendEmail() {
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(frame,
                    "Please login first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel recipientsLabel = new JLabel("Recipients (comma separated):");
        JLabel subjectLabel = new JLabel("Subject:");
        JTextField recipientsField = new JTextField();
        JTextField subjectField = new JTextField();
        inputPanel.add(recipientsLabel);
        inputPanel.add(recipientsField);
        inputPanel.add(subjectLabel);
        inputPanel.add(subjectField);

        JLabel bodyLabel = new JLabel("Body:");
        JTextArea bodyArea = new JTextArea(10, 30);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(bodyLabel, BorderLayout.CENTER);
        panel.add(bodyScroll, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Send Email",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String[] recipients = recipientsField.getText().split("\\s*,\\s*");
                String subject = subjectField.getText();
                String body = bodyArea.getText();

                boolean sent = EmailService.sendEmail(recipients, subject, body, loggedInUser.getEmail());
                if (sent) {
                    JOptionPane.showMessageDialog(frame,
                            "Email sent successfully!\nCode: " + EmailService.outputCode,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to send email",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame,
                        "Error sending email: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void viewEmails() {
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(frame,
                    "Please login first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton allEmailsButton = new JButton("All Emails");
        JButton unreadEmailsButton = new JButton("Unread Emails");
        JButton sentEmailsButton = new JButton("Sent Emails");
        JButton getEmailButton = new JButton("Get Email by Code");

        buttonPanel.add(allEmailsButton);
        buttonPanel.add(unreadEmailsButton);
        buttonPanel.add(sentEmailsButton);
        buttonPanel.add(getEmailButton);

        JTextArea emailsArea = new JTextArea(20, 60);
        emailsArea.setEditable(false);
        JScrollPane emailsScroll = new JScrollPane(emailsArea);

        JPanel getEmailPanel = new JPanel(new BorderLayout());
        JPanel codeInputPanel = new JPanel();
        JLabel codeLabel = new JLabel("Enter Email Code:");
        JTextField codeField = new JTextField(15);
        JButton fetchEmailButton = new JButton("Fetch Email");

        codeInputPanel.add(codeLabel);
        codeInputPanel.add(codeField);
        codeInputPanel.add(fetchEmailButton);

        getEmailPanel.add(codeInputPanel, BorderLayout.NORTH);
        getEmailPanel.setVisible(false);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(emailsScroll, BorderLayout.CENTER);
        mainPanel.add(getEmailPanel, BorderLayout.SOUTH);

        allEmailsButton.addActionListener(e -> {
            getEmailPanel.setVisible(false);
            try {
                List<Email> allEmails = EmailService.AllEmails(loggedInUser.getEmail());
                String allEmailsText = allEmails.stream()
                        .map(Email::toString)
                        .collect(Collectors.joining("\n\n"));
                emailsArea.setText(allEmailsText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error loading emails: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        unreadEmailsButton.addActionListener(e -> {
            getEmailPanel.setVisible(false);
            try {
                List<Email> unreadEmails = EmailService.unreadEmails(loggedInUser.getEmail());
                String unreadEmailsText = unreadEmails.stream()
                        .map(Email::toString)
                        .collect(Collectors.joining("\n\n"));
                emailsArea.setText(unreadEmailsText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error loading unread emails: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        sentEmailsButton.addActionListener(e -> {
            getEmailPanel.setVisible(false);
            try {
                List<Email> sentEmails = EmailService.sentEmails(loggedInUser.getEmail());
                String sentEmailsText = sentEmails.stream()
                        .map(Email::toString)
                        .collect(Collectors.joining("\n\n"));
                emailsArea.setText(sentEmailsText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error loading sent emails: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        getEmailButton.addActionListener(e -> {
            getEmailPanel.setVisible(true);
            emailsArea.setText("");
        });

        fetchEmailButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter an email code",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Email email = EmailService.getEmailByCode(code, loggedInUser.getEmail());
                if (email != null) {
                    String emailDetails = String.format(
                            "Code: %s\nFrom: %s\nSubject: %s\nDate: %s\n\n%s",
                            email.getCode(),
                            email.getSender() != null ? email.getSender().getEmail() : "Unknown",
                            email.getSubject(),
                            email.getDate(),
                            email.getBody()
                    );
                    emailsArea.setText(emailDetails);
                } else {
                    emailsArea.setText("No email found with code: " + code);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error retrieving email: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JOptionPane.showMessageDialog(frame, mainPanel, "View Emails",
                JOptionPane.PLAIN_MESSAGE, null);
    }

    private static void replyEmail() {
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(frame,
                    "Please login first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel codeLabel = new JLabel("Email Code to Reply:");
        JTextField codeField = new JTextField();
        JLabel bodyLabel = new JLabel("Reply Body:");
        inputPanel.add(codeLabel);
        inputPanel.add(codeField);
        inputPanel.add(bodyLabel);

        JTextArea bodyArea = new JTextArea(10, 30);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(bodyScroll, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Reply to Email",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String code = codeField.getText().trim();
                String body = bodyArea.getText();

                boolean replied = EmailService.reply(loggedInUser.getEmail(), code, body);
                if (replied) {
                    JOptionPane.showMessageDialog(frame,
                            "Reply sent successfully!\nCode: " + EmailService.outputCode,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to send reply",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame,
                        "Error replying to email: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void forwardEmail() {
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(frame,
                    "Please login first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel codeLabel = new JLabel("Email Code to Forward:");
        JTextField codeField = new JTextField();
        JLabel recipientsLabel = new JLabel("New Recipients (comma separated):");
        JTextField recipientsField = new JTextField();

        inputPanel.add(codeLabel);
        inputPanel.add(codeField);
        inputPanel.add(recipientsLabel);
        inputPanel.add(recipientsField);

        panel.add(inputPanel, BorderLayout.NORTH);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Forward Email",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String code = codeField.getText().trim();
                String[] recipients = recipientsField.getText().split("\\s*,\\s*");

                boolean forwarded = EmailService.forward(code, recipients, loggedInUser.getEmail());
                if (forwarded) {
                    JOptionPane.showMessageDialog(frame,
                            "Email forwarded successfully!\nCode: " + EmailService.outputCode,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to forward email",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame,
                        "Error forwarding email: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
