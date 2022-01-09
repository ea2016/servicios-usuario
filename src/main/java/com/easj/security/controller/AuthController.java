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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easj.dto.Mensaje;
import com.easj.security.dto.ActualizarUsuario;
import com.easj.security.dto.JwtDto;
import com.easj.security.dto.LoginUsuario;
import com.easj.security.dto.NuevoUsuario;
import com.easj.security.dto.RecuperarClave;
import com.easj.security.entity.Rol;
import com.easj.security.entity.Usuario;
import com.easj.security.enums.RolNombre;
import com.easj.security.jwt.JwtProvider;
import com.easj.security.service.RolService;
import com.easj.security.service.SendMailService;
import com.easj.security.service.UsuarioRolService;
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
	UsuarioRolService usuarioRolserivce;

	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	private SendMailService sendMailService;

	/********************************************************************************************************************************/
	@GetMapping("/lista")
	public ResponseEntity<List<Usuario>> list() {
		List<Usuario> list = usuarioService.list();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getUsers/{username}")
	public ResponseEntity<Usuario> getUsuario(@PathVariable(value = "username") String username) {

		Usuario usuario = usuarioService.getByNombreUsuario(username).get();

		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/login")
	public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity(new Mensaje("campos mal puesto"), HttpStatus.BAD_REQUEST);
		}
		Usuario usuario = usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario()).get();
		if (usuario.getSesion()) {
			return new ResponseEntity(usuario, HttpStatus.BAD_REQUEST);
		}

		if (!usuario.isUsuarioActivo() && !usuario.getPrimeraVez()) {
			return new ResponseEntity(usuario, HttpStatus.BAD_REQUEST);
		}

		if (!usuario.isUsuarioActivo() && usuario.getPrimeraVez()) {
			return new ResponseEntity(usuario, HttpStatus.BAD_REQUEST);
		}

		usuario.setSesion(true);
		usuarioService.save(usuario);
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());
		return new ResponseEntity(jwtDto, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/logout")
	public ResponseEntity<JwtDto> logout(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity(new Mensaje("campos mal puesto"), HttpStatus.BAD_REQUEST);
		}
		Usuario usuario = usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario()).get();

		usuario.setSesion(false);
		usuarioService.save(usuario);
		return new ResponseEntity("fin session " + loginUsuario.getNombreUsuario(), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/getCodigoUsuario")
	public ResponseEntity<Usuario> getCodigoPassword(@Valid @RequestBody RecuperarClave rc,
			BindingResult bindingResult) {

		Usuario usuario = usuarioService.getByNombreUsuario(rc.getNombreUsuario()).get();

		if (usuario.getCodigoReinicio().equals(rc.getCodigoValidacion())) {
			return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
		} else {
			return new ResponseEntity(new Mensaje("La clave introducida no es válida"), HttpStatus.BAD_REQUEST);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/activarUsuario")
	public ResponseEntity<Usuario> activarUsuario(@Valid @RequestBody RecuperarClave usu, BindingResult bindingResult) {

		Usuario usuario = usuarioService.getByNombreUsuario(usu.getNombreUsuario()).get();
		if (usuario.getCodigoReinicio().equals(usu.getCodigoValidacion())) {
			usuario.setUsuarioActivo(true);
			usuario.setPrimeraVez(true);
			usuario.setCodigoReinicio(null);
			usuarioService.save(usuario);
		} else {
			return new ResponseEntity(new Mensaje("El código introducida no es válida"), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity("usuario Activado" + usuario.getNombreUsuario(), HttpStatus.OK);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/forgotPassword")
	public ResponseEntity<JwtDto> forgotPassword(@Valid @RequestBody RecuperarClave nombreUsuario,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return new ResponseEntity(new Mensaje("campos mal puesto o email invalido"), HttpStatus.BAD_REQUEST);

		Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario.getNombreUsuario()).get();
		usuario.setCodigoReinicio(sendMailService.sendMail(nombreUsuario.getNombreUsuario(), false));
		usuarioService.save(usuario);
		return new ResponseEntity(new Mensaje("clave enviada"), HttpStatus.CREATED);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/nuevo")
	public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return new ResponseEntity(new Mensaje("campos mal puesto"), HttpStatus.BAD_REQUEST);

		if (usuarioService.existeByNombreUsuario(nuevoUsuario.getNombreUsuario()))
			return new ResponseEntity(new Mensaje("ese nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);

		Usuario usuario = new Usuario(nuevoUsuario.getNombreUsuario(), nuevoUsuario.getNombre(),
				nuevoUsuario.getTelefono(), nuevoUsuario.getDireccion(), nuevoUsuario.getMunicipio(),
				nuevoUsuario.getProvincia(), nuevoUsuario.getCodigo_postal(), nuevoUsuario.getRif(),
				passwordEncoder.encode(nuevoUsuario.getPassword()));

		Set<Rol> roles = new HashSet<>();

		if (nuevoUsuario.getRoles().contains("master")) {
			roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_ADMIN).get());
			roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_PARN).get());
		}
		if (nuevoUsuario.getRoles().contains("admin")) {
			roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_PARN).get());
		}
		if (nuevoUsuario.getRoles().contains("junta")) {
			roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_JUNTA).get());
		}
		if (nuevoUsuario.getRoles().contains("usuario")) {
			roles.add(rolserivce.getByRolNombre(RolNombre.ROLE_USER).get());
		}
		String codigoActivacion = sendMailService.sendMail(nuevoUsuario.getNombreUsuario(), true);
		usuario.setCodigoReinicio(codigoActivacion);
		usuario.setRoles(roles);
		usuarioService.save(usuario);
		return new ResponseEntity(new Mensaje("usuario guardado"), HttpStatus.CREATED);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/actualizar")
	public ResponseEntity<?> update(@RequestBody ActualizarUsuario usuarioActualizar) {

		if (!usuarioService.existsById(usuarioActualizar.getIdUsuario()))
			return new ResponseEntity(new Mensaje("no existe el usuario"), HttpStatus.NOT_FOUND);

		if (usuarioService.existeByNombreUsuario(usuarioActualizar.getNombreUsuario()) && usuarioService
				.getByNombreUsuario(usuarioActualizar.getNombre()).get().getId() != usuarioActualizar.getIdUsuario())
			return new ResponseEntity(new Mensaje("ese nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);

		Usuario usuario = new Usuario(usuarioActualizar.getNombreUsuario(), usuarioActualizar.getNombre(),
				usuarioActualizar.getTelefono(), usuarioActualizar.getDireccion(), usuarioActualizar.getMunicipio(),
				usuarioActualizar.getProvincia(), usuarioActualizar.getCodigo_postal(), usuarioActualizar.getRif(),
				passwordEncoder.encode(usuarioActualizar.getPassword()));

		usuarioService.save(usuario);
		return new ResponseEntity(new Mensaje("usuario actualizado"), HttpStatus.CREATED);
	}

	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" })
	 * 
	 * @PreAuthorize("hasRole('ADMIN')")
	 * 
	 * @PostMapping("/eliminar") public ResponseEntity<?> eliminar(@RequestBody
	 * Usuario usuarioEliminar) {
	 * 
	 * if (!usuarioService.existsById(usuarioEliminar.getId())) { return new
	 * ResponseEntity(new Mensaje("no existe el usuario"), HttpStatus.NOT_FOUND); }
	 * 
	 * List<UsuarioRol> rolUsuarioRepository =
	 * usuarioRolserivce.findByUsuarioId(usuarioEliminar.getId());
	 * 
	 * return new ResponseEntity(new Mensaje("usuario eliminado"),
	 * HttpStatus.CREATED); }
	 */

}
