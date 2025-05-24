package su.jut.onepiecedownloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OnePieceDownloaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnePieceDownloaderApplication.class, args);
	}
}
