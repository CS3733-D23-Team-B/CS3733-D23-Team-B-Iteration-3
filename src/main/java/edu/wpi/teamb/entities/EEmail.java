package edu.wpi.teamb.entities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import static javax.mail.Message.RecipientType.TO;

public class EEmail {
  private Gmail service;
  private int currentVerificationCode;

  private EEmail() {
    try {
      final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
      this.service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, JSON_FACTORY))
              .setApplicationName("TestMailer")
              .build();
    }
    catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }
  }

  private static class SingletonHelper {
    //Nested class is referenced after getEEmail() is called
    private static final EEmail eEmail = new EEmail();
  }

  public static EEmail getEEmail() {
    return SingletonHelper.eEmail;
  }

  private static final String fromEmailAddress = "teambD2023@gmail.com";
//  private static final String toEmailAddress = "teambD2023@gmail.com";

  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, GsonFactory JSON_FACTORY)
          throws IOException {
    // Load client secrets.
//    InputStream in = EEmail.class.getResourceAsStream("/...json");
//    if (in == null) {
//      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//    }
    GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(EEmail.class.getResourceAsStream("/TwoFactorAutheticationClientSecrets/client_secret.json")));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("src/main/resources/token")))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    //returns an authorized Credential object.
    return credential;
  }

  public void sendMail(String toEmailAddress, String subject, String msg) {

    // Encode as MIME message
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    MimeMessage email = new MimeMessage(session);
    try {
      email.setFrom(new InternetAddress(fromEmailAddress));
      email.addRecipient(TO, new InternetAddress(toEmailAddress));
      email.setSubject(subject);
      email.setText(msg);
    } catch (javax.mail.MessagingException e) {
      e.printStackTrace();
    }

    // Encode and wrap the MIME message into a gmail message
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try {
      email.writeTo(buffer);
    } catch (MessagingException | IOException e) {
      e.printStackTrace();
    }
    byte[] rawMessageBytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
    Message message = new Message();
    message.setRaw(encodedEmail);

    try {
      // Create send message
      message = service.users().messages().send("me", message).execute();
      System.out.println("Message id: " + message.getId());
      System.out.println(message.toPrettyString());
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 403) {
        System.err.println("Unable to send message: " + e.getDetails());
      } else {
        try {
          throw e;
        } catch(GoogleJsonResponseException f) {
          f.printStackTrace();
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void generateVerificationCode() {
    //generate a random 6 digit number
    Random rand = new Random();
    int min = 100000;
    int max = 999999;
    currentVerificationCode = rand.nextInt((max - min) + 1) + min;
  }

  public int getCurrentVerificationCode() {
    return this.currentVerificationCode;
  }

  public void sendVerificationCodeEmail(String toEmailAddress) {
    generateVerificationCode();
    sendMail(
            toEmailAddress,
            "Verification Code for Hospital App Login",
            "Your verification code is: " + currentVerificationCode
                    + ".\n\nPlease enter this code to login to your account."
                    + "\n\nIf you did not request this code, please ignore this email." +
                    "\n\nThank you,\nHospital App Team");
  }
}
