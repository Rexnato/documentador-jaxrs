package mx.com.rexnato.swing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.Path;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import mx.com.rexnato.api.ClaseEndPoint;
import mx.com.rexnato.spi.InspectorClaseUtils;
import mx.com.rexnato.swing.ClassLoaderUtils;

public class InspectorArchivoUtils {

	private static List<FileObject> clasesConDependencias;
	
	private static FileSystemManager manager;
	
	public static Class<?>[] obtenerClases(String rutaArchivo){
		
		clasesConDependencias = new CopyOnWriteArrayList<>();
		
		try {
			manager= VFS.getManager();
			FileObject archivoVirtual = manager.resolveFile("jar:"+rutaArchivo);
			
			List<Class<?>> clases = buscarClases(archivoVirtual);
			
			//System.out.println("Reintentando");
			
			for(FileObject archivoClass : clasesConDependencias){
				Class<?> clase = obtenerClase(archivoClass);
				if(clase!=null){
					clases.add(clase);
				}
			}
			
			clasesConDependencias.clear();
			
			return clases.toArray(new Class<?>[0]);
			
		} catch(FileSystemException e){
			throw new RuntimeException("Error al leer el archivo "+rutaArchivo, e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Class<?>[0];
		}
	}
	
	private static List<Class<?>> buscarClases(FileObject directorio) throws Exception{
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if(directorio.isFolder()&&directorio.exists()){
			FileObject[] children = directorio.getChildren();
			
			if(children!=null&&children.length!=0){
        		classes.addAll(buscarClases(Arrays.asList(children)));
        	}
		}
		return classes;
	}
	
	private static List<Class<?>> buscarClases(List<FileObject> archivosDirectorio) throws Exception{
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    
	    for (FileObject file : archivosDirectorio) {
	        if (file.isFolder() && file.exists()) {
	       
	        	classes.addAll(buscarClases(file));
	            
	        } else if(file.getName().getExtension().equals("jar")){
	        	FileObject jar = file;
	        	
	        	/*if(manager==null){
	        		manager= VFS.getManager();
	        	}*/
	        	
	        	InputStream streamJar = jar.getContent().getInputStream();
	        	
	        	File carpetaJars = new File(System.getProperty("java.io.tmpdir")+"/jars_documentador");
	        	
	        	if(!carpetaJars.exists()){
	        		carpetaJars.mkdir();
	        		carpetaJars.deleteOnExit();
	        	}
	        	
	        	File archivoJar = new File(carpetaJars.getPath()+"/"+jar.getName().getBaseName());
	        	
	        	//Si el archivo ya existe continuo
	        	if(archivoJar.exists()){
	        		continue;
	        	}
	        	
	        	//System.out.println("Creando archivo en "+archivoJar.getAbsolutePath());
	        	archivoJar.createNewFile();
	        	archivoJar.deleteOnExit();
	        	OutputStream streamArchivo = new FileOutputStream(archivoJar);
	        	IOUtils.copy(streamJar, streamArchivo);
	        	IOUtils.closeQuietly(streamJar);
	        	IOUtils.closeQuietly(streamArchivo);
	        	
	        	FileObject jarLeido = manager.resolveFile("jar:"+archivoJar.getPath());
	        	
	        	classes.addAll(buscarClases(jarLeido));
	        } else if (file.getName().getExtension().equals("class")) {
	            Class<?> clase = obtenerClase(file);
	        	if(clase!=null){
	        		classes.add(clase);
	        	}
	        }
	    }
	    return classes;
	}
	
	private static Class<?> obtenerClase(FileObject archivoClass) throws Exception{
		
		InputStream inputStream = archivoClass.getContent().getInputStream();
		
		byte[] bytes = IOUtils.toByteArray(inputStream);
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		//https://docs.oracle.com/javase/7/docs/api/java/lang/ClassLoader.html#defineClass(byte[],%20int,%20int)
		Method metodoDefinirClase = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class,int.class,int.class);
		metodoDefinirClase.setAccessible(true);
		
		try{
			Class<?> clase = (Class<?>) metodoDefinirClase.invoke(classLoader, bytes,0,bytes.length);
			//System.out.println(clase);
			return clase;
		} catch(InvocationTargetException ex){
			
			System.err.print("No se pudo cargar "+archivoClass.getName().getPathDecoded()+". ");
			if(ex.getTargetException().getCause() instanceof ClassNotFoundException){
				String claseFaltante = ex.getTargetException().getMessage();
				System.err.println("No se encuentra la clase "+claseFaltante.replaceAll("/", "."));
			}
			clasesConDependencias.add(archivoClass);
			//ex.printStackTrace();
		}
		
		return null;
	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		//DocumentacionForRest documentacion = new DocumentacionForRestImplements();
//		//System.out.println(documentacion.obtenerClasesRestProyecto("mx"));
//		//System.getProperties().list(System.out);
//		
//		ClassLoaderUtils.agregarCarpetaJars("/home/conrado/Documentos/Servidores/jboss-as-7.1.1.Final/modules");
//		
//		long currentTimeBefore = System.currentTimeMillis();
//		
//		//Class<?>[] clasesEncontradas = obtenerClases("/home/conrado/Documentos/svn/sit-vehicular-integral/vehicular-integral-ws/target/vehicular-integral-ws-1.0.0-SNAPSHOT.war");
//		//Class<?>[] clasesEncontradas = obtenerClases("/home/conrado/Documentos/svn/sit-vehicular-integral/vehicular-integral-ws/target/vehicular-integral-ws-1.0.0-SNAPSHOT-classes.jar");
//		Class<?>[] clasesEncontradas = obtenerClases("/home/conrado/Documentos/svn/trunk/finanzas/finanzas-web/target/finanzas-web-5.0.6-SNAPSHOT.war");
//		
//		System.out.println("Clases procesadas: "+clasesEncontradas.length);
//		
//		long currentTimeAfter = System.currentTimeMillis();
//		System.out.println("Segundos Transcurridos procesando: "+(currentTimeAfter-currentTimeBefore)/1000);
//		
//		
//		List<ClaseEndPoint> endPointsEncontrados = new ArrayList<>();
//		for(Class<?> c : clasesEncontradas){
//			Annotation anotacionServicio = c.getAnnotation(Path.class);
//			if(anotacionServicio!= null){
//				endPointsEncontrados.add(InspectorClaseUtils.obtenerInformacion(c, (Path) anotacionServicio));
//			}
//		}
//		
//		System.out.println("Servicios encontrados: "+endPointsEncontrados.size());
//		
//	}

}
