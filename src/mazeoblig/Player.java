package mazeoblig;

import java.rmi.Remote;
import java.rmi.RemoteException;

import simulator.PositionInMaze;


public interface Player extends Remote   {
	public PositionInMaze setPlayersPosition(PositionInMaze[] positions) throws RemoteException;
}
