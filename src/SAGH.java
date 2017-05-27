import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SAGH {
	
	public static class Track {
		int trackId, artistId, popularity;
		String trackname, artistName;
		
		public String toString(){
			return trackname + " by " + artistName + "," + " popularity: " + popularity;
		}
		
		public Track(int artistId, int trackId){
			this.trackId = trackId;
			this.artistId = artistId;
			this.popularity = 1;
		}
	}

	public static void main(String[] args) {
		List<Track> input = new ArrayList<>();
		// Example playlist from the pdf
		input.add(new Track(32, 239));
		input.add(new Track(610, 2766));
		input.add(new Track(610, 317));
		input.add(new Track(276, 2160));
		input.add(new Track(805, 540));
		input.add(new Track(138, 2479));
		input.add(new Track(805, 2509));
		for (Track t : new SAGH().recommend(input)) {
			System.out.println(t);
		}
	}

	public List<Track> recommend(List<Track> input) {
		// Find all artist IDs in question
		Set<Integer> artistIds = new HashSet<>();
		for (Track t : input) {
			artistIds.add(t.artistId);
		}

		// Maps trackId -> track object
		Map<Integer, Track> tracks = new TreeMap<>();

		// Fill tracks-map with tracks from playlist file, increase track's
		// popularity if already existing
		for (String line : getFileLines("playlists-integer-ids.txt")) {
			for (String track : line.split(" ")) {
				int artistId = Integer.parseInt(track.split(":")[0]);
				int trackId = Integer.parseInt(track.split(":")[1]);
				if (artistIds.contains(artistId)) {
					if (tracks.containsKey(trackId)) {
						tracks.get(trackId).popularity++;
					} else {
						tracks.put(trackId, new Track(artistId, trackId));
					}
				}
			}
		}

		List<Track> result = new ArrayList<>();
		for (Integer artist : artistIds) {
			result.addAll(
					tracks.values().stream()
					.filter((t) -> t.artistId == artist)
					.sorted((t1, t2) -> new Integer(t1.popularity).compareTo(t2.popularity) * -1)
					.limit(1)
					.collect(Collectors.toList()));
		}

		List<String> artistNames = getFileLines("artists-integer-ids.txt");
		List<String> trackNames = getFileLines("tracks-integer-ids.txt");
		for (Track t : result) {
			t.artistName = artistNames.get(t.artistId - 1);
			t.trackname = trackNames.get(t.trackId - 1);
		}
		result.sort((t1,t2) -> new Integer(t1.popularity).compareTo(t2.popularity) * -1);
		return result;
	}

	@SuppressWarnings("resource")
	private static List<String> getFileLines(String filename) {
		InputStream fileStream;
		try {
			fileStream = new FileInputStream(filename);
			return new BufferedReader(
					new InputStreamReader(fileStream))
					.lines()
					.collect(Collectors.toList());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}

	}
}
