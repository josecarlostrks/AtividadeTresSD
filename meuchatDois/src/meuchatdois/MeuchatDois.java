/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meuchatdois;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jcarlos
 */
public class MeuchatDois {


    
    static BufferedReader entradaCliente, entradaServidor;
    static PrintWriter saidaCliente, saidaServidor;
    static String msg;
    static Socket clienteServidor;
    static String emailUsuario;
    
    //lista de destinatarios/usuários
    static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
    static ArrayList<String> nomesUsuarios = new ArrayList<String>();
    static ArrayList<String> mensagensDoServidor = new ArrayList<String>();
    static int mensagensParaClientes =0;
    static boolean esperando = false;

    
    public static void main(String[] args) throws IOException {
        
        
        MeuchatDois.iniciarConexao();
        // cria o servidor, recebe a conexão e cria a thread de manipulação da conversa
        System.out.println("Aguardando conexões...");
        ServerSocket ss = new ServerSocket(5577);
        
        while(true){
            Socket usuario = ss.accept();
            System.out.println("Usuário conectado");
            
            ManipuladorConversa conversa = new ManipuladorConversa(usuario);
            conversa.start();
        }
    }
    
    
    //método que recebe uma mensagem vinda do usuário e encaminha para o servidor Raiz
    //este servidor agora é o cliente da situação
     static public void iniciarConexao() throws IOException{
         Socket clienteServidor = new Socket("localhost",12345);
        
        entradaServidor = new BufferedReader(new InputStreamReader(clienteServidor.getInputStream()));
        saidaServidor = new PrintWriter(clienteServidor.getOutputStream(),true);
    
    }
    
    
    static void lendoDoEspacoCompartilhado() throws IOException{
        String retorno = MeuchatDois.entradaServidor.readLine();
        System.out.println("mensagem de retorno do servidor "+retorno);

            MeuchatDois.mensagensDoServidor.add(msg);         
   
    }
    
    static void escrevendoNoEspacoCompartilhado(String n) throws IOException{
            saidaServidor.println(n);           
    }
    
    public static boolean validarEmail(String email){
        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }    
    
    
}
// Thread que manipula a conversa com o cliente
class ManipuladorConversa extends Thread{
    Socket usuarioConversa;
    BufferedReader entrada;
    static PrintWriter saida;
    String nomeUsuario;
    
    public ManipuladorConversa(Socket usuarioConversa) throws IOException{
        this.usuarioConversa = usuarioConversa;
    }
    
    public void run(){
        try{
            this.entrada = new BufferedReader(new InputStreamReader(usuarioConversa.getInputStream()));
            this.saida = new PrintWriter(usuarioConversa.getOutputStream(), true);
            
            int contador = 0;
            //laço infinito que trata do nome, o usuário só pode mandar e receber mensagens
            //se digitar um nome válido.                      
            int mail = 0;
            while(true){
                if(mail > 0){
                    saida.println("EMAIL_EXISTENTE");
                }else{
                    saida.println("EMAIL_REQUERIDO");
                }
                String email = (String) entrada.readLine();

                if(email==null){
                    return;                                     
                } 

                if(MeuchatDois.validarEmail(email)){
                    MeuchatDois.emailUsuario=email;
                     break;  
                }
                mail++;
                   
            }             
            
            saida.println("EMAIL_ACEITO");


                //adicionando o novo usuário a lista de usuários conectados ao servidor
                //todos da lista recebem as mensagens enviadas ao chat.
             MeuchatDois.printWriters.add(saida);

             while(true){
                 
                 String msg = (String) entrada.readLine();
                 String vazia ="";
                 if(msg==null){
                     System.out.println("mensagem nula por isso volta");
                     continue;
                 }
                 
                 System.out.println("mensagem do cliente "+msg);
                 if(msg.equals(vazia)){
                     System.out.println("pressionou enter");
                 }else{
                     MeuchatDois.escrevendoNoEspacoCompartilhado(msg);
                 }
                 
                 if(MeuchatDois.entradaServidor.readLine()!=null){
                     for(PrintWriter writer: MeuchatDois.printWriters){
                         writer.println((String)MeuchatDois.entradaServidor.readLine());
                     }                 
                 }
             }
                 
            
        }catch(Exception e){
            System.out.println("Erro no Servidor "+ e.getMessage());
            e.printStackTrace();
        }
    }
    
}

