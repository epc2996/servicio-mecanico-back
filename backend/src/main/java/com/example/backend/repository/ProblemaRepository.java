package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.repository.bean.Problema;

@Repository
public interface ProblemaRepository extends JpaRepository<Problema, Integer> {

	
}
