package logic;

import java.io.*;
import java.net.*;

import datas.*;

public class ServerThread extends Thread
{
	Socket socket = null;
	int clientnum = 0;

	String line;
	BufferedReader is;
	PrintWriter os;
	byte inArray[] = new byte[1000];
	byte outArray[] = new byte[1000];
	//private PipedInputStream pis;
	//private PipedOutputStream pos;
	//Condition c;
	
	
	public ServerThread(Socket socket, int num) 
	{
		this.socket = socket;
		clientnum = num;
		Main.player[clientnum].active = true;
		//this.pis = pis;
		//this.pos = pos;
	}
	
	public void getInfo()
	{
		try
		{
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
			while (true)
			{
				
				//synchronized (Main.lock)
				//{
					System.out.println("Player " + clientnum + "'s trying.(" + Main.playerNow + ")");
					while (Main.playerNow != clientnum)
					{
						os.println("No");
						os.flush();
						System.out.println("Player " + clientnum + " is waiting......");
						int f = Main.pis[clientnum].read(inArray);
						String receive = new String(inArray).trim();
						while (!receive.equals("end") && !(f == -1))
						{
							System.out.println(receive + " received!(" + clientnum + ")");
							os.println(receive);
							os.flush();
							inArray = new byte[1000];
							f = Main.pis[clientnum].read(inArray);
							receive = new String(inArray).trim();
						}
						os.println("end");
						os.flush();
						System.out.println("One player's turn has ended");
						//Main.lock.wait();
					}
					os.println("Yes");
					os.flush();
					System.out.println("Player " + clientnum + "'s turn has begun.");
					line = is.readLine();
					while (command(line))
					{
						//os.println(line);
						//os.flush();
						for (int i = 0;i < 4;i++)
							if (i != clientnum && !Main.player[clientnum].bankrupt)
								Main.pos[i].write(line.getBytes());
						System.out.println("Player " + clientnum + " " + line);
						line = is.readLine();
					}
					System.out.println("Player " + clientnum + "'s turn is ended.");
					Main.playerNow = (Main.playerNow + 1) % Main.playerNum;
					if (Main.playerNow == 0)
						Main.turns++;
					for (int i = 0;i < 4;i++)
						if (i != clientnum && !Main.player[clientnum].bankrupt)
							Main.pos[i].write("end".getBytes());
					//Main.lock.notifyAll();
				//}
			}
		}
		catch(Exception e) 
		{
			System.out.println(e);
		}
	}
	
	private boolean getReady()
	{
		try 
		{
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
			os.println(clientnum);
			os.flush();
			while (true)
			{
				if (is.readLine().equals("Ready"))
				{
					os.println("Please wait......");
					Main.playerReady++;
					break;
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public void run() 
	{
		System.out.println("New thread " + clientnum);
		if (getReady())
		{
			System.out.println("Player " + clientnum + " is ready.");
			while (Main.playerReady < Main.playerNum || Main.playerNum < 2) {}
			os.println("Start");
			os.flush();
			getInfo();
		}
	}
	
	private void judge(int n, int position)
	{
		int temp = 0;
		switch (n)
		{
		case 0:
			if (Main.player[clientnum].getCash() >= Main.chess.getData(0, position, clientnum))
			{
				//ѯ���Ƿ�������
				//if yes
				Main.player[clientnum].purchaseLand(position);
			}
			break;
		case 1:    //����·��
			int factor = 1;   //״̬����
			if (Main.player[clientnum].getCondNum() == 1)
				factor = 0;
			if (Main.player[clientnum].getCondNum() == 2)
				factor = 2;
			temp = Main.chess.getData(1, position, clientnum) * factor;
			Main.player[clientnum].addCash(-temp);
			Main.player[Main.chess.getOwner(position)].addDeposit(temp);
			break;
		case 2:
			if (Main.player[clientnum].getCash() >= Main.chess.getData(0, position, clientnum))
			{
				//ѯ���Ƿ�ӸǷ���
				//if yes
				Main.player[clientnum].upgradeLand(position);
			}
			break;
		case 3:
			//ѯ���Ƿ���д�ȡ�����
			//if yes
			//temp = ������ȡ����Ϊ����
			Main.player[clientnum].addCash(-temp);
			Main.player[clientnum].addDeposit(temp);
			break;
		case 4:
			temp = Main.chess.getData(4, position, clientnum);  //���ؿ�Ƭ����
			break;
		case 5:   //�������
			Main.player[clientnum].setCondNum(3);
			Main.player[clientnum].setPosition(10);  //λ����Ϊ��������
			Main.player[clientnum].remain = 1;
			break;
		default:
			break;
		}
	}
	
	private void handle(int position)   //�������ڸ���ִ�в�ͬ�ĺ���
	{
		int result = Main.chess.getLattice(position, clientnum);
		if (position < Main.player[clientnum].lastPosition) //�������ӷ������Ϣ
		{
			if (position != 0)
				Main.player[clientnum].addInterest();
		}
		if (Main.chess.passBank(position, Main.player[clientnum].lastPosition))
			judge(3, 20);    //����������ɴ�ȡ��
		Main.player[clientnum].lastPosition = position;
		judge(result, position);
		if (Main.player[clientnum].getCondNum() != 0)  //����״̬
		{
			Main.player[clientnum].remain--;
			if (Main.player[clientnum].remain == 0)
				Main.player[clientnum].setCondNum(0);
		}
		
		if (Main.player[clientnum].bankrupt)  // ����Ʋ�
		{
			Main.chess.clearData(clientnum);    //��������������
			//TO DO......
		}
	}
	
	private boolean command(String s)  //�����û�������ָ����в�ͬ����
	{
		if (s.equals("end"))     //������
			return false;            //������
		if (s.startsWith("@"))        //��������Ϣ��������������
		{
			int n = s.charAt(1) - 48;    //���������������
			if (Main.player[clientnum].getCondNum() == 3)
				n = 0;
			int f = Main.player[clientnum].addPosition(n);
			handle(f);
			return false;
		}
		if (s.startsWith("#"))          //��ѯ����
		{
			String info = Main.player[clientnum].getInfo();
			os.println(info);
			os.flush();
			return true;
		}
		if (s.startsWith("|"))     //�ϴ�ͼƬ����
		{
			try 
			{	
				int len = 0;
				String fileName = s.substring(1);
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				File f = new File(fileName);
				FileOutputStream fout = new FileOutputStream(f);
				if (!f.exists())
					f.createNewFile();
				byte inputByte[] = new byte[1024];
				while ((len = dis.read(inputByte, 0, inputByte.length)) > 0)
				{
					 fout.write(inputByte, 0, len);
	                 fout.flush();
				}
				dis.close();
				fout.close();
			} 
			catch (IOException e) 
			{
				System.out.println(e);
			}
		}
		if (s.startsWith("&"))      //�������
		{
			String words = s.substring(1);
			os.println(words);
			os.flush();
			
			//TO DO
			
			return true;
		}
		if (s.startsWith("%"))       //��ȡ����
		{
			try
			{
				if (s.startsWith("%save%"))
				{
					String fileName = s.substring(6);   //����ļ���
					File f = new File(fileName);
					if (!f.exists())
						f.createNewFile();
					BufferedWriter out = new BufferedWriter(new FileWriter(f));
					for (int i = 0;i < 4;i++)
					{
						out.write(Main.player[i].getData());
						out.newLine();
					}
					for (int i = 0;i < ChessBoard.latticeNum;i++)
					{
						out.write(Main.chess.getInfo(i));
						out.newLine();
					}
					out.close();
					return true;
				}
				
				if (s.startsWith("%load%"))
				{
					String fileName = s.substring(6);
					File f = new File(fileName);
					BufferedReader in = new BufferedReader(new FileReader(f));
					String temp = in.readLine();
					for (int i = 0;i < 4;i++)
					{
						Main.player[i].loadData(temp);
						temp = in.readLine();
					}
					for (int i = 0;i < ChessBoard.latticeNum;i++)
					{
						Main.chess.loadData(temp, i);
						temp = in.readLine();
					}
					return true;
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
		return true;
	}
}
