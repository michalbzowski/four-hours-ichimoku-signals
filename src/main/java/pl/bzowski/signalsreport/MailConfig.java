package pl.bzowski.signalsreport;
import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "mail.smtp")
public interface MailConfig {

	@ConfigProperty(name = "host")
	String host();

	@ConfigProperty(name = "port")
	String port();
	
	@ConfigProperty(name = "ssl.enable")
	String sslEnable();

	@ConfigProperty(name = "auth")
	String auth();
  
	@ConfigProperty(name = "starttls.enable")
	String starttlsEnable();

  @ConfigProperty(name = "username")
	String username();

  @ConfigProperty(name = "password")
	String password();

  @ConfigProperty(name = "to")
  String to();

}
