package multithreading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Multithreading {

    
    //lista de servidores destinatarios
    static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
    static String mensagem = "agora produziu e consumiu";

    
    public static void main(String[] args) throws IOException {         
        
        System.out.println("Aguardando conexões...");
        ServerSocket ss = new ServerSocket(12345);
        while(true){
            Socket usuario = ss.accept();
            System.out.println("Servidor usuário conectado");
            
            ManipuladorDeServidores servidor = new ManipuladorDeServidores(usuario);
            servidor.start();
            
            
        }
        
    }
    
    
    //método criado para mandar requsições de volta ao servidor cliente
//    public static void mandarRequisicao(String mensagem) throws IOException{
//        
//        Socket usuario = new Socket("localhost",5566);
//        BufferedReader entrada = new BufferedReader(new InputStreamReader(usuario.getInputStream()));
//        PrintWriter saida = new PrintWriter(usuario.getOutputStream(),true);
//        
//        Socket usuariodois = new Socket("localhost",5577);
//        BufferedReader entradadois = new BufferedReader(new InputStreamReader(usuariodois.getInputStream()));
//        PrintWriter saidadois = new PrintWriter(usuariodois.getOutputStream(),true);
//        
//        saida.println(mensagem);
//        saidadois.println(mensagem);
//    
//    }
        
}

    
// Thread que manipula a conversa
class ManipuladorDeServidores extends Thread{

    Socket servidorUsuario;
    BufferedReader entrada;
    PrintWriter saida;
    String nomeUsuario;   
    private Buffer localizacaoCompartilhada;
    
    public ManipuladorDeServidores(Socket servidorUsuario) throws IOException{
        this.servidorUsuario = servidorUsuario;
    }
    
    public void run(){
        try{
            this.entrada = new BufferedReader(new InputStreamReader(servidorUsuario.getInputStream()));
            this.saida = new PrintWriter(servidorUsuario.getOutputStream(), true);
            
            
            //adicionando o usuário a lista de usuários conectados no servidor Raiz
            Multithreading.printWriters.add(saida);
            System.out.println("a lista de prinwriters do servidor raiz "+Multithreading.printWriters.size());
            
            localizacaoCompartilhada = new BufferBlocking();                      
            
            while(true){
                String msg = (String) entrada.readLine();
                if(msg==null){
                    String resposta = localizacaoCompartilhada.get();
                    return;
                }
                
                System.out.println("isso é apenas para testar como a mensagem chega até multithreading" +msg);
                
                localizacaoCompartilhada.set(msg);
                
                String resposta = localizacaoCompartilhada.get();

                
                //repassei dessa mesma maneira a mensagem do servidor para o cliente, mas não está enviando
                // aqui da Raiz para o servidoCliente

                //Aqui eu tinha comentado para passar os dados via requisição também
                
                for(PrintWriter writer: Multithreading.printWriters){
                    System.out.println("esta é a mensagem de retorno dentro do for "
                        +resposta);
                    writer.println(resposta);
                }   
                  //Multithreading.mandarRequisicao(resposta);
                
                                              
            }
            
        }catch(Exception e){
            System.out.println("Erro no Servidor Raiz"+ e.getMessage());
            e.printStackTrace();
        }
    }
    
}
