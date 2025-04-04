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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="Visualizacao", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visualizacao {
	
	@Id
	@Column(name = "id_visualizacao")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_pergunta")
	private Pergunta pergunta;
	
	@CreationTimestamp
	@Column (name = "dt_visualizacao")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime dataVisualizacao;

}
