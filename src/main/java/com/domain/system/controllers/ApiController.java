package com.domain.system.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("apiv1")
@CrossOrigin(origins = "http://localhost:8081", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.TRACE}, allowedHeaders = "Authorization")
public class ApiController {
	
	@GetMapping
    public String index() {
        return "Bienvenido a la API de SystemApp";
    }
	
	@GetMapping("/demo")
    public String demo() {
        return "Bienvenido al demo de la API SystemApp";
    }

}