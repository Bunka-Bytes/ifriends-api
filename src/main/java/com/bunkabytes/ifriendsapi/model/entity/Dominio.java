package com.bunkabytes.ifriendsapi.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="dominio_institucional", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dominio {
	
	@Id
	@Column(name = "id_dominio_institucional")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "dominio")
	private String dominio;
}
