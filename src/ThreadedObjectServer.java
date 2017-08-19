import java.util.*;
import java.io.*;
import java.net.*;

public class ThreadedObjectServer{  
	public static void main(String[] args ){  
		ArrayList<ThreadedObjectHandler> handlers = new ArrayList<ThreadedObjectHandler>();
		try{  
			ServerSocket s = new ServerSocket(4444);
			for (;;){
				Socket incoming = s.accept();
				new ThreadedObjectHandler(incoming, handlers).start();
			}   
		}
		catch (Exception e){  
			System.out.println(e);
		} 
	} 
}

class ThreadedObjectHandler extends Thread{ 
	Object myObject = null;
	private Socket incoming;
	ArrayList<ThreadedObjectHandler> handlers;
	ObjectInputStream in;
	ObjectOutputStream out;

	public ThreadedObjectHandler(Socket incoming, ArrayList<ThreadedObjectHandler> handlers){ 
		this.incoming = incoming;
		this.handlers = handlers;
		handlers.add(this);
	}
	
	public synchronized void broadcast(Object obj){
		Iterator<ThreadedObjectHandler> it = handlers.iterator();
		while(it.hasNext()){
			ThreadedObjectHandler current = it.next();
			try{
				current.out.writeObject(obj);
				current.out.reset();
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
   
	public void run(){  
		try{ 	
			in = new ObjectInputStream(incoming.getInputStream());

			out = new ObjectOutputStream(incoming.getOutputStream());

			for(;;){
				myObject = in.readObject();

				broadcast(myObject);
			}		    
		}catch (Exception e){  
			System.out.println(e);
		}finally{
			handlers.remove(this);
			try{
				in.close();
				out.close();
				incoming.close();
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
}

