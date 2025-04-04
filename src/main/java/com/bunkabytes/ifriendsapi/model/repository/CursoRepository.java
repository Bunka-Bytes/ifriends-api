package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Curso;

public interface CursoRepository extends JpaRepository<Curso, String>{

}
