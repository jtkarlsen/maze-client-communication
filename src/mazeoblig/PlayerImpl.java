package mazeoblig;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import simulator.PositionInMaze;
import simulator.VirtualUser;

public class PlayerImpl extends UnicastRemoteObject implements Player {
	
	private BoxMazeInterface bm;
	private MazeServer ms;
	private Box[][] maze;
	private VirtualUser vu;
	public PositionInMaze [] pos;
	private PositionInMaze [] posFirstIteration;
	private PositionInMaze [] posIteration;
	public PositionInMaze [] posIterationFinal;
	public static int DIM = 10;
	private int dim = DIM;
	private int spillerNr;
	private int i;
	private Timer timer;

	public int x = 0;
	public int y = 0;
	
	static int xp;
	static int yp;
	static boolean found = false;

	private String server_hostname;
	private int server_portnumber;

	public PlayerImpl() throws RemoteException
	{
		super();
		init();
	}

	/**
	 * Henter labyrinten fra RMIServer
	 */
	public void init() {
		int size = dim;
		
		Thread move = new Thread(){
			public void run(){
				if (i == posIterationFinal.length)
				{
					getPlayerPath();
					i = 0;
				}
				setX(posIterationFinal[i].getXpos());
				setY(posIterationFinal[i].getYpos());
				i++;
				/*try {
					Thread.sleep((long) Math.random()*2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		};
		
		
		timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (i == posIterationFinal.length)
				{
					getPlayerPath();
					i = 0;
				}
				setX(posIterationFinal[i].getXpos());
				setY(posIterationFinal[i].getYpos());
				i++;
			}
		};
		
		/*
		 ** Kobler opp mot RMIServer, under forutsetning av at disse
		 ** kjører på samme maskin. Hvis ikke må oppkoblingen
		 ** skrives om slik at dette passer med virkeligheten.
		 */
		if (server_hostname == null)
			server_hostname = "localhost";
		if (server_portnumber == 0)
			server_portnumber = 9000;
		
		//Her begynner letingen etter serveren sin RMI
		try {
			java.rmi.registry.Registry serverRMI = java.rmi.registry.LocateRegistry.getRegistry(server_hostname, server_portnumber);
			
			/*
			 ** Henter inn referansen til Labyrinten (ROR)
			 */
			bm = (BoxMazeInterface) serverRMI.lookup("Maze");
			maze = bm.getMaze();
			
			getPlayerPathStart();
			
			/*
			 *	Registrerer spilleren på serveren
			 */
			ms = (MazeServer) serverRMI.lookup("MazeServer");
			spillerNr = ms.registerPlayer(this);
			System.out.println("Registrert som spiller nr. " + spillerNr);
			
			/*
			 ** Finner løsningene ut av maze - se forøvrig kildekode for VirtualMaze for ytterligere
			 ** kommentarer. Løsningen er implementert med backtracking-algoritme
			 */
			
		}
		catch (RemoteException e) {
			System.out.println("Får ikke kontakt med serveren. Er den i gang?");
			//System.err.println("Remote Exception: " + e.getMessage());
			System.exit(0);
		}
		catch (NotBoundException f) {
			/*
			 ** En exception her er en indikasjon på at man ved oppslag (lookup())
			 ** ikke finner det objektet som man søker.
			 ** Årsaken til at dette skjer kan være mange, men vær oppmerksom på
			 ** at hvis hostname ikke er OK (RMIServer gir da feilmelding under
			 ** oppstart) kan være en årsak.
			 */
			System.err.println("Not Bound Exception: " + f.getMessage());
			System.exit(0);
		}
		timer.scheduleAtFixedRate(task, 1000, 500);
		//move.start();
	}
	
	public PositionInMaze setPlayersPosition(PositionInMaze[] positions) throws RemoteException
	{
		//System.out.println("PositionInMaze kallt eksternt");
		pos = positions;
		//System.out.println("PositionInMaze kallt eksternt return: "+x+" "+y);
		
		return new PositionInMaze(x,y);
	}
	
	public void setX(int _x)
	{
		x = _x;
	}
	public void setY(int _y)
	{
		y = _y;
	}
	/*
	 ** Finner løsningene ut av maze - se forøvrig kildekode for VirtualMaze for ytterligere
	 ** kommentarer. Løsningen er implementert med backtracking-algoritme
	 */
	private void getPlayerPathStart()
	{
		vu = new VirtualUser(maze);
		posFirstIteration = vu.getFirstIterationLoop();
		
		int length = posFirstIteration.length;

		posIterationFinal = new PositionInMaze[length];
		
		for (int j = 0; j < length; j++)
		{
			posIterationFinal[j] = posFirstIteration[j];
		}
	}
	/*
	 ** Finner løsningene ut av maze - se forøvrig kildekode for VirtualMaze for ytterligere
	 ** kommentarer. Løsningen er implementert med backtracking-algoritme
	 */
	private void getPlayerPath()
	{
		vu = new VirtualUser(maze);
		posIteration = vu.getIterationLoop();
		
		int length = posIteration.length;

		posIterationFinal = new PositionInMaze[length];
		for (int j = 0; j < length; j++)
		{
			posIterationFinal[j] = posIteration[j];
		}
	}
}
