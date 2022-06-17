package com.bunkabytes.ifriendsapi.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="Pergunta", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pergunta {
	
	@Id
	@Column(name = "id_pergunta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;
	
	@Column(name = "titulo_perg")
	private String titulo;
	
	@Column(name = "texto_perg")
	private String texto;
	
	@Column(name = "respondida")
	private boolean respondida;
	
	@Column(name = "visualizacao")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer visualizacao;
	
	@Column(name = "deletado")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private boolean deletado;
	
	@CreationTimestamp
	@Column (name = "dt_perg")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataEmissao;
	
	@UpdateTimestamp
	@Column(name = "dt_alt_perg")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataAlteracao;
	
	@Transient
	private List<String> tags;
	
	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long qtdCurtida;
	
	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long qtdResposta;

}
