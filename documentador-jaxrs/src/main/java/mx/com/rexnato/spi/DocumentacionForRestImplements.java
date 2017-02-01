package mx.com.rexnato.spi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;

import mx.com.rexnato.api.ClaseEndPoint;
import mx.com.rexnato.api.DocumentacionForRest;

public class DocumentacionForRestImplements implements DocumentacionForRest {

	@Override
	public List<ClaseEndPoint> obtenerClasesRestProyecto(String packageName) {
		Class<?>[] clasesEncontradas = {};
		try {
			clasesEncontradas = InspectorClaseUtils.getClasses(packageName);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Ocurrio un error intentando leer el paquete , favor de reportar el bug");
		}
		
		List<ClaseEndPoint> endPointsEncontrados = new ArrayList<>();
		for(Class<?> c : clasesEncontradas){
			Annotation anotacionServicio = c.getAnnotation(Path.class);
			if(anotacionServicio!= null){
				endPointsEncontrados.add(InspectorClaseUtils.obtenerInformacion(c, (Path) anotacionServicio));
			}
		}
		return endPointsEncontrados;
	}
	
	
	
		
	
	
	

}
