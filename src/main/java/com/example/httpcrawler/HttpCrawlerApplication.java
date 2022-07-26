package com.example.httpcrawler;

import java.io.*;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.aop.TimedAspect;
import org.springframework.context.annotation.Bean;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.annotation.Timed;

@SpringBootApplication
@RestController
public class HttpCrawlerApplication {

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(HttpCrawlerApplication.class, args);
		URL url;
		String[] strArrayUrls = {"https://httpstat.us/200", "https://httpstat.us/503"};

		 try {
			while (true) {  
				//initialize an immutable list from array using asList method
				List<String> urllist = Arrays.asList(strArrayUrls);

				for(String urlVal : urllist){
					url = new URL(urlVal);
					System.out.print("Crawling URL : " + urlVal + "\n");	

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();

					connection.setReadTimeout(6000);
					connection.setDoOutput(true);
					connection.setRequestMethod("GET");
					connection.getResponseCode();

					System.out.println("HTTP Response code for " + url + " : " + connection.getResponseCode());

					connection.disconnect();
					Thread.sleep(1000);
				}
			}
		} catch(MalformedURLException e) {
			System.out.println("The url is not well formed: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("An I/O error occurs: " + e.getMessage());
		}
	}

	@Timed(value = "greeting.time", description = "Time taken to return greeting")
	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}
}
