package com.herokuapp.kamabye.controllers;

import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.herokuapp.kamabye.interfaces.IUserService;
import com.herokuapp.kamabye.models.postgresql.Usuario;

@RestController
@RequestMapping({ "apiv1/user", "apiv1/user/" })
@CrossOrigin(origins = { "http://localhost:8081", "http://localhost:4200" }, methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.TRACE, RequestMethod.OPTIONS }, allowedHeaders = { "Authorization", "Content-Type" }, exposedHeaders = {})
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("initialized")
	public ResponseEntity<?> initializedUser(@RequestBody Usuario usuario) {
		
		Map<String, Object> responseBody = new HashMap<>();
		
		try {
			System.out.println(usuario.toString());
			if (usuario.getUsername().equals("ADMINISTRADOR") && usuario.getEmail().equals("admin@domain.com") && usuario.getPassword().equals("12345")) {
				Usuario userSave = userService.save(usuario);
				
				return new ResponseEntity<Usuario>(userSave, null, HttpStatus.OK);
				
			}
			responseBody.put("mensaje",
			  "No se pudo inizializar el usuario ADMINISTRADOR por claves incorrectas.");
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
			
		} catch (DataIntegrityViolationException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "El Objeto: ".concat(usuario.getEmail().concat(" ya existe en la base de datos")));
			responseBody.put("error", "DataIntegrityViolationException: "
			  .concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (DataAccessException e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error",
			  "DataAccessException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "Exception: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			
		}
	}
	
	@PreAuthorize("hasAnyRole('Administrador','USERS_Administrador')")
	@GetMapping("")
	
	public ResponseEntity<?> users(@RequestParam(defaultValue = "0") int pageNumber,
	  @RequestParam(defaultValue = "2") int pageSize) {
		Map<String, Object> responseBody = new HashMap<>();
		// List<Usuario> listaUsuarios;
		Page<Usuario> pageListaUsuarios;
		
		try {
			
			// listaUsuarios = userService.findAll();
			pageListaUsuarios = userService.findAll(pageNumber, pageSize);
			
			// if (!listaUsuarios.isEmpty()) {
			if (!pageListaUsuarios.isEmpty()) {
				
				// return new ResponseEntity<List<Usuario>>(listaUsuarios, null, HttpStatus.OK);
				return new ResponseEntity<Page<Usuario>>(pageListaUsuarios, null, HttpStatus.OK);
				
			}
			
			responseBody.put("mensaje", "No se encontraron resultados");
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
			
		} catch (EmptyResultDataAccessException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "No se encontraron resultados.");
			responseBody.put("error", "EmptyResultDataAccessException: "
			  .concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
		} catch (DataAccessException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error",
			  "DataAccessException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "Exception: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			
		}
	}
	
	@PreAuthorize("hasAnyRole('Administrador', 'Editor', 'Lector','USERS_Administrador', 'USERS_Editor', 'USERS_Lector')")
	@GetMapping("{idUsuario}")
	public ResponseEntity<?> findUsuario(@PathVariable(name = "idUsuario", required = false) String idUsuarioString) {
		Map<String, Object> responseBody = new HashMap<>();
		
		try {
			
			if (idUsuarioString != null) {
				
				Long idUsuario = Long.valueOf(idUsuarioString);
				
				Usuario usuario = userService.findByIdUsuario(idUsuario);
				
				if (usuario != null) {
					return new ResponseEntity<Usuario>(usuario, null, HttpStatus.OK);
				}
				
				responseBody.put("mensaje",
				  "El ID: ".concat(idUsuarioString.toString().concat(" no existe en la base de datos!.")));
				return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
				
			}
			responseBody.put("mensaje", "Parámetros nulos");
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NO_CONTENT);
			
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ingrese un ID válido");
			responseBody.put("error", "NumberFormatException: ".concat(e.getMessage().concat(" : ").concat(e.getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.BAD_REQUEST);
		} catch (DataAccessException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error",
			  "DataAccessException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "Exception: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			
		}
	}
	
	@PreAuthorize("hasAnyRole('Administrador', 'Editor', 'Lector','USERS_Administrador', 'USERS_Editor', 'USERS_Lector')")
	@PostMapping("")
	public ResponseEntity<?> saveUser(@RequestBody Usuario usuario) {
		
		Map<String, Object> responseBody = new HashMap<>();
		
		try {
			
			Usuario userSave = userService.save(usuario);
			
			if (userSave != null) {
				
				return new ResponseEntity<Usuario>(userSave, null, HttpStatus.OK);
				
			}
			responseBody.put("mensaje",
			  "El Objeto :".concat(usuario.toString().concat(" no se pudo guardar en la base de datos!.")));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
			
		} catch (
		
		MultipartException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Error el multipartFile");
			responseBody.put("error",
			  "MultipartException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ingrese un ID válido");
			responseBody.put("error", "NumberFormatException: ".concat(e.getMessage().concat(" : ").concat(e.getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.BAD_REQUEST);
		} catch (DataIntegrityViolationException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "El Objeto: ".concat(usuario.getEmail().concat(" ya existe en la base de datos")));
			responseBody.put("error", "DataIntegrityViolationException: "
			  .concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (DataAccessException e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error",
			  "DataAccessException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "Exception: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			
		}
	}
	
	@PreAuthorize("hasAnyRole('Administrador', 'Editor', 'Lector','USERS_Administrador', 'USERS_Editor', 'USERS_Lector')")
	@PutMapping("")
	public ResponseEntity<?> updateUser(@RequestBody Usuario usuario) {
		Map<String, Object> responseBody = new HashMap<>();
		
		try {
			
			Long countUsuario = userService.countByIdUsuario(usuario.getIdUsuario());
			
			if (countUsuario > 0) {
				
				Usuario userUpdate = userService.update(usuario);
				
				if (userUpdate != null) {
					
					return new ResponseEntity<Usuario>(userUpdate, null, HttpStatus.OK);
					
				}
				responseBody.put("mensaje",
				  "El ID: ".concat(usuario.toString().concat(" no se pudo actualizar en la base de datos!.")));
				return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
				
			}
			
			responseBody.put("mensaje", "El ID: ".concat(usuario.toString().concat(" no existe en la base de datos!.")));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
			
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ingrese un ID válido");
			responseBody.put("error", "NumberFormatException: ".concat(e.getMessage().concat(" : ").concat(e.getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.BAD_REQUEST);
		} catch (DataAccessException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error",
			  "DataAccessException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "Exception: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			
		}
	}
	
	@PreAuthorize("hasAnyRole('Administrador', 'Editor', 'Lector','USERS_Administrador', 'USERS_Editor', 'USERS_Lector')")
	@PutMapping("{idUsuario}")
	public ResponseEntity<?> uploadImage(
	  @PathVariable(name = "idUsuario", required = true) String idUsuarioString,
	  @RequestPart(name = "imagen", required = false) MultipartFile imagen) {
		Map<String, Object> responseBody = new HashMap<>();
		
		try {
			
			if (idUsuarioString != null) {
				
				Long idUsuario = Long.valueOf(idUsuarioString);
				
				Long countUsuario = userService.countByIdUsuario(idUsuario);
				
				if (countUsuario > 0) {
					
					if (imagen.getBytes() != null) {
						// usuario.setImagen(imagen.getBytes());
						
						Blob blob = new SerialBlob(imagen.getBytes());
						// usuario.setImagen(imagen.getBytes());
						Usuario userUpdate = userService.uploadImage(idUsuario, blob);
						
						if (userUpdate != null) {
							return new ResponseEntity<Usuario>(userUpdate, null, HttpStatus.OK);
							
						}
						
						responseBody.put("mensaje",
						  "El ID: ".concat(idUsuarioString.concat(" no se pudo actualizar en la base de datos!.")));
						return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					responseBody.put("mensaje", "Datos inválidos");
					return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.BAD_REQUEST);
					
				}
				
				responseBody.put("mensaje", "El ID: ".concat(idUsuarioString.concat(" no existe en la base de datos!.")));
				return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
				
			}
			responseBody.put("mensaje", "Datos inválidos");
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.BAD_REQUEST);
			
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ingrese un ID válido");
			responseBody.put("error", "NumberFormatException: ".concat(e.getMessage().concat(" : ").concat(e.getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.BAD_REQUEST);
		} catch (DataAccessException e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error",
			  "DataAccessException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (HttpMessageNotWritableException e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "HttpMessageNotWritableException: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "Exception: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		finally {
			
		}
	}
	
	@PreAuthorize("hasAnyRole('Administrador','USERS_Administrador')")
	@DeleteMapping("{idUsuario}")
	public ResponseEntity<?> deleteUsuarioByPath(
	  @PathVariable(name = "idUsuario", required = true) String idUsuarioString) {
		Map<String, Object> responseBody = new HashMap<>();
		try {
			Long idUsuario = Long.valueOf(idUsuarioString);
			
			Usuario userDeleted = userService.deleteReturn(idUsuario);
			
			if (userDeleted != null) {
				return new ResponseEntity<Usuario>(userDeleted, null, HttpStatus.OK);
			}
			
			responseBody.put("mensaje", "El ID: " + idUsuario + " no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.NOT_FOUND);
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ingrese un ID válido");
			responseBody.put("error", "NumberFormatException: ".concat(e.getMessage().concat(" : ").concat(e.getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.BAD_REQUEST);
		} catch (EmptyResultDataAccessException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "El ID : " + idUsuarioString + " no existe en la base de datos");
			responseBody.put("error", "EmptyResultDataAccessException: "
			  .concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (DataAccessException e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error",
			  "DataAccessException: ".concat(e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			// e.printStackTrace();
			responseBody.put("mensaje", "Ha ocurrido un error.");
			responseBody.put("error", "Exception: ".concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(responseBody, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			
		}
		
	}
	
	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<String> handleAccessDeniedException() {
		return new ResponseEntity<>("Acceso Denegado: El usuario no tiene los permisos para este recurso", HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
 public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, WebRequest request) {
     String message = "Content-Type not supported: " + ex.getContentType();
     return new ResponseEntity<>(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
 }
	
}