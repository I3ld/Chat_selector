/**
 *
 *  @author Weikert Robert S17092
 *
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server {
	
	private InetSocketAddress hostAddress;
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private int port = 4444;
	
	public Server() throws IOException {
		selector = Selector.open(); 
		hostAddress = new InetSocketAddress(port); 
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(hostAddress);  
		serverSocketChannel.configureBlocking(false); 
		
		int ops = serverSocketChannel.validOps(); 
	    serverSocketChannel.register(selector, ops, SelectionKey.OP_ACCEPT);   
	     
	    while(true){ 
	    	selector.select();  
	    	Set<SelectionKey> selectedKeys = selector.selectedKeys();  
	    	Iterator<SelectionKey> itr = selectedKeys.iterator(); 
	    	 
	    	 while (itr.hasNext()) {
	    		 SelectionKey key = (SelectionKey) itr.next();
	    		 
	    		if(key.isAcceptable()) {
	    			 SocketChannel clientSocketChannel = serverSocketChannel.accept();
	    			 if(clientSocketChannel != null) {
	    				 clientSocketChannel.configureBlocking(false);
	    				 clientSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE); 
	    			 }
	    			 
	    		 }else if (key.isReadable()) {
	    			 //reading string msg from client
	    			 SocketChannel clientSocketChannel = (SocketChannel) key.channel();  
	    			 ByteBuffer buffer = ByteBuffer.allocate(1000);  
	    			 clientSocketChannel.read(buffer); 
	    			 String output = new String(buffer.array()).trim();

	    			//checking string msg from client and sending to all clients
	    			 if (output.matches("\\(.{1,}\\) logout!")) {
	    				 clientSocketChannel.close(); 
	    				 System.exit(0);
	    			 }else if(output != null){ 
	    				 sendMsgToAll(output, buffer); 
	    			 }	  
	    		 }
			}	
	    	 itr.remove();
	     }
	}
	
	public void sendMsgToAll(String msg, ByteBuffer buffer) {
		try {
			buffer.flip();
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iter = keys.iterator();
			
			while(iter.hasNext()) {
				SelectionKey key = (SelectionKey) iter.next();
				if (key.isWritable()) {
					SocketChannel clientChannel = (SocketChannel) key.channel();
					ByteBuffer buf = Charset.forName("ISO-8859-2").encode(msg);
						
					while( buf.hasRemaining())
						clientChannel.write(buf);
						
					buffer.clear();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public InetAddress getSocketAddress() {
		return this.hostAddress.getAddress();
	}

	public int getSocketSPort() {
		return this.hostAddress.getPort();
	}
	
	public static void main(String[] args) throws IOException {
		new Server();
	}

}
