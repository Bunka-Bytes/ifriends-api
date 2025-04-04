package com.bunkabytes.ifriendsapi.model.entity;


import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table( name="Resposta", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resposta {
	@Id
	@Column(name = "id_resposta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_pergunta")
	private Pergunta pergunta;
	  
	@Column(name = "texto_resp", length=25000, nullable=false)
	private String texto;

	@Column(name = "aceita")
	private Boolean aceita;
	
	@OneToMany(targetEntity = ImagemResp.class, cascade = CascadeType.ALL, mappedBy = "resposta")
	private List<ImagemResp> imagens;
	
	@Column(name = "deletado")
	private boolean deletado;
	
	@CreationTimestamp
	@Column (name = "dt_emis_resp")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataEmissao;
	
	@UpdateTimestamp
	@Column(name = "dt_alt_resp")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataAlteracao;
	
	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long qtdCurtida;
}
