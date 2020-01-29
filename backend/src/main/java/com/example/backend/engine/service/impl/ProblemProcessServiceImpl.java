package com.example.backend.engine.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dao.ProblemaDAO;
import com.example.backend.dao.UsuarioDAO;
import com.example.backend.engine.context.ContextHolder;
import com.example.backend.engine.context.UserContext;
import com.example.backend.engine.service.ProblemProcessService;
import com.example.backend.engine.service.ValidationService;
import com.example.backend.exceptions.BackEndException;
import com.example.backend.repository.bean.Problema;
import com.example.backend.repository.bean.Usuario;
import com.example.backend.repository.enums.AccionProblemaEnum;
import com.example.backend.rest.type.base.ResponseStatusBase;
import com.example.backend.rest.type.business.ListProblemaRest;
import com.example.backend.rest.type.business.ProblemaRest;
import com.example.backend.rest.type.request.GestionarProblemaRequest;
import com.example.backend.rest.type.response.GestionarProblemaResponse;
import com.example.backend.rest.type.response.ListarProblemasResponse;

@Service
public class ProblemProcessServiceImpl implements ProblemProcessService{

	private static final Logger LOGGER = LogManager.getLogger(ProblemProcessServiceImpl.class);
	
	@Autowired
	ValidationService validationService;
	
	@Autowired
	ProblemaDAO problemaDAO;
	
	@Autowired
	UsuarioDAO usuarioDAO;
	
	@Override
	public void registrar() {
		// TODO Auto-generated method stub
		final String methodName = "registrar";
		LOGGER.traceEntry(methodName);
		
		//validationService.validateRegistroParams();
		
		UserContext context = ContextHolder.get(UserContext.class);
		GestionarProblemaRequest request= context.getRegistrarProblemaRequest();
		GestionarProblemaResponse response= context.getRegistrarProblemaResponse();
		int idConductor = 0;
		String estado = "";
		
		//Validar acción
		AccionProblemaEnum accion = AccionProblemaEnum.getByCode(request.getProblema().getAccion());
		
		if (accion == null) {
			LOGGER.info("La acción no es válida");
			throw new BackEndException("La acción no es válida");
		}

		
		if (request.getProblema().getAccion().equals("CREAR")) {
			
			//Recuperar conductor
			Usuario conductor = usuarioDAO.recuperarUsuarioPorDNI(request.getProblema().getConductor());
			
			if (conductor == null) {
				LOGGER.info("El conductor no existe");
				throw new BackEndException("El conductor no existe");
			}
			
			idConductor = conductor.getId();
			estado = "CREADO";
		}else if (request.getProblema().getAccion().equals("ASIGNAR")) {
			estado = "EN PROCESO";
		}else if (request.getProblema().getAccion().equals("ATENDIDO")) {
			estado = "ATENDIDO";
		}else {
			estado = "RECHAZADO";
		}
		
	
		Problema problema = gestionProblema(request, idConductor, estado);
		
		if (problema!=null) {
			
			response.setStatus(new ResponseStatusBase());
			response.getStatus().setSuccess(Boolean.TRUE);
			response.getStatus().setMessage("Registro OK");
			
			response.setProblema(new ProblemaRest());
			response.getProblema().setCodigoProblema(problema.getIdProblema());
			response.getProblema().setConductor(request.getProblema().getConductor());
			response.getProblema().setMecanico(request.getProblema().getMecanico());
			response.getProblema().setDetalles(problema.getDetalles());
			response.getProblema().setFoto(problema.getFoto());
			response.getProblema().setAccion(request.getProblema().getAccion());
			response.getProblema().setLatitud(problema.getLatitud());
			response.getProblema().setLongitud(problema.getLongitud());
			
		}else {
			throw new BackEndException("No se pudo registrar al usuario");
		}
		
		LOGGER.traceExit(methodName);
	}


	@Override
	public void listar() {
		// TODO Auto-generated method stub
		final String methodName = "listar";
		LOGGER.traceEntry(methodName);
		
		//validationService.validateRegistroParams();
		
		UserContext context = ContextHolder.get(UserContext.class);
		ListarProblemasResponse listarProblemasResponse = context.getListarProblemasResponse();
		
	    List<Problema> listProblem = problemaDAO.listarProblemas();
		
	    if (listProblem.size() == 0 ) {
	    	LOGGER.info("No hay problemas que necesiten atención");
			throw new BackEndException("No hay problemas que necesiten atención");
	    }
	    
	    List<ListProblemaRest> listaProblema = new ArrayList<>();
	    ListProblemaRest listProblemaRest = new ListProblemaRest();
	    
		for (int i = 0; i < listProblem.size(); i++) {
			listProblemaRest = new ListProblemaRest();
	    	if (listProblem.get(i).getEstado().equals("CREADO")) {
	    		Usuario searchUsuario = usuarioDAO.recuperarUsuarioPorID(listProblem.get(i).getIdUsuarioEmi());
	    		
	    		listProblemaRest.setCodigoProblema(listProblem.get(i).getIdProblema());
	    		listProblemaRest.setDetalleProblema(listProblem.get(i).getDetalles());
	    		listProblemaRest.setNombreConductor(searchUsuario.getNombres()+ " " +searchUsuario.getApellidos());
	    		listProblemaRest.setFoto(listProblem.get(i).getFoto());
	    		listProblemaRest.setFechaRegistro(listProblem.get(i).getFechaRegistro());
	    		
	    		listaProblema.add(listProblemaRest);
	    	} 	
	    	
		}
		
		listarProblemasResponse.setListaProblema(listaProblema);
		LOGGER.traceExit(methodName);
	}
	
	private Problema gestionProblema (GestionarProblemaRequest request, int idConductor, String estado) {
		
		Problema problemaRegistro = new Problema();
		
		if (!request.getProblema().getAccion().equals("CREAR")) {
			Problema searchProblema = problemaDAO.buscarProblema(request.getProblema().getCodigoProblema());
			
			if (searchProblema == null) {
				LOGGER.info("El problema no se encuentra registrado");
				throw new BackEndException("El problema no se encuentra registrado");
			}
		
			Usuario mecanico = usuarioDAO.recuperarUsuarioPorDNI(request.getProblema().getMecanico());
			
			if (mecanico == null) {
				LOGGER.info("El mecanico no existe");
				throw new BackEndException("El mecanico no existe");
			}
			
			
			problemaRegistro.setIdProblema(searchProblema.getIdProblema());
			problemaRegistro.setIdUsuarioEmi(searchProblema.getIdUsuarioEmi());
			problemaRegistro.setIdUsuarioRecep(mecanico.getId());
			problemaRegistro.setDetalles(searchProblema.getDetalles());
			problemaRegistro.setFoto(searchProblema.getFoto());
			problemaRegistro.setFechaRegistro(searchProblema.getFechaRegistro());
			problemaRegistro.setLatitud(searchProblema.getLatitud());
			problemaRegistro.setLongitud(searchProblema.getLongitud());
			problemaRegistro.setEstado(estado);
			
		}else {
			problemaRegistro.setIdUsuarioEmi(idConductor);
			problemaRegistro.setDetalles(request.getProblema().getDetalles());
			problemaRegistro.setFoto(request.getProblema().getFoto());
			problemaRegistro.setEstado(estado);
			problemaRegistro.setFechaRegistro(new Date());
			problemaRegistro.setLatitud(request.getProblema().getLatitud());
			problemaRegistro.setLongitud(request.getProblema().getLongitud());
			
		}
		
		problemaRegistro =  problemaDAO.registrarProblema(problemaRegistro);
		
		return problemaRegistro;
	}
}
