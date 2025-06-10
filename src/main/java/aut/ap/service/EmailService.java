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
                String generatedcode;
                do {
                    generatedcode = generateRandomCode();
                } while (codeExists(generatedcode));

                Email newemail = new Email(sender, subject, body, date, generatedcode, null);
                session.persist(newemail);

                for (String email : recipients) {
                    User user = session.createQuery("from User where email = :givenemail", User.class)
                            .setParameter("givenemail", email)
                            .getSingleResult();

                    EmailRecipient emailRecipient = new EmailRecipient(newemail, user);
                    session.persist(emailRecipient);
                }
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
