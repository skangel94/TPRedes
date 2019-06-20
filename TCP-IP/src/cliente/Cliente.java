package cliente;

import java.net.*;
import java.util.Scanner;

import config.Configuracion;

import java.io.*;

public class Cliente {
	
	private Socket clienteSocket;
	private BufferedReader entrada = null;
    private PrintWriter salida = null;
    
    private static Scanner escaner;
    Scanner teclado = new Scanner(System.in);
	private static String ipConfig;
	private static String puertoConfig;
	private static boolean isOk = false;
	
    public static void main(String[] args) {
        
    
    	Cliente cliente = new Cliente();
       
    	Configuracion configuracion = Configuracion.getInstance();
		ipConfig = configuracion.getPropiedades().getProperty("ip");
		puertoConfig = configuracion.getPropiedades().getProperty("socketport");
		String puerto = "";
		String ip = "";
    	
		do {
			
			escaner = new Scanner(System.in);
            mostrarTexto("Ingresar IP: ");
            ip = escaner.nextLine();
            
            mostrarTexto("Puerto: ");
            puerto = escaner.nextLine();
            
            if ((ip.compareTo(ipConfig)==0) && (puerto.compareTo(puertoConfig)==0))
            	isOk = true;
            else
             	System.out.println("Ip o Puerto ingresados son incorrectos, por favor ingrese nuevamente.");
            

    	}while(!isOk);
		        
        
        if(isOk == true) {
        	cliente.ejecutarConexion(ip, Integer.parseInt(puerto));
            cliente.escribirDatos();
        }
        
    }
    
    public void ejecutarConexion(String ip, int puerto) {
        
    	Thread hilo = new Thread(new Runnable() 
    	{
            
            public void run() {
                
            	
            	levantarConexion(ip, puerto);
				obtenerStreams();
				recibirDatos();
            }
        }
    	);
        hilo.start();
    }
    
	
    public void levantarConexion(String ip, int puerto) {
        try {
        	clienteSocket = new Socket(ip, puerto);
        } catch (Exception e) {
            mostrarTexto("Excepción al levantar conexión: " + e.getMessage());
            System.exit(0);
        }
    }


    public void obtenerStreams() {
        try {
            entrada =new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            salida = new PrintWriter(clienteSocket.getOutputStream(),true);;
            salida.flush();
            


            
        } catch (IOException e) {
            mostrarTexto("Error en obtener Streams");
        }
    }
    
    public void recibirDatos() {
        String st = "";
        try {
            do {
                st = (String) entrada.readLine();
                mostrarTexto(st);
                
                if(st.compareTo("Conexión terminada")==0){
        			cerrarConexion();
        			System.exit(0);
        			
        		}else {
        			System.out.print("\n[Cliente]: ");
        		}
                
            } while (true);
        } catch (IOException e) {
        	mostrarTexto("Conexión terminada");
        	System.exit(0);
        }
    }
   
    public void cerrarConexion() {
        try {
        	clienteSocket.close();
        	entrada.close();
            salida.close();
            teclado.close();
            escaner.close();
            
        } catch (IOException e) {
            mostrarTexto("IOException on cerrarConexion()");
        }
        
    }
    
    public void escribirDatos() {
        String entrada = "";
        while (true) {
            System.out.print("[Servidor]: ");
            entrada = teclado.nextLine();
            if(entrada.length() > 0) {
            	enviar(entrada);
            	if(entrada.compareTo("X")==0 ) {
                	cerrarConexion();
                	System.exit(0);
            	}
            }
        }
    }

    public void enviar(String s) {
        salida.println(s);
		salida.flush();
    }


    public static void mostrarTexto(String s) {
        System.out.println(s);
    }
    

	
}