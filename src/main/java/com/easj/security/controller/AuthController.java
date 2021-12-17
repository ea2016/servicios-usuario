package com.easj.security.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easj.dto.Mensaje;
import com.easj.security.dto.JwtDto;
import com.easj.security.dto.LoginUsuario;
import com.easj.security.dto.NuevoUsuario;
import com.easj.security.entity.Rol;
import com.easj.security.entity.Usuario;
import com.easj.security.enums.RolNombre;
import com.easj.security.jwt.JwtProvider;
import com.easj.security.service.RolService;
import com.easj.security.service.SendMailService;
import com.easj.security.service.UsuarioService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UsuarioService usuarioService;

	@Autowired
	RolService rolserivce;

	@Autowired
	JwtProvider jwtProvider;
	
	@Autowired
	private SendMailService sendMailService;
	
	@GetMapping("/lista")
	public ResponseEntity<List<Usuario>> list() {
		List<Usuario> list = usuarioService.list();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/login")
	public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
			return new ResponseEntity(new Mensaje("campos mal puesto"), HttpStatus.BAD_REQUEST);
		}		
		Usuario usuario=usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario()).get();
		if (usuario.getSesion()) {
			return new ResponseEntity(new Mensaje("este usuario ya se encuentra con una sesion activa"), HttpStatus.BAD_REQUEST);
		}
		if (!usuario.isUsuarioActivo()) {
			return new ResponseEntity(new Mensaje("este usuario ha sido desactivado"), HttpStatus.BAD_REQUEST);
		}
		if (!usuario.isPrimeraVez()) {
			return new ResponseEntity(new Mensaje("este usuario no ha aceptado los terminos y condiciones para dar de alta su cuenta"), HttpStatus.BAD_REQUEST);
		}
		
		usuario.setSesion(true);
		usuarioService.save(usuario);
		Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt= jwtProvider.generateToken(authentication);
		UserDetails userDetails= (UserDetails) authentication.getPrincipal();
		JwtDto jwtDto=new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());
		return new ResponseEntity(jwtDto,HttpStatus.OK);
		
		
	} 
	
	@GetMapping("/resetPasswod")
	public ResponseEntity<JwtDto> resetPasswod(@Valid @RequestBody String correo, BindingResult bindingResult){
		
		if (bindingResult.hasErrors())
			return new ResponseEntity(new Mensaje("campos mal puesto o email invalido"), HttpStatus.BAD_REQUEST);
		
		sendMailService.sendMail (correo);
		return new ResponseEntity(new Mensaje("clave enviada"),HttpStatus.CREATED);
	}
	
	@PostMapping("/forgotPasswod")
	public ResponseEntity<JwtDto> forgotPasswod(@Valid @RequestBody String correo, BindingResult bindingResult){
		
		if (bindingResult.hasErrors())
			return new ResponseEntity(new Mensaje("campos mal puesto o email invalido"), HttpStatus.BAD_REQUEST);
		
		sendMailService.sendMail (correo);
		return new ResponseEntity(new Mensaje("clave enviada"),HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/nuevo")
	public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return new ResponseEntity(new Mensaje("campos mal puesto o email invalido"), HttpStatus.BAD_REQUEST);

		if (usuarioService.existeByNombreUsuario(nuevoUsuario.getNombreUsuario()))
			return new ResponseEntity(new Mensaje("ese nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);

		if (usuarioService.existeByEmail(nuevoUsuario.getEmail()))
			return new ResponseEntity(new Mensaje("ese email ya existe"), HttpStatus.BAD_REQUEST);

		Usuario usuario = new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(),
				nuevoUsuario.getEmail(), passwordEncoder.encode(nuevoUsuario.getPassword()));

		Set<Rol> roles = new HashSet<>();
		roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_USER).get());

		if (nuevoUsuario.getRoles().contains("admin"))
			roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_ADMIN).get());

		usuario.setRoles(roles);
		usuarioService.save(usuario);
		return new ResponseEntity(new Mensaje("usuario guardado"),HttpStatus.CREATED);
	}
	
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody Usuario nuevoUsuario) {
		
		if (!usuarioService.existsById(id))
			return new ResponseEntity(new Mensaje("no existe el usuario"), HttpStatus.NOT_FOUND);
		
		if (usuarioService.existeByNombreUsuario(nuevoUsuario.getNombre()) && usuarioService.getByNombreUsuario(nuevoUsuario.getNombre()).get().getId()!=id)
			return new ResponseEntity(new Mensaje("ese nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);

		if (usuarioService.existeByEmail(nuevoUsuario.getEmail()))
			return new ResponseEntity(new Mensaje("ese email ya existe"), HttpStatus.BAD_REQUEST);

		Usuario usuario = new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(),
				nuevoUsuario.getEmail(), passwordEncoder.encode(nuevoUsuario.getPassword()));

		/*
		 * Set<Rol> roles = new HashSet<>();
		 * roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_USER).get());
		 * 
		 * if (nuevoUsuario.getRoles().contains("admin"))
		 * roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_ADMIN).get());
		 * 
		 * usuario.setRoles(roles);
		 */
		usuarioService.save(usuario);
		return new ResponseEntity(new Mensaje("usuario modificado"),HttpStatus.CREATED);
	}
	
	
}
