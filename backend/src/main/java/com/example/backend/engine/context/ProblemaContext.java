package com.example.backend.engine.context;

import com.example.backend.rest.type.request.GestionarProblemaRequest;
import com.example.backend.rest.type.response.GestionarProblemaResponse;

public class ProblemaContext extends Context{
	
	private GestionarProblemaRequest registrarProblemaRequest;
	private GestionarProblemaResponse registrarProblemaResponse;
	
	public GestionarProblemaRequest getRegistrarProblemaRequest() {
		return registrarProblemaRequest;
	}
	public void setRegistrarProblemaRequest(GestionarProblemaRequest registrarProblemaRequest) {
		this.registrarProblemaRequest = registrarProblemaRequest;
	}
	public GestionarProblemaResponse getRegistrarProblemaResponse() {
		return registrarProblemaResponse;
	}
	public void setRegistrarProblemaResponse(GestionarProblemaResponse registrarProblemaResponse) {
		this.registrarProblemaResponse = registrarProblemaResponse;
	}
	
	

}
