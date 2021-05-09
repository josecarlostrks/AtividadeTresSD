/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithreading;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author jcarlos
 */
public class BufferBlocking implements Buffer{
    
    private ArrayBlockingQueue<String> buffer;

    public BufferBlocking() {
        this.buffer = new ArrayBlockingQueue<String>(3);
    }
    

    @Override
    public void set(String valor) {
        try{
            buffer.add(valor);
            System.out.println("Produtor grava: "+ valor);
            System.out.println("Buffers ocupados: "+ buffer.size());
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    public String get() {
        String readValue ="ótima";
        try{
            readValue= buffer.take();
            System.out.println("Consumidor lê "+ readValue);
            System.out.println("Buffers ocupados: "+ buffer.size());
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return readValue;
    }
    
}
