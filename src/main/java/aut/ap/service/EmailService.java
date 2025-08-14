package aut.ap.service;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.Email;
import aut.ap.model.EmailRecipient;
import aut.ap.model.User;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class EmailService {
    public static String outputCode;

    public static boolean sendEmail(String[] recipients, String subject, String body, String senderemail) {
        if (recipients == null || recipients.length == 0) {
            return false;
        }

        if (subject == null || subject.trim().isEmpty()) {
            return false;
        }
        try {
            SingletonSessionFactory.get().inTransaction(session -> {
                User sender = session.createQuery("from User where email = :givenemail", User.class)
                        .setParameter("givenemail", senderemail)
                        .getSingleResult();

                LocalDate date = LocalDate.now();
                String generatedCode;
                do {
                    generatedCode = generateRandomCode();
                } while (codeExists(generatedCode));

                Email newemail = new Email(sender, subject, body, date, generatedCode, null);
                session.persist(newemail);

                for (String email : recipients) {
                    if (!email.endsWith("@Milou.com")) {
                        email = email + "@Milou.com";
                    }

                    User user = session.createQuery("from User where email = :givenemail", User.class)
                            .setParameter("givenemail", email)
                            .getSingleResult();

                    EmailRecipient emailRecipient = new EmailRecipient(newemail, user);
                    session.persist(emailRecipient);
                }
                outputCode = generatedCode;
            });
            return true;
        } catch (NoResultException e) {
            System.err.println("User not found: " + e.getMessage());
            return false;
        } catch (PersistenceException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    public static List<Email> unreadEmails(String email) {
        List<Email> unReadEmails = SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select e.* from emails e " +
                                        "left join email_recipients er on e.id = er.email_id " +
                                        "left join users u on er.recipient_id = u.id " +
                                        "where u.email = :givenEmail and er.is_read = false " +
                                        "order by e.date desc"
                                , Email.class)
                        .setParameter("givenEmail", email)
                        .getResultList()
        );

        System.out.println("Unread Emails:");
        for (Email unRead : unReadEmails) {
            System.out.println("+" + unRead.toString());
        }
        return unReadEmails;
    }

    public static List<Email> AllEmails(String email) {
        List<Email> AllEmails = SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select e.* from emails e " +
                                        "left join email_recipients er on e.id = er.email_id " +
                                        "left join users u on er.recipient_id = u.id " +
                                        "where u.email = :givenEmail " +
                                        "order by e.date desc"
                                , Email.class)
                        .setParameter("givenEmail", email)
                        .getResultList()
        );

        System.out.println("All Emails:");
        for (Email e : AllEmails) {
            System.out.println("+" + e.toString());
        }
        return AllEmails;
    }

    public static Email getEmailByCode(String code, String userEmail) {
        final Email[] getEmail = {null};
        if (!userEmail.endsWith("@Milou.com")) {
            userEmail = userEmail + "@Milou.com";
        }
        String finalUserEmail = userEmail;

        SingletonSessionFactory.get().inTransaction(session -> {
            Email originalEmail = session.createQuery(
                            "from Email where code = :givenCode", Email.class)
                    .setParameter("givenCode", code)
                    .getSingleResult();

            User user = session.createQuery("from User where email = :givenemail", User.class)
                    .setParameter("givenemail", finalUserEmail)
                    .getSingleResult();

            if (originalEmail == null) {
                throw new RuntimeException("Original email not found for code: " + code);
            }

            List<User> recipients = session.createQuery(
                            "select u from User u " +
                                    "join EmailRecipient er on er.recipient = u " +
                                    "join Email e on e = er.email " +
                                    "where e.code = :givenCode", User.class)

                    .setParameter("givenCode", code)
                    .getResultList();

            if (!user.getId().equals(originalEmail.getSender().getId()) &&
                    recipients.stream().noneMatch(r -> r.getId().equals(user.getId()))) {
                throw new RuntimeException("You cannot read this email.");
            }

            List<EmailRecipient> recipientRecord = session.createQuery(
                            "from EmailRecipient where email = :email and recipient = :recipient",
                            EmailRecipient.class)
                    .setParameter("email", originalEmail)
                    .setParameter("recipient", user)
                    .getResultList();

            for ( EmailRecipient emailRecipient : recipientRecord){
                if (user.equals(emailRecipient.getRecipient())) {
                    emailRecipient.setRead(true);
                    session.merge(emailRecipient);
                }
            }

            System.out.println("Email:");
            System.out.println(originalEmail.getCode() + "\n");
            System.out.print("Recipient(s): ");
            for (User user1 : recipients) {
                System.out.print(user1.getEmail() + ",");
            }
            System.out.println("\n" + originalEmail.getSubject() + "\n" + originalEmail.getDate());
            System.out.println(originalEmail.getBody());
            System.out.println();
            getEmail[0] = originalEmail;
        });
    return getEmail[0];
    }

    public static List<Email> sentEmails(String email) {
        List<Email> sentEmails = SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select e.* from emails e " +
                                        "left join users u on e.sender_id = u.id " +
                                        "where u.email = :givenEmail " +
                                        "order by e.date desc"
                                , Email.class)
                        .setParameter("givenEmail", email)
                        .getResultList()
        );

        System.out.println("Sent Emails:");
        for (Email unRead : sentEmails) {
            System.out.println("+" + unRead.toString());
        }
        return sentEmails;
    }

    public static boolean reply(String senderEmail, String code, String body) {
        try {
            SingletonSessionFactory.get().inTransaction(session -> {
                Email originalEmail = session.createQuery(
                                "from Email where code = :givenCode", Email.class)
                        .setParameter("givenCode", code)
                        .getSingleResult();

                if (originalEmail == null) {
                    throw new RuntimeException("Original email not found for code: " + code);
                }

                User newSender = session.createQuery(
                                "from User where email = :givenEmail", User.class)
                        .setParameter("givenEmail", senderEmail)
                        .getSingleResult();

                List<User> recipients = session.createQuery(
                                "select u from User u " +
                                        "join EmailRecipient er on er.recipient = u " +
                                        "join Email e on e = er.email " +
                                        "where e.code = :givenCode", User.class)
                        .setParameter("givenCode", code)
                        .getResultList();

                String replySubject = "[RE] " + originalEmail.getSubject();

                String generatedCode;
                do {
                    generatedCode = generateRandomCode();
                } while (codeExists(generatedCode));

                Email replyEmail = new Email(newSender, replySubject, body, LocalDate.now(), generatedCode, originalEmail);
                replyEmail.setReply(true);
                session.persist(replyEmail);

                EmailRecipient originalRecipient = new EmailRecipient(replyEmail, originalEmail.getSender());
                session.persist(originalRecipient);

                for (User user : recipients) {
                    if (!user.equals(newSender)) {
                        EmailRecipient emailRecipient = new EmailRecipient(replyEmail, user);
                        session.persist(emailRecipient);
                    }
                }
                outputCode = generatedCode;
            });
            return true;
        } catch (NoResultException e) {
            System.err.println("User or email not found: " + e.getMessage());
            return false;
        } catch (PersistenceException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    public static boolean forward(String code, String[] recipients, String senderEmail) {
        try {
            SingletonSessionFactory.get().inTransaction(session -> {
                Email originalEmail = session.createQuery(
                                "from Email where code = :givenCode", Email.class)
                        .setParameter("givenCode", code)
                        .getSingleResult();

                if (originalEmail == null) {
                    throw new RuntimeException("Original email not found for code: " + code);
                }

                User newSender = session.createQuery(
                                "from User where email = :givenEmail", User.class)
                        .setParameter("givenEmail", senderEmail)
                        .getSingleResult();

                String replySubject = "[Fw] " + originalEmail.getSubject();

                String generatedCode;
                do {
                    generatedCode = generateRandomCode();
                } while (codeExists(generatedCode));

                Email forwardEmail = new Email(newSender, replySubject, originalEmail.getBody(), LocalDate.now(), generatedCode, originalEmail);
                forwardEmail.setForward(true);
                session.persist(forwardEmail);

                for (String email : recipients) {
                    if (!email.endsWith("@Milou.com")) {
                        email = email + "@Milou.com";
                    }
                    User user = session.createQuery("from User where email = :givenemail", User.class)
                            .setParameter("givenemail", email)
                            .getSingleResult();

                    EmailRecipient emailRecipient = new EmailRecipient(forwardEmail, user);
                    session.persist(emailRecipient);
                }
                outputCode = generatedCode;
            });
            return true;
        } catch (NoResultException e) {
            System.err.println("User or email not found: " + e.getMessage());
            return false;
        } catch (PersistenceException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    private static String generateRandomCode() {
        final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static boolean codeExists(String code) {
        List<String> existingCodes = SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select e.code from emails e where e.code = :code", String.class)
                        .setParameter("code", code)
                        .getResultList()
        );

        return !existingCodes.isEmpty();
    }
}
