/*
Matrix Mulplication with one client and multiple servers. The client sends the data to the servers and get the final product matrix.
*/
import java.net.* ;
import java.io.* ;
import java.util.*;

public class ClientFile {

  static Socket sock[];               
  static InetAddress Serveraddresss[];    
  static DataInputStream datainput[];      
  static DataOutputStream dataoutput[];   
  static int TotalServers;

  public static void main(String args[]) throws IOException {
    int i;
    BufferedReader ServerConfigFile; 
    String IntputString=null, Servernames[];

    /* Opening and reading the server configuration file */  
    FileInputStream ff = new FileInputStream("server_config_file.txt");
    ServerConfigFile = new BufferedReader (new InputStreamReader(ff));

    try {
      IntputString = ServerConfigFile.readLine();
      System.out.println("IntputString: "+IntputString);
    } catch (IOException ioe)  {
      System.out.println("Error reading the number of servers") ;
      System.exit(1);
    } 

    try {
      TotalServers = Integer.parseInt(IntputString);      
      System.out.println("TotalServers: "+TotalServers);
    } catch (NumberFormatException nfe) {
      System.out.println("Servers is not an integer.");
      System.exit(1);
    }

    /* Instantiating arrays */
    Servernames = new String[TotalServers];           
    sock = new Socket[TotalServers]; 
    Serveraddresss = new InetAddress[TotalServers]; 
    datainput = new DataInputStream[TotalServers]; 
    dataoutput = new DataOutputStream[TotalServers];

    for (i=0; i < TotalServers; i++) {
      try {
        Servernames[i] = ServerConfigFile.readLine();
      } catch(IOException e) {
        System.out.println("Error reading server names");                                           
        System.exit(1); 
      } 
      Servernames[i] = Servernames[i].trim();
      System.out.println(Servernames[i]); 
    }


    try {
      ServerConfigFile.close();
    } catch (IOException e) {
      System.out.println("Input output error while closing server config file"); 
      System.exit(1);
    } 
    /* Opening the socket connection to the server and setting up the streams */
    try{
      for (i = 0 ;i<TotalServers; i++) {
      Serveraddresss[i] = InetAddress.getByName(Servernames[i]); //getting the ip address
      sock[i] = new Socket(Serveraddresss[i],8000);
      datainput[i] = new DataInputStream(new BufferedInputStream(sock[i].getInputStream())); 
      dataoutput[i] = new DataOutputStream(new BufferedOutputStream(sock[i].getOutputStream())); 
      } 
    } catch(IOException e) {
      System.out.println("Input output error while opening streams and sockets."); 
      System.exit(1); 
    } 

    /* Calling the ClientWork for further operations */  
    ClientWork(); 

    /* Closing the sockets and streams*/
    try{
      for (i = 0 ;i<TotalServers; i++) {
      dataoutput[i].close();
      datainput[i].close();
      sock[i].close();   
      }
    } catch(IOException e) {
      System.out.println("Input output error while closing streams and sockets."); 
      System.exit(1); 

    }

  }

  public static void ClientWork() throws IOException {

    int i, j, k;
    int ColumnsforARowsforB = 0, RowsforA = 0;

    BufferedReader ClientConfigFile ;
    String IntputString = null;
    Scanner s = new Scanner(System.in);

    /* Getting rows for Matrix A */
    System.out.print("Enter number of rows in the first matrix (A) ");
    int rowsInA = s.nextInt();

    /* Getting columns for Matrix A and Rows for matrix B (getting the input for 2 things at the same time becuase for multiplying 2 matrices the columns for Matrix A must be equal to the rows of Matrix B) */
    System.out.print("Enter number of columns in the first martrix A or the rows in the martix B: ");
    int columnsInArowsInB = s.nextInt();

    /* Getting columns for Matrix B */
    System.out.print("Enter number of columns in B: ");
    int columnsInB = s.nextInt();

    /* Matrices created according to user input */
    int[][] A = new int[rowsInA][columnsInArowsInB];
    int[][] B = new int[columnsInArowsInB][columnsInB];

    ColumnsforARowsforB = rowsInA;

    Random rand = new Random();

    /* Adding rando values from 0-99 to Matrix A */
    for (i=0;i<rowsInA;i++){
      for(j=0;j<columnsInArowsInB;j++){
        A[i][j] = rand.nextInt(100);
      }
    }

    /* Adding rando values from 0-99 to Matrix B */
    for ( i=0;i<columnsInArowsInB;i++){
      for( j=0;j<columnsInB;j++){
        B[i][j] = rand.nextInt(100);
      }
    }
    /* Displaying the Matrix A values */
    System.out.println("Matrix A is:");
    for(int m=0;m<A.length;m++){
      for(int l=0;l<A[0].length;l++){
        System.out.print(A[m][l] + "  ");
      }
      System.out.println();
    }

    /* Displaying the Matrix B values */
    System.out.println("Matrix B is:");
    for(int m=0;m<B.length;m++){
      for(int l=0;l<B[0].length;l++){
        System.out.print(B[m][l] + "  ");
      }
      System.out.println();
    }

    /* Initializing Matrix C which is the product matrix */
    int[][] C = new int[rowsInA][columnsInB];

    /* Diving number of tasks for the available servers */
    RowsforA = ColumnsforARowsforB/TotalServers;

    System.out.println("Sending required data to servers!!");

    Date start = new Date();
    try{
      /* Sending data or writing data to the server */
      for (i = 0 ;i<TotalServers; i++) {
        dataoutput[i].writeInt(columnsInArowsInB) ;
        dataoutput[i].writeInt(columnsInB) ;
        dataoutput[i].writeInt(RowsforA) ;
        dataoutput[i].flush();
        for (j = RowsforA*i ;j<RowsforA*(i+1); j++) {
          for (k = 0 ;k<columnsInArowsInB; k++) {
            dataoutput[i].writeInt(A[j][k]);
          }
          dataoutput[i].flush();
        }


        for (j = 0;j<columnsInArowsInB; j++) {
          for (k = 0 ;k<columnsInB; k++) {
            dataoutput[i].writeInt(B[j][k]);
          }
          dataoutput[i].flush();
        }

      }
    } catch(IOException e) {
      System.out.println("Error sending matrix data to servers!!!");                                           
      System.exit(1); 
    } 

    try{
      /* Reading data from the server and storing it to the array C */
      for (i = 0 ;i<TotalServers; i++) {
        for (j = RowsforA*i ;j<RowsforA*(i+1); j++) {
          for (k = 0 ;k<ColumnsforARowsforB; k++) {
            C[j][k] = datainput[i].readInt();
          }
        }
      } 

      System.out.println();

      /* Displaying Final Product Matrix */
      System.out.println("Product Matrix");
      System.out.println();
      for (i = 0;i<C.length; i++) {
        for (j = 0 ;j<C[0].length; j++) {
          System.out.print(C[i][j] + "   ");
        }
        System.out.println();
      }
    } 
    catch(IOException e) {
      System.out.println("Error receiving result from server.");                                           
      System.exit(1); 
    } 
    Date end = new Date();
    System.out.print("\nTime taken in milli seconds: " + (end.getTime() - start.getTime())+ " for Matrix A: "+rowsInA +"X"+ columnsInArowsInB + " and Matrix B: "+columnsInArowsInB+"X"+columnsInB);
  } /* End public class NetC*/
}
