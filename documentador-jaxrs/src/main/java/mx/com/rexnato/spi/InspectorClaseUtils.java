package mx.com.rexnato.spi;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.lang3.StringUtils;

import mx.com.rexnato.api.ClaseEndPoint;
import mx.com.rexnato.api.ServicioEndPoint;

public class InspectorClaseUtils {
	
	
	//tomado de inter todo lo de abajo no se ni que hace jaja XD 
	/**http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection/520344#520344
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses(String packageName)
	        throws ClassNotFoundException, IOException {
	    
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        //http://stackoverflow.com/a/6138183
	        dirs.add(new File(URLDecoder.decode(resource.getFile(),StandardCharsets.UTF_8.toString())));
	    }
	    
	    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    if(files==null){
	    	return classes;
	    }
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    URLClassLoader.getSystemResource("");
	    return classes;
	}
	
	
	//obtiene los datos del endpoint
	public static ClaseEndPoint obtenerInformacion(Class<?> c,Path path){
				
				ClaseEndPoint nuevo = new ClaseEndPoint();
				nuevo.setNombreClase(c.getName());
				nuevo.setPathEndpoint(path.value());
				//recorro cada uno de los metodos para ver los servicios publicados en cada uno de ellos
				List<ServicioEndPoint> servicios = new ArrayList<>();
				for(Method m : c.getMethods()){
					ServicioEndPoint nuevoServicio = validarAnotacionesMetodo(m.getAnnotations(), nuevo.getPathEndpoint());
					if(nuevoServicio != null){
						servicios.add(nuevoServicio);
					}
				}
				nuevo.setServicios(servicios);
				return nuevo;
	}
			/**
			 * Verifica si es un metodo que se usa para servicio en caso de no serlo
			 * retorna un nullo
			 * @param anotacionesMetodo
			 */
			
			public static ServicioEndPoint validarAnotacionesMetodo(Annotation[] anotacionesMetodo,String pathEndPoint){
				ServicioEndPoint nuevoServicio = new ServicioEndPoint();
				for(Annotation anotacion : anotacionesMetodo ){
					//ver su path
					if(anotacion instanceof Path){
						Path pathMetodo = (Path) anotacion;
						nuevoServicio.setPathServicio(pathEndPoint+pathMetodo.value());
						System.out.println(pathMetodo.value());
					}
					//ver su produces
					else if (anotacion instanceof Produces){
						Produces producesMethodo = (Produces) anotacion;
						String valoresProduce = "";
						for(String s :producesMethodo.value()){
							valoresProduce = valoresProduce+s+",";
						}
						nuevoServicio.setProduces(valoresProduce);
					}else{
						//ver si es un metodo http
						if(anotacion instanceof GET){
							nuevoServicio.setMetodoHttp("GET");
						}else if(anotacion instanceof POST){
							nuevoServicio.setMetodoHttp("POST");
						}else if(anotacion instanceof PUT){
							nuevoServicio.setMetodoHttp("PUT");
						}else if(anotacion instanceof DELETE){
							nuevoServicio.setMetodoHttp("DELETE");
						}else if(anotacion instanceof OPTIONS){
							nuevoServicio.setMetodoHttp("OPTIONS");
						}
					}
				}
				
				if(StringUtils.isBlank(nuevoServicio.getMetodoHttp())  && StringUtils.isBlank(nuevoServicio.getPathServicio())
						&& StringUtils.isBlank(nuevoServicio.getProduces())){
					return null;
				}
				
				return nuevoServicio;
			}

}
