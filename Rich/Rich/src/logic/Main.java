package logic;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;

import datas.Cards;
import datas.ChessBoard;
import datas.PlayerInfo;

public class Main 
{
	public static int playerNum = 0;    //���������
	public static int playerReady = 0;    //׼���õ��������
	static int playerNow = 0;      //��ǰ������Ϸ����ұ��
	static int turns = 1;         //��Ϸ�غ���
	public static PlayerInfo player[] = new PlayerInfo[4];
	ServerThread sThread[] = new ServerThread[4];
	final static String lock = new String();
    static PipedOutputStream pos[] = new PipedOutputStream[4];
    static PipedInputStream pis[] = new PipedInputStream[4];
	static ChessBoard chess = new ChessBoard();
	
	static boolean gameOver()
	{
		int sum = 0;
		for (int i = 0;i < 4;i++)
			if (player[i].bankrupt)
				sum++;
		if (sum == playerNum - 1)
			return true;
		else return false;
	}
	
	public static void main (String args[])
	{
		Cards.setCards();
		for (int i = 0;i < 4;i++)
			player[i] = new PlayerInfo(i);
		ServerSocket serverSocket = null;
		boolean listening = true;
		try
		{
			serverSocket = new ServerSocket(4700);
			for (int i = 0;i < 4;i++)
			{
				pos[i] = new PipedOutputStream();
				pis[i] = new PipedInputStream();
				pos[i].connect(pis[i]);
			}
			while (listening)
			{
				new ServerThread(serverSocket.accept(), playerNum).start();
				System.out.println(playerNum++);
			}
			serverSocket.close();
		}
		catch(IOException e) 
		{
			System.out.println("Could not listen port:4700.");
			System.exit(-1);
		}
	}
}