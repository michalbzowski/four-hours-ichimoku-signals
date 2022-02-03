package pl.bzowski.signalsreport;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class SignalsReport {

  private static final Logger LOG = Logger.getLogger(SignalsReport.class);

  private MailConfig mailConfig;
  private SignalsReportRepository signalsReportRepository;

  

  @Inject
  public SignalsReport(MailConfig mailConfig, SignalsReportRepository signalsReportRepository) {
    this.mailConfig = mailConfig;
    this.signalsReportRepository = signalsReportRepository;
  }

  @Scheduled(cron = "{signals.report.cron}")
  public void sendSignalsReport() {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
    ZonedDateTime signalTime = now.truncatedTo(ChronoUnit.HOURS);
    LOG.info("Czas rozpoczęcia nadania e-mail:" + signalTime.toString());
    Collection<Signal> signals = signalsReportRepository.getSingals(signalTime);
    LOG.info("Count:" + signals.size());
    Properties prop = new Properties();

    // Setup mail server
    prop.put("mail.smtp.host", mailConfig.host());
    prop.put("mail.smtp.port", mailConfig.port());
    prop.put("mail.smtp.ssl.enable", mailConfig.sslEnable());
    prop.put("mail.smtp.auth", mailConfig.auth());
    prop.put("mail.smtp.starttls.enable", mailConfig.starttlsEnable());
    prop.put("mail.smtp.ssl.trust", mailConfig.host());
    prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
  
    // SSL Factory
    prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
    Session session = Session.getInstance(prop, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mailConfig.username(), mailConfig.password());
      }
    });
    session.setDebug(true);
    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(mailConfig.username()));
      message.setRecipients(
          Message.RecipientType.TO, InternetAddress.parse(mailConfig.to()));
      message.setSubject("Ichimoku signals! " + signalTime.toString() );

      StringBuilder sb = new StringBuilder();
      sb.append("<h1>Welcome, here tradesignals.pl</h1>");
      if(signals.isEmpty()) {
        sb.append("<p>No signals this time</p>");
      } else {
        sb.append(String.format("<p>Next 4 hour (%s) candle is closed! I found signals for you:</p>", signalTime, ZoneId.systemDefault()));
       
        Collection<Signal> shouldEnter = signals.stream().filter(Signal::getShouldEnter).collect(Collectors.toUnmodifiableList());
        Collection<Signal> shouldExit = signals.stream().filter(Signal::getShouldExit).collect(Collectors.toUnmodifiableList());
        sb.append("<p>You can enter</p>");
        sb.append("<ol>");
        for(Signal s : shouldEnter) {
          sb.append(String.format("<li>Symbol: %s. Signal type: %s</li>", s.getSymbol(), s.getSignalType()));
        }
        sb.append("</ol>");
        sb.append("<p>You should exit</p>");
        sb.append("<ol>");
        for(Signal s : shouldExit) {
          sb.append(String.format("<li>Symbol: %s. Signal type: %s</li>", s.getSymbol(), s.getSignalType()));
        }
        sb.append("</ol>");
      }
      sb.append("Disclamer! Always rememeber! Service warunki - nie odpowiadam za podjete akcje");
      
 
      String msg = sb.toString();

      MimeBodyPart mimeBodyPart = new MimeBodyPart();
      mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(mimeBodyPart);

      message.setContent(multipart);

      Transport.send(message);
      LOG.info("Koniec wysyłania e-mail: " + ZonedDateTime.now().toString());
    } catch (Exception ex) {
      System.out.println(ex.getLocalizedMessage());
    }
  }

}
