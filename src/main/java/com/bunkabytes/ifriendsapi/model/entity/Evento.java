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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="Evento", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
	
	@Id
	@Column(name = "id_evento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;
	
	@Column(name = "nome_evento", length=150, nullable=false)
	private String nome;
	
	@Column(name = "local_evento", length=255, nullable=false)
	private String local;
	
	@Column(name = "dt_evento", nullable=false)
	private LocalDateTime dataEvento;
	
	@OneToMany(targetEntity = ImagemEvento.class, cascade = CascadeType.ALL, mappedBy = "evento")
	private List<ImagemResp> imagens;
	
	@CreationTimestamp
	@Column (name = "dt_publicacao")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataEmissao;
	
	@CreationTimestamp
	@Column (name = "dt_alt_evento")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataAlteracao;
	
	@Column(name = "desc_evento", nullable=false)
	private String descricao;
	
	@Column(name = "presencial", nullable=true)
	private Boolean presencial;
	
	@Column(name = "link_evento", nullable=true)
	private String link;
	
	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer qtdFavorito;
	
	@Transient
	private List<String> tags;

}
