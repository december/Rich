package datas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

enum Types {Land, Bank, Cards, Prison, Startline, Captured, Others};

class Lattice
{
	Types type;
	String name;
	int street;
	int owner;
	int level;          //土地等级
	int landValue;    //土地价值 / 购买所需费用
	int fee;       //过路费
	int upPrice;    //升到下一级所需费用
	
	Lattice()
	{
		level = 0;
		owner = -1;
	}
}

public class ChessBoard 
{
	static Lattice board[] = new Lattice[50];
	public static int latticeNum;
	
	public ChessBoard()
	{
		latticeNum = 30;
		//set street, fee, type and landValue
		//for (int i = 0;i < latticeNum;i++)
		//{
			
		//}
		try     //读取文件中的资料
		{
			File file = new File("BoardData");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String temp;
			for (int i = 0;i < latticeNum;i++)
			{
				temp = reader.readLine();
				String t[] = temp.split(" ");
				board[i].type = Types.valueOf(t[0]);
				board[i].name = t[1];
				board[i].street = Integer.parseInt(t[2]);
				board[i].landValue = Integer.parseInt(t[3]);
				board[i].fee = Integer.parseInt(t[4]);
				board[i].upPrice = Integer.parseInt(t[5]);
			}
			reader.close();
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
	
	public boolean passBank(int pos, int lastPos)
	{
		if (pos > 20 && lastPos < 20)   //代表银行位置
			return true;
		else return false;
	}
	
	public int getOwner(int position)
	{
		return board[position].owner;
	}
	
	public int getLattice(int number, int player)
	{
		Types t = board[number].type;
		if (t == Types.Land)
		{
			if (board[number].owner == -1)
				return 0;   //询问是否买地
			if (board[number].owner != player)
				return 1;    //付过路费
			if (board[number].level < 3)
				return 2;     //询问是否加盖
		}
		if (t == Types.Bank)
			return 3;     //存取款操作
		if (t == Types.Cards)
			return 4;       //抽取卡片操作
		if (t == Types.Captured)
			return 5;          //被捕入狱
		return -1;        //什么也没发生   
	}
	
	public int getData(int result, int position, int n)
	{
		switch (result)
		{
		case 0:
			return board[position].landValue;
		case 1:
			return getFee(position);
		case 2:
			return board[position].upPrice;
		case 4:
			return Cards.getCards(n);
		default:
			return -1;
		}
	}
	
	public int getFee(int number)
	{
		int sum = board[number].fee;
		for (int i = number + 1;i < latticeNum;i++)
		{
			if (board[i].street == board[number].street)
			{
				if (board[i].type == Types.Land && board[i].owner != board[number].owner)
					return board[number].fee;
				if (board[i].type == Types.Land)
					sum += board[i].fee;
			}
			else break;
		}
		for (int i = number - 1;i >= 0;i--)
		{
			if (board[i].street == board[number].street)
			{
				if (board[i].type == Types.Land && board[i].owner != board[number].owner)
					return board[number].fee;
				if (board[i].type == Types.Land)
					sum += board[i].fee;
			}
			else break;
		}
		return sum * 2;
	}
	
	public void clearData(int n)
	{
		for (int i = 0;i < latticeNum;i++)
			if (board[i].owner == n)
				board[i].owner = -1;
	}
	
	public String getInfo(int i)
	{
		String s =  board[i].type + "?" + board[i].name + "?" + board[i].street + "?" 
					+ board[i].owner + "?" + board[i].level + "?" + board[i].landValue
					+ "?" + board[i].fee + "?" + board[i].upPrice + "?";
		return s;
	}
	
	public void loadData(String s, int i)
	{
		String []temp = s.split("?");
		if (temp.length < 8)
		{
			System.out.println("Load error!");
			return;
		}
		board[i].type = Types.valueOf(temp[0]);
		board[i].name = temp[1];
		board[i].street = Integer.parseInt(temp[2]);
		board[i].owner = Integer.parseInt(temp[3]);
		board[i].level = Integer.parseInt(temp[4]);
		board[i].landValue = Integer.parseInt(temp[5]);
		board[i].fee = Integer.parseInt(temp[6]);
		board[i].upPrice = Integer.parseInt(temp[7]);
	}
	
}
