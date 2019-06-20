package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Clase encargada de recibir peticiones y de leer los mensajes entrantes.
 * Hereda de la clase Thread
 */
public class SocketServidor extends Thread {
	
	private Socket cliente;
	private ServerSocket servidor;
	private BufferedReader entrada;
	private PrintWriter salida;
	private boolean conexionActiva = true;
	private int id = 0;
	private static Scanner escaner;

	public SocketServidor (Socket clienteket, int id) {
		this.cliente = clienteket;
		this.id = id;
	}

	public void run() {
	  
		do {
			
			try {
				procesarConexion();
			}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				
				mensajeServidor("Conexión terminada de IP ["+ cliente.getInetAddress().toString().substring(1)+ "]:" + " Cliente n° " + id);
	        	conexionActiva=false;
				try {
					mensajeCliente("Conexión terminada");
		        	cliente.close();
		        	entrada.close();
		            salida.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        	
			}
		}while(conexionActiva);
		
	}

	 private void procesarConexion() throws ClassNotFoundException, IOException {
		
		String mensajeEntrada = "";
		entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
		mensajeEntrada = entrada.readLine();
		
		if(mensajeEntrada == null) {
			cerrarConexion();
			}
		else{
			if (!mensajeEntrada.isEmpty()){
				mensajeEntrada = mensajeEntrada.replace(":","");
				if(mensajeEntrada.compareTo("X")==0){
	    			cerrarConexion();
	    		}else {
	    			mensajeServidor("Mensaje del Cliente n°" + id + " - IP ["+ cliente.getInetAddress().toString().substring(1)+ "]:" + mensajeEntrada);
	    			
	    			escaner = new Scanner(System.in);
				    String mensaje = escaner.nextLine();
				   
				    if (mensaje.compareTo("X")==0){
				    	mensajeServidor("Finalizo la conexión");
				    	mensajeCliente("Conexión terminada");
				    	cerrarConexion();
				    	System.exit(0);			    	
				    }else {
				    	mensajeServidor("Mensaje para Cliente n°" + id + " - IP ["+ cliente.getInetAddress().toString().substring(1)+ "]:"+mensaje);
					    mensajeCliente(mensaje);
				    } 
	    		}	
			}	
		}		
	 }
	 
	public void cerrarConexion() throws IOException {
	        try {
	        	mensajeCliente("Conexión terminada");
	        	mensajeServidor("Conexión terminada de IP ["+ cliente.getInetAddress().toString().substring(1)+ "]:" + " Cliente n° " + id);
	        	conexionActiva=false;
	        	cliente.close();
	        	entrada.close();
	            salida.close();
	           
	        } catch (IOException e) {
	        	mensajeCliente("IOException on cerrarConexion()");
	        }
	    }


	private void mensajeCliente(String msg) throws IOException {
		salida = new PrintWriter(cliente.getOutputStream(), true);
		salida.println(msg);
		
	}

	private void mensajeServidor(String msg) {
		System.out.println(msg);
	}


	public ServerSocket getServidor() {
		return servidor;
	}

	public void setServidor(ServerSocket servidor) {
		this.servidor = servidor;
	}
}