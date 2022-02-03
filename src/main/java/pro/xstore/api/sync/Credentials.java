package pro.xstore.api.sync;
import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "xtb.credentials")
public interface Credentials {

	@ConfigProperty(name = "login")
	String login();

	@ConfigProperty(name = "password")
	String password();
	
	@ConfigProperty(name = "appId")
	String appId();

	@ConfigProperty(name = "appName")
	String appName();
}