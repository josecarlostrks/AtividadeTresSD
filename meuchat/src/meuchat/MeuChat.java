/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meuchat;

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
public class MeuChat {
    
    static BufferedReader entradaServidor;
    static PrintWriter saidaServidor;
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
        
        MeuChat.iniciarConexao();
        // cria o servidor, recebe a conexão e cria a thread de manipulação da conversa
        System.out.println("Aguardando conexões...");
        ServerSocket ss = new ServerSocket(5566);
        while(true){
            Socket usuario = ss.accept();
            System.out.println("Usuário conectado");
            
            ManipuladorConversa conversa = new ManipuladorConversa(usuario);
            conversa.start();
        }
    }
    
    static public void iniciarConexao() throws IOException{
         Socket clienteServidor = new Socket("localhost",12345);
        
        entradaServidor = new BufferedReader(new InputStreamReader(clienteServidor.getInputStream()));
        saidaServidor = new PrintWriter(clienteServidor.getOutputStream(),true);
    
    }
    
    static void lendoDoEspacoCompartilhado() throws IOException{
        String retorno = MeuChat.entradaServidor.readLine();
        System.out.println("mensagem de retorno do servidor "+retorno);

            MeuChat.mensagensDoServidor.add(msg);         
   
    }
    
    static void escrevendoNoEspacoCompartilhado(String n) throws IOException{   
            MeuChat.saidaServidor.println(n);
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
                
                if(MeuChat.validarEmail(email)){
                    MeuChat.emailUsuario=email;
                    
                     break;  
                }
                mail++;
                 
            } 
            saida.println("EMAIL_ACEITO");
            




                //adicionando o novo usuário a lista de usuários conectados ao servidor
                //todos da lista recebem as mensagens enviadas ao chat.
                MeuChat.printWriters.add(saida);


                //laço que recebe e trata cada mensagem enviada.
                //se a mensagem é válida ela é enviada para o servidor Raiz
            while(true){
                
                String msg = (String) entrada.readLine();
                String vazia ="";

                 
                 if(msg==null){
                     System.out.println("nula");
                     continue;
                 }
                 
                 System.out.println("mensagem do cliente "+msg);
                 if(msg.equals(vazia)){
                     System.out.println("pressionou enter");
                 }else{
                     MeuChat.escrevendoNoEspacoCompartilhado(msg);
                 }
                 
                 if(MeuChat.entradaServidor.readLine()!=null){
                     for(PrintWriter writer: MeuChat.printWriters){
                         writer.println((String)MeuChat.entradaServidor.readLine());
                     }                  
             }
            
            
        }catch(Exception e){
            System.out.println("Erro no Servidor "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
