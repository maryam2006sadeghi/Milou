package aut.ap.model;

import aut.ap.framework.IdEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "emails")
public class Email extends IdEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Basic(optional = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String body;

    @Basic(optional = false)
    private LocalDate date;

    @Column(unique = true, nullable = false, length = 6)
    private String code;

    @Column(name = "is_reply")
    private boolean isReply = false;

    @Column(name = "is_forward")
    private boolean isForward = false;

    @ManyToOne
    @JoinColumn(name = "parent_email_id")
    private Email parentEmail;

    public Email() {
    }

    public Email(User sender, String subject, String body, LocalDate date, String code, Email parentEmail) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.code = code;
        this.parentEmail = parentEmail;
        this.isReply = false;
        this.isForward = false;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean forward) {
        isForward = forward;
    }

    public Email getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(Email parentEmail) {
        this.parentEmail = parentEmail;
    }

    @Override
    public String toString() {
        return "Email{\n" +
                "\tsender ='" + sender.getEmail() + "'\n" +
                "\tsubject ='" + subject + "'\n" +
                "\tcode='" + code + "'\n" +
                '}';
    }
}