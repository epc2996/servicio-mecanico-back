package com.example.backend.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.repository.ProblemaRepository;
import com.example.backend.repository.bean.Problema;

@Service
public class ProblemaDAO {

	@Autowired
	ProblemaRepository problemaRepository;
		
	/*registrar un problema*/
	public Problema registrarProblema(Problema problema) {
		return problemaRepository.save(problema);
	}
	
	/*buscar un problema por ID*/
	public Problema buscarProblema(int id) {
		return problemaRepository.getOne(id);
	}
	
	/*listar problemas*/
	public List<Problema> listarProblemas() {
		return problemaRepository.findAll();
	}
	
	
}
