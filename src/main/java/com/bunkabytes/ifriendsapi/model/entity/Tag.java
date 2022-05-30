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
@Table( name="Tag", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
	
	@Id
	@Column(name = "id_tag")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome_tag")
	private String nome; 
}
