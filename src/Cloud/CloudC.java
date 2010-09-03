package Cloud;

/**
 * @author Emanuel Andersson
 */

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Main client class
 * @author Emanuel Andersson
 */

class CloudC
{
    
    Socket              clientSocket;
    String              modifiedSentence;
    DataOutputStream    write;
    DataInputStream     read;
    static String       password;

    /**
     * Main method, handles user input
     * @param argv User input
     * @throws java.io.IOException
     */

    public static void main(String argv[]) throws java.io.IOException
    {

        /* Set custom port, host and password */
        if(argv.length == 3)
        {
            try
            {
                Integer.parseInt(argv[1]);
                password = md5Enc(argv[2]);
            }
            catch(Exception ex)
            {
                System.out.println("Invalid host, port or password: " + ex.getMessage() + ". Please use <host> <port> <password> form.");
                System.exit(1);
            }
        }
        else
        {
            System.out.println("Please specify host, port and password. Use <host> <port> <password> form.");
            System.exit(1);
        }

        /* Run client */
        CloudC client = new CloudC(argv[0], Integer.parseInt(argv[1]));
    }

    CloudC(String host, int port) throws java.io.IOException
    {

        /* Try to connect to server */
        serverConnect(host,port);

        /* Connected loop */
        while(clientSocket.isClosed() == false)
        {
            /* Get user input */
            System.out.print("exec:");
            String userInput = new BufferedReader(new InputStreamReader(System.in)).readLine();

            /* Check if user terminate client */
            if(userInput.toLowerCase().equals("kill cloudc"))
                break;

            /* Try statement to make sure we have connection */
            try
            {
                /* Write to server */
                write.writeUTF(userInput);
                write.flush();

                /* Get response */
                System.out.println(read.readUTF());
            }
            catch(Exception e)
            {
                break;
            }
        }

        /* Clean up */
        read.close();
        write.close();
        clientSocket.close();

        /* Exit information */
        System.out.println("Connection terminated!");
        System.out.print("\n");
        System.exit(1);
    }

    /**
     * Connect to a server
     * @param host Hostname/ipnr
     * @param port Port number
     * @return True it connection was successful
     */
    private boolean serverConnect(String host, int port)
    {
        try
        {
            clientSocket = new Socket(host, port);

            /* Server exchange */
            write = new DataOutputStream(clientSocket.getOutputStream());
            read = new DataInputStream(clientSocket.getInputStream());

            /* Send password */
            write.writeUTF(password);
            write.flush();

            /* Check answer */
            if(read.readUTF().equals("ACCEPTED"))
            {
                System.out.print("\n");
                System.out.println("Connected to " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                return true;
            }
            else
            {
                System.out.println("Wrong password");
                clientSocket.close();
                return false;
            }

        }
        catch(Exception ex)
        {
            return false;
        }
    }

    /**
     * Simple method to encrypt text
     * @param text Text to encrypt
     * @return Encrypted param
     * @throws NoSuchAlgorithmException
     */
    public static String md5Enc(String text) throws NoSuchAlgorithmException
    {
        MessageDigest mdEnc = MessageDigest.getInstance("MD5");
        mdEnc.update(text.getBytes(), 0, text.length());
        return new BigInteger(1, mdEnc.digest()).toString(16);
    }
}
