package mazeoblig;

import java.awt.*;
import java.applet.*;

import simulator.*;

/**
 *
 * <p>Title: Maze</p>
 *
 * <p>Description: En enkel applet som viser den randomiserte labyrinten</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Tegner opp maze i en applet, basert på definisjon som man finner på RMIServer
 * RMIServer på sin side  henter størrelsen fra definisjonen i Maze
 * @author asd
 *
 */
public class Maze extends NoMoreFlickering {

	private BoxMazeInterface bm;
	private Box[][] maze;
	private PlayerImpl[] player;
	int numberOfPlayers = 1000;
	int playerIndexToBeDrawn = 0;
	
	public static int DIM = 80;
	private int dim = DIM;
	private int i;
	private Timer timer;
	
	static int xp;
	static int yp;
	static boolean found = false;

	private String server_hostname;
	private int server_portnumber;


	/**
	 * Henter labyrinten fra RMIServer
	 */
	public void init() {
		int size = dim;
		i = 0;
		Thread t = new Thread(){
			public void run()
			{
				while (true)
				{
					repaint();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
		/*timer = new Timer();
		TimerTask task = new TimerTask()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				repaint();
			}
		};
		timer.scheduleAtFixedRate(task, 1000, 500); // Bestemmer hvor ofte repaint() skal kalles.
		*/
		// antall spillere som skal lages i denne applet'en
		
		// Lager alle spillerne
		player = new PlayerImpl[numberOfPlayers];
		for (int i = 0; i < numberOfPlayers; i++)
		{
			try {
				player[i] = new PlayerImpl();

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
	}

	
	//Get a parameter value
	public String getParameter(String key, String def) {
		return getParameter(key) != null ? getParameter(key) : def;
	}
	//Get Applet information
	public String getAppletInfo() {
		return "Applet Information";
	}

	//Get parameter info
	public String[][] getParameterInfo() {
		java.lang.String[][] pinfo = { {"Size", "int", ""},
		};
		return pinfo;
	}
	
	/**
	 * Viser labyrinten / tegner den i applet
	 * @param g Graphics
	 */
	public void paint (Graphics g) {
		int x, y;
		
		g.clearRect(0, 0, 2000, 2000);

		// Tegner baser på box-definisjonene ....
		for (x = 1; x < (dim - 1); ++x)
			for (y = 1; y < (dim - 1); ++y) {
				if (maze[x][y].getUp() == null)
					g.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
				if (maze[x][y].getDown() == null)
					g.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
				if (maze[x][y].getLeft() == null)
					g.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
				if (maze[x][y].getRight() == null)
					g.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
			}
		
		// Velg vilken spiller sitt perspektiv du vil følge med å skrive spillerens index til verdi
		
		
		
		
		// Tegner posisjonene sendt til den valgte spilleren
		if (player[playerIndexToBeDrawn].pos != null)
		{
			for (int j = 0; j < player[playerIndexToBeDrawn].pos.length; j++)
			{
				g.fillOval(player[playerIndexToBeDrawn].pos[j].getXpos() *10+2, player[playerIndexToBeDrawn].pos[j].getYpos() *10+2, 5, 5);
			}
		}
		// Tegner spilleren sin nåværende posisjon
		if (player != null)
		{
			g.setColor(Color.red);
			g.fillOval(player[playerIndexToBeDrawn].x*10+2, player[playerIndexToBeDrawn].y*10+2, 5, 5);
			g.setColor(Color.gray);
		}
	}
}
