package boardgameProcess.helper;

import java.util.*;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.PasswordAuthentication;

public class Email {
    private final static Logger LOGGER = Logger.getLogger(Database.class.getName());
    private final static Email INSTANCE = new Email();

    private Properties properties;
    private Session session;

    private Email() {
        try {
            properties = System.getProperties();
            properties.load(getClass().getClassLoader().getResourceAsStream("email.properties"));
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
            properties.put("mail.smtp.connectiontimeout", "5000");
            properties.put("mail.smtp.timeout", "5000");

            session = Session.getInstance(
                    properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    properties.getProperty("mail.smtp.user"),
                                    properties.getProperty("mail.smtp.pass"));
                        }
                    });

            LOGGER.info("\n\n\n"+Email.class.getName() +" - email created SUCCESSFUL\n\n\n");
        } catch (Exception e) {
            LOGGER.severe("\n\n\n" + Email.class.getName() + " - email setup FAILURE\n");
            e.printStackTrace();
        }
    }

    public static Email getInstance() {
        return Email.INSTANCE;
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
