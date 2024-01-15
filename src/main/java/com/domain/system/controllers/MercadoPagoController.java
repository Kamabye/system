package com.domain.system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.domain.system.services.MePaService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

@RestController
@RequestMapping({ "apiv1/mercadopago", "apiv1/mercadopago/" })
@CrossOrigin(origins = { "http://localhost:8081", "http://localhost:4200" }, methods = { RequestMethod.GET,
		RequestMethod.POST, RequestMethod.PUT,
		RequestMethod.DELETE }, allowedHeaders = { "Authorization", "Content-Type" }, exposedHeaders = {})
public class MercadoPagoController {

	@Autowired
	private MePaService mePagoService;

	@GetMapping("")
	public String createPayment() {

		try {
			Payment payment = mePagoService.createPayment();
			return payment.getCallbackUrl();
		} catch (MPApiException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return "Mercado Pago";
		} catch (MPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Mercado Pago";
		}

	}

}
