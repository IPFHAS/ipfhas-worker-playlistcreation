package fr.tse.lt2c.satin.IpfhasWorkerPlaylistCreation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for the Playlist Creation
 * @author Antoine Lavignotte
 * @version 1.0
 */
public class PlaylistCreation extends IpfhasWorkerPlaylistCreation implements GearmanFunction{

	private static final Logger logger = LoggerFactory.getLogger(PlaylistCreation.class);

	/**
	 * Data into a JSON Object
	 */
	private JSONObject dataJson;

	/**
	 * Video name
	 */
	private String videoName;

	/**
	 * Video Duration
	 */
	private float videoDuration;

	/**
	 * List of video shots
	 */
	private JSONArray listShots;

	/**
	 * List of the shots durations
	 */
	private JSONArray listDurations;
	
	

	/**
	 * M3U8 file
	 */
	private File m3u8File;

	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback)
			throws Exception {

		try {

			logger.info("----- in PlaylistCreation -----");

			// Data received
			dataJson = convertDataToJson(data);
			videoName = dataJson.get("videoName").toString();
			listShots = (JSONArray) dataJson.get("listShots");
			listDurations = (JSONArray) dataJson.get("listDurations");
			videoDuration = Float.parseFloat(dataJson.get("dataJson").toString());
		
			createM3U8();

			return null;
		}
		catch(Exception e) {
			logger.debug("Bug in work: {}", e);
			JSONObject error = new JSONObject();
			error.put("error", e.toString());
			return  error.toJSONString().getBytes(); 
		}	
	}

	/**
	 * Convert a byte[] data into JSONObject data
	 * @param data Data to convert into JSON
	 * @return Data converted into a JSONObject
	 */
	private JSONObject convertDataToJson(byte[] data) {
		try {
			logger.info("---- In convertDataToJson ----");

			String dataString = new String(data);
			Object obj = JSONValue.parse(dataString);
			JSONObject dataJsonObject = (JSONObject) obj;
			return dataJsonObject;
		}
		catch(Exception e) {
			logger.error("Bug in convertDataToJSON: {}", e);
			return null;
		}
	}

	/**
	 * M3U8 creation
	 */
	private void createM3U8() {

		try {
			String m3u8Destination = dataJson.get("m3u8Destination").toString();
			

			// M3U8 file creation
			m3u8File = new File(m3u8Destination + "/" + videoName + ".m3u8");
			m3u8File.createNewFile();

			BufferedWriter writer = null;

			String line1 = "#EXTM3U";
			String line2 = "#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=263851";
			String line3 = "gear1/prog_index.m3u8";
			
			/*String line2 = "#EXT-X-PLAYLIST-TYPE:VOD";
			String line3 = "#EXT-X-TARGETDURATION:" + videoDuration;
			String line4 = "#EXT-X-VERSION:3";
			String line5 = "#EXT-X-MEDIA-SEQUENCE:0";*/

			writer = new BufferedWriter(new FileWriter(m3u8File.toString(), true));
			writer.write(line1,0,line1.length());
			writer.newLine();
			writer.write(line2,0,line2.length());
			writer.newLine();
			writer.write(line3,0,line3.length());
			writer.newLine();

			for(int i=0; i<listShots.size();i++) {
				line1 = "#EXTINF:" + listDurations.get(i);
				writer.write(line1,0,line1.length());
				line2 = listLocationVideoShots.get(i).toString();
				writer.write(line2,0,line2.length());
			}

			if(writer != null) {
				writer.close();
			}

		}
		catch(Exception e) {
			logger.error("Bug in createM3U8: {}", e);
		}

	}
}
