package mazeoblig;

import java.rmi.*;

public interface MazeServer extends Remote {
	public int registerPlayer(Player mi) throws RemoteException;
}
