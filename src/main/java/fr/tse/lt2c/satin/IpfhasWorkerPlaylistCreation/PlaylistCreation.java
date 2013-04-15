package fr.tse.lt2c.satin.IpfhasWorkerPlaylistCreation;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;

public class PlaylistCreation extends IpfhasWorkerPlaylistCreation implements GearmanFunction{

	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback)
			throws Exception {
		return null;
	}
}
