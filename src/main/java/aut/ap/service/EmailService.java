package aut.ap.service;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.Email;
import aut.ap.model.EmailRecipient;
import aut.ap.model.User;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class EmailService {
    public static String code;

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
                    User user = session.createQuery("from User where email = :givenemail", User.class)
                            .setParameter("givenemail", email)
                            .getSingleResult();

                    EmailRecipient emailRecipient = new EmailRecipient(newemail, user);
                    session.persist(emailRecipient);
                }
                code = generatedCode;
            });
            return true;
        } catch (NoResultException e) {
            System.err.println("User not found: " + e.getMessage());
            return false;
        } catch (jakarta.persistence.PersistenceException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    public static void unreadEmails(String email) {
        List<Email> unReadEmails = SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select e.* from emails e " +
                                        "left join email_recipients er on e.id = er.email_id " +
                                        "left join users u on er.recipient_id = u.id " +
                                        "where u.email = :givenEmail and er.is_read = false "
                                        , Email.class)
                        .setParameter("givenEmail", email)
                        .getResultList()
        );

        System.out.println("Unread Emails:");
        for (Email unRead : unReadEmails) {
            System.out.println("+" + unRead.toString());
        }
    }

    private static String generateRandomCode() {
        final String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
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
