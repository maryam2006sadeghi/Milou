package aut.ap.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "email_recipients")
@IdClass(EmailRecipient.EmailRecipientId.class)
public class EmailRecipient {
    @Id
    @ManyToOne
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;

    @Id
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    public EmailRecipient() {
    }

    public EmailRecipient(Email email, User recipient) {
        this.email = email;
        this.recipient = recipient;
        this.isRead = false;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "EmailRecipient{\n" +
                "\temail_id=" + email.getId() + ", \n" +
                "\trecipient_id==" + recipient.getId() + ", \n" +
                '}';
    }

    public static class EmailRecipientId implements Serializable {
        private Integer email;
        private Integer recipient;

        public EmailRecipientId() {
        }

        public EmailRecipientId(Integer email, Integer recipient) {
            this.email = email;
            this.recipient = recipient;
        }

        public Integer getEmail() {
            return email;
        }

        public void setEmail(Integer email) {
            this.email = email;
        }

        public Integer getRecipient() {
            return recipient;
        }

        public void setRecipient(Integer recipient) {
            this.recipient = recipient;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmailRecipientId that = (EmailRecipientId) o;
            return Objects.equals(email, that.email) &&
                    Objects.equals(recipient, that.recipient);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, recipient);
        }
    }
}