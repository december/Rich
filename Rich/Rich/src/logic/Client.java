package logic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client 
{
	int num;
	Socket socket;
	BufferedReader sin, is;
	PrintWriter os;
	String readline;

	public void connect()
	{
		try
		{
			socket = new Socket("127.0.0.1", 4700);
			num = Main.playerNum++;
			sin = new BufferedReader(new InputStreamReader(System.in));
			os = new PrintWriter(socket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(Exception e) 
		{
			System.out.println("Failed to connect!");
			System.out.println(e);
		}

	}
	
	public void gameRun()  //��Ϸ����
	{
		while (true)
		{
			//���num�Ļغϣ�����ֱ����Ұ����������н���ť
			//���ܽ��еĲ�������ѯ�����졢�ϴ���Ƭ����ȡ���������н���end��
			System.out.println("My game is running......");
			try 
			{
				readline = is.readLine();
				//System.out.println(readline);
				if (readline.equals("Yes"))     //��ҵĻغ�
				{
					System.out.println("It's my turn!");
					while (true)
					{
						readline = sin.readLine();
						os.println(readline);
						os.flush();
						if (readline.equals("end"))   //���Զ��߳���
							break;
					}
					System.out.println("My turn is ended!");
				}
				else                     //�����˵Ļغ�
				{
					System.out.println("I have to wait:(");
					while (true)
					{
						readline = is.readLine();
						System.out.println(readline);
						if (readline.equals("end"))
							break;
					}
					System.out.println("One player's turn is ended......");
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void getReady()
	{
		try 
		{
			readline = is.readLine();
			num = Integer.parseInt(readline);
			System.out.println("I'm player " + num);
			readline = sin.readLine();
			while (!readline.equals("Ready"))
				readline = sin.readLine();
			os.println(readline);
			os.flush();
			System.out.println("I am ready.");
			while (!readline.equals("Start"))
				readline = is.readLine();
		}	 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		System.out.println("Here we go.");
	}
	
	public void sendPic(String name)
	{
		try 
		{
			int len = 0;
			File pic = new File(name);
			FileInputStream fis = new FileInputStream(pic);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			byte sendBytes[] = new byte[1024];
			while ((len = fis.read(sendBytes, 0, sendBytes.length)) > 0) 
			{
                dos.write(sendBytes, 0, len);
                dos.flush();
            }
			dos.close();
			fis.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
	public static void main (String args[])
	{
		Client c = new Client();
		c.connect();
		c.getReady();
		c.gameRun();
	}
	
}