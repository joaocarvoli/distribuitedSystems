package tcp;

import java.net.*;
import java.io.*;
import data.DataHandlingCalculator;
import data.DataHandlingConverse;
import data.DataHandlingServices;
import services.Calculator;
import flow.*;
import services.CurrencyConverter;


public class TCPServer {
    public static void main (String[] args) {
        try{
            int serverPort = 7896;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
                c.start();
            }
        } catch(IOException e) {System.out.println("Listen :"+e.getMessage());}
    }
}

class Connection extends Thread {
    Socket clientSocket;
    Dispatcher dispatcher;
    public Connection (Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            dispatcher = new Dispatcher(clientSocket);
        } catch(IOException e)  {System.out.println("tcp.Connection:"+e.getMessage());}
    }
    public void run(){
        try {
            String message = dispatcher.getRequest();
            int service = DataHandlingServices.ServiceSelector(message);
            double result = 0;

            if(service == 1)
            {
                Calculator calc = new Calculator();
                DataHandlingCalculator dataHandled = new DataHandlingCalculator(message);
                result = calc.doCalc(dataHandled.getOperator(),dataHandled.getValueA(), dataHandled.getValueB());
            }
            else if(service == 2)
            {
                CurrencyConverter converter = new CurrencyConverter();
                DataHandlingConverse dataHandled = new DataHandlingConverse(message);

                if((dataHandled.getCurrency()).equals("Dollar"))
                {
                    result = converter.DollarToReal(dataHandled.getRealValue(),dataHandled.getDollarValue());
                }
                else if((dataHandled.getCurrency()).equals("Real"))
                {
                    result = converter.RealToDollar(dataHandled.getRealValue(),dataHandled.getDollarValue());
                }
            }

            dispatcher.sendResponse(String.valueOf(result));

        } catch(EOFException e) {System.out.println("EOF Server:"+e.getMessage());
        } catch(IOException e) {System.out.println("IO Server:"+e.getMessage());
        } finally{
            try {
                clientSocket.close();
            }catch (IOException e){/*close failed*/}}
    }
}