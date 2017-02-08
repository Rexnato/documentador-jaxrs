package mx.com.rexnato.spi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassLoaderUtils {
	
	public static void agregarJars(List<String> jars){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		try {
			
			URL[] urls = new URL[jars.size()];
			
			for(int i=0;i<urls.length;i++){
				urls[i] = new File(jars.get(i)).toURI().toURL();
			}
			
			URLClassLoader urlClassLoader = new URLClassLoader(urls,classLoader);
			
			Thread.currentThread().setContextClassLoader(urlClassLoader);
			
			//System.out.println(urlClassLoader);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void agregarJar(String jar){
		agregarJars(Arrays.asList(jar));
	}
	
	public static void agregarCarpetaJars(String rutaCarpetaJars){
		File carpetaJars = new File(rutaCarpetaJars);
		
		if(!carpetaJars.exists()||!carpetaJars.isDirectory()){
			throw new RuntimeException("El parametro enviado no es una carpeta");
		}
		
		List<String> jarsEncontrados = buscarJars(carpetaJars);
		
		agregarJars(jarsEncontrados);
	}
	
	private static List<String> buscarJars(File archivo){
		List<String> jarsEncontrados = new ArrayList<>();
		
		File[] hijos = archivo.listFiles();
		
		for(File hijo : hijos){
			if(hijo.isDirectory()){
				jarsEncontrados.addAll(buscarJars(hijo));
			} else if(hijo.getName().endsWith(".jar")){
				jarsEncontrados.add(hijo.getAbsolutePath());
			}
		}
		
		return jarsEncontrados;
		
	}
	
	
	public static void main(String[] args) {
		//agregarJars(Arrays.asList("/home/conrado/Documentos/svn/sit-vehicular-integral/vehicular-integral-ws/target/vehicular-integral-ws-1.0.0-SNAPSHOT-classes.jar"));
		agregarCarpetaJars("/home/conrado/Documentos/Servidores/jboss-as-7.1.1.Final/modules");
	}
}
