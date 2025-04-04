package com.bunkabytes.ifriendsapi.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="Tag_evento", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagEvento {
	
	@Id
	@Column(name = "id_tag_evento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_tag")
	private Tag tag;
	
	@ManyToOne
	@JoinColumn(name = "id_evento")
	private Evento evento;

}
