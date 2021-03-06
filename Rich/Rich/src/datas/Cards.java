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
		try     //读取文件中的资料
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
	
	public static int getCards(int n)   //传入玩家编号
	{
		Random random = new Random();
		int result = random.nextInt(10);
		int temp = 0;
		switch (result)
		{
		case 0:   //玩家进入监狱，停留一回合
			Main.player[n].setCondition(Conditions.STOP);
			Main.player[n].setPosition(10);    //位置设为监狱坐标
			Main.player[n].remain = 1;
			break;
		case 1:    //玩家设为无敌状态三回合
			Main.player[n].setCondition(Conditions.LUCKY);
			Main.player[n].remain = 3;
			break;
		case 2:    //玩家设为倒霉状态三回合
			Main.player[n].setCondition(Conditions.DOOM);
			Main.player[n].remain = 3;
			break;
		case 3:    //玩家加发存款利息
			Main.player[n].addInterest();
			break;
		case 4:    //玩家收取税款
			temp = (int) (0.1 * Main.player[n].getFortune());
			Main.player[n].addDeposit(-temp);
			break;
		case 5:    //奖励玩家随机数额金钱(100-1000)
			temp = random.nextInt(900) + 100;
			Main.player[n].addCash(temp);
			break;
		case 6:    //罚款玩家随机数额金钱(100-1000)
			temp = random.nextInt(900) + 100;
			Main.player[n].addCash(-temp);
			break;
		case 7:    //向所有玩家收取200元
			temp = 0;
			for (int i = 0;i < Main.playerNum;i++)
				if (i != n)
				{
					Main.player[i].addCash(-200);
					temp++;
				}
			Main.player[n].addCash(200 * temp);
			break;
		case 8:    //向所有玩家付款200元
			temp = 0;
			for (int i = 0;i < Main.playerNum;i++)
				if (i != n)
				{
					Main.player[i].addCash(200);
					temp++;
				}
			Main.player[n].addCash(-200 * temp);
			break;
		case 9:    //随机获得一块土地或加盖一层房屋
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
