package com.easj.security.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@NotNull
	@Column(unique = true)
	private String nombreUsuario;
	@NotNull
	private String password;
	
	private boolean sesion;
	private boolean usuarioActivo;
	private boolean primeraVez;
	private String codigoReinicio;
	private String nombre;
	private String telefono;
	private String direccion;
	private String municipio;
	private String provincia;
	private String codigo_postal;
	private String rif;
	
	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
	private Set<Rol> roles = new HashSet<>();

	public Usuario() {
	}

	public Usuario(@NotNull String nombreUsuario, @NotNull String nombre,
			@NotNull String telefono,@NotNull String direccion,@NotNull String municipio,@NotNull String provincia,
			@NotNull String codigo_postal,@NotNull String rif, @NotNull String password) {
		this.nombreUsuario = nombreUsuario;
		this.nombre = nombre;
		this.telefono = telefono;
		this.direccion = direccion;
		this.municipio = municipio;
		this.provincia = provincia;
		this.codigo_postal = codigo_postal;
		this.rif = rif;
		this.password = password;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}

	public boolean getSesion() {
		return sesion;
	}

	public void setSesion(boolean sesion) {
		this.sesion = sesion;
	}

	public boolean isUsuarioActivo() {
		return usuarioActivo;
	}

	public void setUsuarioActivo(boolean usuarioActivo) {
		this.usuarioActivo = usuarioActivo;
	}

	public boolean getPrimeraVez() {
		return primeraVez;
	}

	public void setPrimeraVez(boolean primeraVez) {
		this.primeraVez = primeraVez;
	}

	public String getCodigoReinicio() {
		return codigoReinicio;
	}

	public void setCodigoReinicio(String codigoReinicio) {
		this.codigoReinicio = codigoReinicio;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCodigo_postal() {
		return codigo_postal;
	}

	public void setCodigo_postal(String codigo_postal) {
		this.codigo_postal = codigo_postal;
	}

	public String getRif() {
		return rif;
	}

	public void setRif(String rif) {
		this.rif = rif;
	}
	
	
}
