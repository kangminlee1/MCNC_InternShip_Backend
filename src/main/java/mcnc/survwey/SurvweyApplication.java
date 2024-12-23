package mcnc.survwey;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SurvweyApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("MAIL_USER_NAME", dotenv.get("MAIL_USER_NAME"));
		System.setProperty("MAIL_USER_PASSWORD", dotenv.get("MAIL_USER_PASSWORD"));
		System.setProperty("INVITATION_URL", dotenv.get("INVITATION_URL"));
		System.setProperty("ENCRYPTION_SECRET_KEY", dotenv.get("ENCRYPTION_SECRET_KEY"));
		System.setProperty("NOTIFICATION_URL", dotenv.get("NOTIFICATION_URL"));
		System.setProperty("ALLOWED_URL", dotenv.get("ALLOWED_URL"));
		SpringApplication.run(SurvweyApplication.class, args);

	}

}
