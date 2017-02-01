package mx.com.rexnato.api;

public class ServicioEndPoint {

	private String metodoHttp;
	
	private String pathServicio;
	
	private String produces;

	public String getProduces() {
		return produces;
	}

	public void setProduces(String produces) {
		this.produces = produces;
	}

	public String getMetodoHttp() {
		return metodoHttp;
	}

	public void setMetodoHttp(String metodoHttp) {
		this.metodoHttp = metodoHttp;
	}

	public String getPathServicio() {
		return pathServicio;
	}

	public void setPathServicio(String pathServicio) {
		this.pathServicio = pathServicio;
	}

	@Override
	public String toString() {
		return "ServicioEndPoint [metodoHttp=" + metodoHttp + ", pathServicio=" + pathServicio + ", produces="
				+ produces + "]";
	}
	
	
}
