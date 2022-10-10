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

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@RestController
public class HttpCrawlerApplication {

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

	
	public static void main(String[] args) throws Exception {
		
		SpringApplication.run(HttpCrawlerApplication.class, args);
		
	} 

	@Timed(value = "greeting.time", description = "Time taken to return greeting")
	@GetMapping("/crawl")
	@ResponseBody
	public String index(@RequestParam String requestUrl) {
		int requestUrlResponseCode;
		try {
			URL url;
			url = new URL("https://" + requestUrl);
			System.out.print("Crawling URL : " + requestUrl + "\n");	

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setReadTimeout(6000);
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			requestUrlResponseCode = connection.getResponseCode();

			System.out.println("HTTP Response code for " + url + " : " + requestUrlResponseCode);

			connection.disconnect();
			
		} catch(MalformedURLException e) {
			System.out.println("The url is not well formed: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("An I/O error occurs: " + e.getMessage());
		}
		return "Greetings from Spring Boot Http Crawler! " + requestUrl;
	}
}
