package com.bunkabytes.ifriendsapi.model.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="Usuario", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	@Id
	@Column(name = "id_usuario")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome_usuario", nullable=false, length=150)
	private String nome;
	
	@Column(name = "apelido_usuario", nullable=false, length=50)
	private String apelido;
	
	@Column(name = "bio_usuario", nullable=true, length=255)
	private String bio;
	
	@Column(name = "email", nullable=false, length=100)
	private String email;
	
	@Column(name = "senha", nullable=false, length=255)
	@JsonIgnore
	private String senha;
	
	@Column(name = "link_img_usuario", nullable=true, length=255)
	private String imagem;
	
	@CreationTimestamp
	@Column (name = "dt_emis_usuario")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataEmissao;
	
	@UpdateTimestamp
	@Column(name = "dt_alt_usuario")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataAlteracao;
	
	@ManyToOne
	@JoinColumn(name = "sigla_curso", nullable=true)
	private Curso curso;
	
	@Column(name = "ano", nullable=true)
	private Integer ano;
	
	@Column(name = "reputacao_total", nullable=true)
	private Integer reputacao;
	
	@Column(name = "administrador", nullable=true)
	@JsonIgnore
	private boolean admin;
	
	@Column(name = "banido", nullable=true)
	@JsonIgnore
	private boolean banido;
	
	@Column(name = "codigo_verificador", nullable=true)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String codVerificador;
	
}
