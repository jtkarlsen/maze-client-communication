package mazeoblig;

import java.rmi.ConnectException;
import java.rmi.MarshalException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import simulator.PositionInMaze;

public class MazeServerImpl extends UnicastRemoteObject implements MazeServer {

	private List<Player> players; // Spillerne
	private PositionInMaze[] pos;
	private Timer timer;
	
	public MazeServerImpl() throws RemoteException
	{
		super();
		players = new ArrayList<Player>();
		
		// thread method of pushing
		Thread t = new Thread(){
			public void run() {
				while (true)
				{
					try {
						pushPositions();
						Thread.sleep(100);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
		
		// timer method of pushing
		/*timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					pushPositions();
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};*/
		//timer.scheduleAtFixedRate(task, 1000, 1000); // Bestemmer hvor ofte serveren skal dytte ut posisjoner
	}
	/*
	 * Metode som legger en ny spiller inn i listen over spillere på serveren
	 */
	public int registerPlayer(Player mi) throws RemoteException
	{
		players.add(mi);
		System.out.println("La spiller nr: " + players.size() + " til serveren");
		return players.size();
	}
	
	/*
	 * Metoden kalles regelmessig og sender posisjonene til alle spillerne til hver av spillerne.
	 * Den samme metoden returnerer også koordinatene til den spilleren som får tilsendt posisjonene,
	 * og fyller med dette opp et nytt array av posisjoner som etter at alle spillerne er gått gjennom
	 * skrives over det arrayet med posisjoner som sendes ut ved neste kjørerunde.
	 */
	public void pushPositions() throws RemoteException
	{
		final int antall = players.size(); // Henter størrelsen her, i tilfelle den endrer seg gjennom metoden.
		final PositionInMaze[] positions = new PositionInMaze[antall]; // Gjør klart array som skal erstatte pos;
		int traader = (int) Math.ceil(antall / 100) + 1; // Bestemmer hvos mange tråder som skal opprettes
		Thread[] delTraad = new Thread[traader]; // Definerer arrayet som skal holde trådene
		for (int j = 0; j < traader; j++) // Oppretter alle trådene
		{
			final int k = j;
			delTraad[j] = new Thread(){
				public void run(){
					for (int i = (k * 100); i < (100+k*100); i++)
					{
						if (i == antall)
						{
							System.out.println("Tråd: " + k);
							return;
						}
						try
						{
							positions[i] = players.get(i).setPlayersPosition(pos); // Sender array med posisjoner, returnerer posisjon
							//System.out.println(positions[i].toString());
						}
						catch (ConnectException e) // Hvis server ikke får kontakt med en klient, fjærnes den
						{
							System.out.println("En klient har forlatt!");
							players.remove(i);
							return;
						}
						catch (IndexOutOfBoundsException e)
						{
							System.out.println("IndexOutOfBoundsException");
							return;
						}
						catch (UnmarshalException e)
						{
							System.out.println("ERROR");
							return;
						}
						catch (MarshalException e)
						{
							System.out.println("ERROR");
							return;
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							System.out.println("ERROR");
							e.printStackTrace();
							return;
						}
					}
					System.out.println("Tråd: " + k);
				}
			};
		}
		// Starter trådene
		for (int j = 0; j < traader; j++)
		{
			delTraad[j].start();
		}
		for (int j = 0; j < traader; j++)
		{
			try {
				delTraad[j].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//System.out.println("PUSH!");
		pos = positions; // Erstatter array med spillernes posisjoner, med de nyinnsamlede posisjonene
	}
}
