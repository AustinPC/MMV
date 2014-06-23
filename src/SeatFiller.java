import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class SeatFiller {
	
	private int middle;

	public SeatFiller(int mid){
		middle = mid;
		
	}
	
	public void createMap(){
		try {
			File file = new File(
					"/Users/austin2/Documents/workspace/MMV/src/map.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i=0; i <= DisplayRenderer.imageHeight; i++){
				for (int j=0; j<=DisplayRenderer.imageWidth; j++){
					if (j < middle){
						bw.write("r");
					}
					else if (j > middle) {
						bw.write("g");
					}
				}
				bw.write("\n");
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("can't write to usedcommands.txt...");
		}
	}
	
}
