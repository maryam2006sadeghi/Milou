# Milou – A Mini Email Service

Milou is a lightweight, console-based email service inspired by Tintin's loyal dog. It allows users to sign up, log in, send, view, reply to, and forward emails through a simple command-line interface.

## 📋 Features

### 👤 User Management
- **Sign up** with a unique email and password
- **Login** with either full email (`user@milou.com`) or just the username (`user`)
- **Password validation** (minimum 8 characters)
- **Unique email validation** to prevent duplicates

### 📧 Email Operations
- **Send emails** to one or multiple recipients
- **Auto-generate** a unique 6-character code for every email
- **View inbox**: all emails, unread emails, or sent emails
- **Read emails by code** (only if you're the sender or recipient)
- **Mark emails as read** after opening

### 🔄 Advanced Actions
- **Reply**: Send a response to all original recipients (with `[Re]` prefix in subject)
- **Forward**: Send existing emails to new recipients (with `[Fw]` prefix in subject)
- **Smart sorting**: All email lists sorted from newest to oldest

### 🛡️ Robust Error Handling
- Prevents users from reading others' emails
- Handles invalid inputs gracefully
- Prevents duplicate sign-ups and weak passwords
- Protects against excessively long subjects or invalid recipients
- Logs errors to file instead of terminal

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### Installation

1. **Clone the repository**
   git clone https://github.com/yourusername/milou.git
   cd milou
