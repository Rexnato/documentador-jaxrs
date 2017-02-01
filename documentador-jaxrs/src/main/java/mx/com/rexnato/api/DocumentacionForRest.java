package mx.com.rexnato.api;

import java.util.List;

public interface DocumentacionForRest {
	
	/**
	 * Retorna todas las clases que tengan servicios 
	 * rest publicados en el proyecto
	 * con que esten compilados en el proyecto los detecta
	 * @param packageName pasar la ruta del paquete a analizar puede ser la inicial de una carpeta
	 * @return
	 */
	List<ClaseEndPoint> obtenerClasesRestProyecto(String packageName);
	
	
	
}
