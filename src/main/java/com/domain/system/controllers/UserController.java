package com.domain.system.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.domain.system.models.postgresql.Usuario;
import com.domain.system.services.interfaces.IUserService;

@RestController
@RequestMapping("apiv1")
@CrossOrigin(origins = "http://localhost:8081", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
		RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.TRACE }, allowedHeaders = "Authorization")
public class UserController {

	@Autowired
	private IUserService userService;

	@PreAuthorize("hasAnyRole('Administrador', 'Editor', 'Lector', 'USERS_Administrador', 'USERS_Editor', 'USERS_Lector')")
	@GetMapping("/users")
	public ResponseEntity<?> users() {
		Map<String, Object> responseBody = new HashMap<>();
		// MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
		List<Usuario> listaUsuarios;

		try {
			listaUsuarios = userService.findAll();
			if (listaUsuarios.size() > 0) {

				responseBody.put("mensaje", "Usuarios encontrados");
				responseBody.put("usuarios", listaUsuarios);

				// responseHeaders.add("Accept-Encoding", "compress;q=0.5");
				// responseHeaders.add("Accept-Encoding", "gzip;q=1.0");

				// return new ResponseEntity<Map<String, Object>>(responseBody, responseHeaders,
				// HttpStatus.OK);

				return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.OK);
			}
			responseBody.put("error", "Usuarios no encontrados");
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NO_CONTENT);
		} catch (DataAccessException e) {
			responseBody.put("mensaje", "Error al realizar la consulta en la base de datos");
			responseBody.put("error", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			responseBody.put("mensaje", "Error al realizar la consulta en la base de datos");
			responseBody.put("error", e.getMessage().concat(" : "));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {

		}
	}
}