package servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import config.Configuracion;
import servidor.SocketServidor;

public class Servidor {
	
	private static ServerSocket server;
	private static Socket cliente;
	private static PrintWriter salida;
	private static int id = 0;
	private static Configuracion configuracion;
	private static String ip= "";
	
	public static void main(String[] args) {
		try {
			
			configuracion = Configuracion.getInstance();
			int puerto =  Integer.parseInt(configuracion.getPropiedades().getProperty("socketport"));
			ip =  configuracion.getPropiedades().getProperty("ip");
			server = new ServerSocket(puerto,10);
			mensajeEnServidor("Servidor iniciado - esperando clientes");
		} catch (Exception e) {
			e.printStackTrace();
		}
		cliente = null;
		
		do {
			try {
				cliente = esperarConexion();				
				((SocketServidor) new SocketServidor(cliente, ++id)).start();
				mensajeEnServidor("Conexión desde ip: "+cliente.getInetAddress().toString().substring(1) + " Cliente n°"+id);
				
				mensajeEnCliente("Conectado a " +ip+ " para finalizar la conexión, ingrese X");
				
			
			}catch (IOException e) {
				e.printStackTrace();
			}
		}while(true);
		}
		

	private static Socket esperarConexion() throws IOException {
		mensajeEnServidor("Esperando Conexión");
		return server.accept();
	}

	private static void mensajeEnCliente(String msg) throws IOException {
		salida = new PrintWriter(cliente.getOutputStream(), true);
		salida.println(msg);
		
	}

	private static void mensajeEnServidor(String msg) {
		System.out.println(msg);
	}

	
}
