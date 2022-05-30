package com.bunkabytes.ifriendsapi.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@Column(name = "nome_usuario")
	private String nome;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "senha")
	@JsonIgnore
	private String senha;
	
	@Column(name = "link_img_usuario")
	private String imagem;
	
	@Column(name = "curso")
	private String curso;
	
	@Column(name = "ano")
	private Integer ano;
	
	@Column(name = "reputacao_total")
	private Integer reputacao;
}
