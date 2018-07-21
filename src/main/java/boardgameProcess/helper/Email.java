package boardgameProcess.helper;

import java.util.*;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Email {
    private final static Logger LOGGER = Logger.getLogger(Database.class.getName());

    private Properties properties;
    private Session session;

    private final static Email INSTANCE = new Email();

    private Email() {
        try {
            properties = System.getProperties();
            properties.load(getClass().getClassLoader().getResourceAsStream("email.properties"));
            properties.put("mail.smtp.auth", "true");

            Authenticator authenticator = new SMTPAuthenticator();
            session = Session.getDefaultInstance(properties, authenticator);

            LOGGER.info("\n\n\n"+Email.class.getName() +" - email created SUCCESSFUL\n\n\n");
        } catch (Exception e) {
            LOGGER.severe("\n\n\n" + Email.class.getName() + " - email setup FAILURE\n");
            e.printStackTrace();
        }
    }

    public static Email getInstance() {
        return Email.INSTANCE;
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = properties.getProperty("mail.smtp.user");
            String password = properties.getProperty("mail.smtp.pass");

            return new PasswordAuthentication(username, password);
        }
    }

    public void sendEmail(String to, String from, String subject, String content) {
        try {
            // create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // set message
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(content);

            // send message
            Transport.send(message);

            LOGGER.info("\n\n\n" + Email.class.getName() + " - email sent SUCCESSFUL" +
                    "\nfrom: " + from +
                    "\nto: " + to +
                    "\nsubject: " + subject +
                    "\ncontent: " + content + "\n\n\n");
        } catch (MessagingException e) {
            LOGGER.severe("\n\n\n" + Email.class.getName() + " - email sent FAILURE\n");
            e.printStackTrace();
        }
    }
}
