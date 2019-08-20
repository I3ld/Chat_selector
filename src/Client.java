/**
 *
 *  @author Weikert Robert S17092
 *
 */

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;


public class Client {
	
	private SocketChannel socketClientChannel;
	private String name;
	private JTextArea outputMess = new JTextArea();
	private JTextArea inputMess = new JTextArea();
	private int serverPort = 4444;
	

	public Client(String name) throws IOException {
		this.name = name;
		startGui();
	    startClient();
	}
	
	public Client() {
		
	}

	private void sendMsg(String msgString) throws IOException {
		ByteBuffer buffer = null;

		if(msgString != null) {
			byte[] message = new String("("+ name + ") " + msgString).getBytes();	
			buffer = ByteBuffer.wrap(message);  
			socketClientChannel.write(buffer);  
		    buffer.clear();
		    inputMess.setText("");
		}
	}
	
	private void startClient() throws IOException {
		this.socketClientChannel = SocketChannel.open(new InetSocketAddress(serverPort));  
	    
	    while(true){
		    ByteBuffer buffer = ByteBuffer.allocate(1000); 
		    socketClientChannel.read(buffer);  
			String output = new String(buffer.array()).trim();
			
			outputMess.append(output + "\n");
			outputMess.setCaretPosition(outputMess.getDocument().getLength());
			outputMess.update(outputMess.getGraphics());
	    }
	}
	
	private void startGui() {
		JFrame mainF = new JFrame("Chat [zalogowany: " + name + "]");
		Button sendButton = new Button("Wyślij");
		TitledBorder border;
		JPanel bottom = new JPanel();
		JPanel center = new JPanel();
		JScrollPane scrollOut = new JScrollPane(outputMess);
		JScrollPane scrollIn = new JScrollPane(inputMess);
		
		sendButton.addActionListener(e->{
			try {
				this.sendMsg(inputMess.getText());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		});
		
		//Jpanels Border
		border = new TitledBorder("Input");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		   
		//Bottom Jpanel - inputArea
		scrollIn.setPreferredSize(new Dimension(300,40));
		bottom.setLayout(new BoxLayout(bottom,BoxLayout.X_AXIS));
		bottom.add(scrollIn);
		bottom.add(sendButton);
		bottom.setBorder(border);
		   
		//Center Jpanel - output Area
		outputMess.setEditable(false);
		outputMess.setWrapStyleWord(true);
		center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
		center.add(scrollOut);
		border = new TitledBorder("Output area");
		center.setBorder(border);

		//Main Frame 
		mainF.setPreferredSize(new Dimension(600,400));
		mainF.setLayout(new BorderLayout());
		mainF.add(center, BorderLayout.CENTER);
		mainF.add(bottom, BorderLayout.SOUTH);
		mainF.pack();
		mainF.setLocationRelativeTo(null); 
		mainF.setVisible(true);   
	}
	
	public static void main(String[] args) {
		new Client();
	}
	
}
