package mx.com.rexnato.api;

import java.util.List;

public class ClaseEndPoint {

	private String nombreClase;
	
	private String pathEndpoint;
	
	private List<ServicioEndPoint> servicios;
	

	public String getNombreClase() {
		return nombreClase;
	}

	public void setNombreClase(String nombreClase) {
		this.nombreClase = nombreClase;
	}

	public String getPathEndpoint() {
		return pathEndpoint;
	}

	public void setPathEndpoint(String pathEndpoint) {
		this.pathEndpoint = pathEndpoint;
	}

	

	@Override
	public String toString() {
		return "ClaseEndpoint [nombreClase=" + nombreClase + ", pathEndpoint=" + pathEndpoint + ", servicios="
				+ servicios + "]";
	}

	public List<ServicioEndPoint> getServicios() {
		return servicios;
	}

	public void setServicios(List<ServicioEndPoint> servicios) {
		this.servicios = servicios;
	}
	
	

}
