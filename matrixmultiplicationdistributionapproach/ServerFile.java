/*Server Code thats listening at port 8000*/
/* This will receive data and compute the result and send back the result to the client*/
/* Import the required classes */
import java.net.* ;
import java.io.* ;
import java.util.*;

public class ServerFile {

  static ServerSocket serverSocket;  
  static DataInputStream datainput;    
  static DataOutputStream dataoutput;
  static Socket mySocket;

  public static void main(String args[]) throws IOException {
    System.out.println("Starting Server...");

    try {
      serverSocket = new ServerSocket(8000);
    } catch (IOException eos)  {
      System.out.println("Error while opening Server Socket.") ;
      System.exit(1);
    } 

    /* Waits for connection indefinitely unless there is an exception error */
    while (true) { 
      try {
        mySocket = serverSocket.accept(); 
      } catch (IOException e)  {
        System.out.println("Error in accepting client connection.") ;
        System.exit(1);
      }

      datainput  = new DataInputStream(new BufferedInputStream(mySocket.getInputStream())); 
      dataoutput = new DataOutputStream(new BufferedOutputStream(mySocket.getOutputStream())); 
      System.out.println(mySocket+ "Waiting for job...");
      ServerWork(); 
    }
      
  }

/*
The following method performs and computes the product matrx. 
It will receive the number of rows and columns for Matrix A and Matrix B. 
And comput product matrix C or Result.
*/

  public static void ServerWork() throws IOException {

    int i, j, k, sum ;
    int ColumnsforARowsforB = 0, RowsforA = 0, ColumnsforB=0;         
    int A[][], B[][], Result[][];    

    try {
      ColumnsforARowsforB = datainput.readInt(); // Read the value of ColumnsforARowsforB
    } catch (IOException e)  {
      System.out.println("Input/Output Error while getting data from client.") ;
      System.exit(1);
    }
    try {
      ColumnsforB = datainput.readInt(); // Read the value of ColumnsforB
    } catch (IOException e)  {
      System.out.println("Input/Output Error while getting data from Client.") ;
      System.exit(1);
    }  
    try {
      RowsforA = datainput.readInt(); // Read the value of RowsforA 
    } catch (IOException e)  {
      System.out.println("Input/Output Error while getting data from Client.") ;
      System.exit(1);
    } 

    A = new int[RowsforA][ColumnsforARowsforB];
    B = new int[ColumnsforARowsforB][ColumnsforB];

    Result = new int[A.length][B[0].length];

    System.out.println("Receiving matrix A from client: "+ RowsforA+"X"+ColumnsforARowsforB) ;

    /* Reading the values for Matrix A */
    for (i = 0 ;i<RowsforA; i++){
      for (j = 0 ;j<ColumnsforARowsforB; j++){
        try{ 
          A[i][j] = datainput.readInt();
        } catch(IOException e) {
          System.out.println("Input/Output error while getting data from Client.");                                           
          System.exit(1); 
        }
      }
    }
  

    System.out.println("Receiving Matrix B from client: "+ ColumnsforARowsforB+"X"+ColumnsforB);
   
    /* Reading the values for Matrix A */
    for (i = 0 ;i<ColumnsforARowsforB; i++) { 
      for (j = 0 ;j<ColumnsforB; j++) {
        try{ 
          B[i][j] = datainput.readInt();
        } catch(IOException e) {
          System.out.println("Input/Output error while getting data from Client.");                                           
          System.exit(1); 
        }
      }
    }

    /* Multiply and compute the product matrix */
    for (i = 0 ;i<A.length; i++) {
      for (j = 0 ;j<B[0].length; j++) {
        sum = 0;
          for (k = 0 ;k<A[i].length; k++) {
            sum += A[i][k] * B[k][j] ;
            Result[i][j] = sum;
          }
      }
    }

    /* Send the computed result to the client */
    for (i = 0 ;i<A.length; i++){
      for (j = 0 ;j<B[0].length; j++){
        try{ 
          dataoutput.writeInt(Result[i][j]);
        } catch(IOException e) {
          System.out.println("Input/Output error message sent to Client.");                                           
          System.exit(1); 
        }
      }
    }
    dataoutput.flush();
    System.out.println("Product matrix sent to Client.");     
  }
}





