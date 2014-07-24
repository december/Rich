package datas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import logic.Main;

public class Cards 
{
	public class SingleCard
	{
		int number;
		boolean luckyCard;
		String name;
	}
	
	public static SingleCard card[] = new SingleCard[30];
	static int cardNum = 10;
	
	public static void setCards()
	{
		for (int i = 0;i < cardNum;i++)
		{
			card[i].number = i;
			if (i % 2 == 1)
				card[i].luckyCard = true;
			else card[i].luckyCard = false;
		}
		try     //��ȡ�ļ��е�����
		{
			File file = new File("CardData");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String temp;
			for (int i = 0;i < cardNum;i++)
			{
				temp = reader.readLine();
				card[i].name = temp;
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
	
	public static int getCards(int n)   //������ұ��
	{
		Random random = new Random();
		int result = random.nextInt(10);
		int temp = 0;
		switch (result)
		{
		case 0:   //��ҽ��������ͣ��һ�غ�
			Main.player[n].setCondition(Conditions.STOP);
			Main.player[n].setPosition(10);    //λ����Ϊ��������
			Main.player[n].remain = 1;
			break;
		case 1:    //�����Ϊ�޵�״̬���غ�
			Main.player[n].setCondition(Conditions.LUCKY);
			Main.player[n].remain = 3;
			break;
		case 2:    //�����Ϊ��ù״̬���غ�
			Main.player[n].setCondition(Conditions.DOOM);
			Main.player[n].remain = 3;
			break;
		case 3:    //��Ҽӷ������Ϣ
			Main.player[n].addInterest();
			break;
		case 4:    //�����ȡ˰��
			temp = (int) (0.1 * Main.player[n].getFortune());
			Main.player[n].addDeposit(-temp);
			break;
		case 5:    //���������������Ǯ(100-1000)
			temp = random.nextInt(900) + 100;
			Main.player[n].addCash(temp);
			break;
		case 6:    //���������������Ǯ(100-1000)
			temp = random.nextInt(900) + 100;
			Main.player[n].addCash(-temp);
			break;
		case 7:    //�����������ȡ200Ԫ
			temp = 0;
			for (int i = 0;i < Main.playerNum;i++)
				if (i != n)
				{
					Main.player[i].addCash(-200);
					temp++;
				}
			Main.player[n].addCash(200 * temp);
			break;
		case 8:    //��������Ҹ���200Ԫ
			temp = 0;
			for (int i = 0;i < Main.playerNum;i++)
				if (i != n)
				{
					Main.player[i].addCash(200);
					temp++;
				}
			Main.player[n].addCash(-200 * temp);
			break;
		case 9:    //������һ�����ػ�Ӹ�һ�㷿��
			temp = random.nextInt(ChessBoard.latticeNum);
			while (ChessBoard.board[temp].type != Types.Land)
				temp = random.nextInt(ChessBoard.latticeNum);
			if (ChessBoard.board[temp].owner == n)
				Main.player[n].upgradeLand(temp);
			else Main.player[n].purchaseLand(temp);
			break;
			
		}
		return result;
	}

}