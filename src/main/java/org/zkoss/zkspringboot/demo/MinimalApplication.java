package org.zkoss.zkspringboot.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootApplication
@Controller
public class MinimalApplication {
	public static void main(String[] args) {
		SpringApplication.run(MinimalApplication.class);
	}

	@GetMapping("/{page}")
	public String root(@PathVariable String page) {
		return page;
	}

	@GetMapping("/public/{page}")
	public String open(@PathVariable String page) {
		return "open/" + page;
	}

	@GetMapping("/secure/{page}")
	public String secure(@PathVariable String page) {
		return "secure/" + page;
	}
}
