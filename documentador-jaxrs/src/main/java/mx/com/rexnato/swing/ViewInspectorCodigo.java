package mx.com.rexnato.swing;

import java.awt.EventQueue;
import java.io.File;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.ws.rs.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import mx.com.rexnato.api.ClaseEndPoint;
import mx.com.rexnato.spi.InspectorClaseUtils;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ViewInspectorCodigo {

	private JFrame frame;
	private JTextField inputUbicacionLibrerias;
	private JTextField inputRutaWar;
	private JTextArea textArea;
	
	//botonoes
	private JButton btnExaminarLibrerias;
	private JButton btnExaminarWar;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					ViewInspectorCodigo window = new ViewInspectorCodigo();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ViewInspectorCodigo() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 749, 525);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		inputUbicacionLibrerias = new JTextField();
		inputUbicacionLibrerias.setEnabled(false);
		inputUbicacionLibrerias.setBounds(145, 65, 560, 20);
		frame.getContentPane().add(inputUbicacionLibrerias);
		inputUbicacionLibrerias.setColumns(10);
		
		btnExaminarLibrerias = new JButton("Examinar");
		btnExaminarLibrerias.setBounds(24, 64, 89, 23);
		btnExaminarLibrerias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				metodoBottonExaminarLibrerias(arg0);
			}
		});
		frame.getContentPane().add(btnExaminarLibrerias);
		
		btnExaminarWar = new JButton("Examinar");
		btnExaminarWar.setBounds(24, 150, 89, 23);
		btnExaminarWar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				metodoBottoExaminarWar(arg0);
			}
		});
		frame.getContentPane().add(btnExaminarWar);
		
		inputRutaWar = new JTextField();
		inputRutaWar.setEnabled(false);
		inputRutaWar.setColumns(10);
		inputRutaWar.setBounds(145, 151, 560, 20);
		frame.getContentPane().add(inputRutaWar);
		
		JLabel lblNewLabel = new JLabel("* Seleccione la carpeta donde estan contenidos las bibliotecas  de tu artefacto");
		lblNewLabel.setBounds(24, 38, 493, 16);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblSelecioneEl = new JLabel("* Selecione el archivo war a inspeccionar");
		lblSelecioneEl.setBounds(24, 124, 493, 16);
		frame.getContentPane().add(lblSelecioneEl);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(41, 268, 664, 207);
		frame.getContentPane().add(scrollPane);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollPane.setRowHeaderView(scrollBar);
		
		textArea = new JTextArea();
		//text area muestre el log 
		textArea.setEditable(false);
	    PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
	    // keeps reference of standard output stream
	    //standardOut = System.out;
	         
	        // re-assigns standard output stream and error output stream
	    System.setOut(printStream);
	    System.setErr(printStream);
		scrollPane.setViewportView(textArea);
		
		JButton btnInspeccionar = new JButton("Inspeccionar :D");
		btnInspeccionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				metodoBottonInspeccionar(arg0);
			}
		});
		btnInspeccionar.setBounds(549, 204, 156, 35);
		frame.getContentPane().add(btnInspeccionar);
	}
	
	private void metodoBottonExaminarLibrerias(ActionEvent e){
		this.inputUbicacionLibrerias.setText("");
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle("Buscar Directorio con Librerias");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// disable the "All files" option
	    fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) { 
		    String pathCarpeta = fileChooser.getSelectedFile().getPath();
		    System.out.println(new Date()+" - La carpeta seleccionada para buscar las librerias es : \n"+pathCarpeta);
		    this.inputUbicacionLibrerias.setText(pathCarpeta);
		}else{
			System.out.println("No se selecciona la carpeta de las librerias :S");
		}
	}
	
	private void metodoBottoExaminarWar(ActionEvent e ){
		this.inputRutaWar.setText("");
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle("Buscar Directorio con WARS");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// disable the "All files" option
	    fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) { 
		    String pathFile = fileChooser.getSelectedFile().getPath();
			String extension = FilenameUtils.getExtension(pathFile);
			if(extension.equals("war") || extension.equals("jar")){
				 System.out.println(new Date()+" - El war seleccionado es  para buscar las librerias es : \n"+pathFile);
				 this.inputRutaWar.setText(pathFile);
			}else{
				JOptionPane.showMessageDialog(frame, "El archivo seleccionado es invalido , solo se aceptan con extension jar o war ");
				System.err.println("El archivo seleccionado es invalido");
			}
		   
		}else{
			System.out.println("No se selecciono un war :S");
		}
	}
	
	private void metodoBottonInspeccionar(ActionEvent e ){
		
		if(StringUtils.isBlank(this.inputRutaWar.getText())){
			JOptionPane.showMessageDialog(frame, "Debe seleccionar el archivo jar o war a examinar");
			System.err.println("Debe seleccionar el archivo jar o war a examinar");
			return ;
		}
		
		ejecutarDocumentacion();
	}
	
	/**
	 * Realiza la documentacion del compa jeje
	 */
	private  void ejecutarDocumentacion(){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				btnExaminarLibrerias.setEnabled(false);
				btnExaminarWar.setEnabled(false);
				try {
					//cargo los modulos
					long currentTimeBefore = System.currentTimeMillis();
					if(!StringUtils.isBlank(inputUbicacionLibrerias.getText())){
						ClassLoaderUtils.agregarCarpetaJars(inputUbicacionLibrerias.getText());
					}
					
					//ahora si cargo los rest con el metodo mejorado del compa :D
					Class<?>[] clasesEncontradas = InspectorArchivoUtils.obtenerClases(inputRutaWar.getText());
					System.out.println("Clases procesadas: "+clasesEncontradas.length);
					long currentTimeAfter = System.currentTimeMillis();
					System.out.println("Segundos Transcurridos procesando: "+(currentTimeAfter-currentTimeBefore)/1000);
					List<ClaseEndPoint> endPointsEncontrados = new ArrayList<>();
					for(Class<?> c : clasesEncontradas){
						Annotation anotacionServicio = c.getAnnotation(Path.class);
						if(anotacionServicio!= null){
							endPointsEncontrados.add(InspectorClaseUtils.obtenerInformacion(c, (Path) anotacionServicio));
						}
					}				
					System.out.println("Servicios encontrados: "+endPointsEncontrados.size());
				} catch (Exception e) {
					System.err.println("Ocurrio un error al intentar generar");
					e.printStackTrace();
				} finally{
					//cochino compa lanza runtimes
					btnExaminarLibrerias.setEnabled(true);
					btnExaminarWar.setEnabled(true);
				}
				
				
				
			}
		});
		thread.start();
	}
	
}
