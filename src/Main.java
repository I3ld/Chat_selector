/**
 *
 *  @author Weikert Robert S17092
 *
 */

import java.io.IOException;

public class Main {

  public static void main(String[] args) {
	  try{
		new Thread() {
			public void run() {
				try {
					new Server();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		Thread.sleep(1500);
		
		new Thread(){
			public void run() {
				try {
					new Client("Carl");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Client("John");
	  	}catch(Exception e){
			e.printStackTrace();
		}
  }
}
