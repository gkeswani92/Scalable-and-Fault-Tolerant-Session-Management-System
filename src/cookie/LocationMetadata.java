package cookie;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the location metadata (mainly present for project 2)
 */
public class LocationMetadata implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<String> wqaddress;
	
	public LocationMetadata(){
		wqaddress = new ArrayList<String>();
	}
	
	public LocationMetadata(List<String> wqaddress){
		wqaddress = new ArrayList<String>();
		this.wqaddress.addAll(wqaddress);
	}
	
	/**
	 * @return the wqaddress
	 */
	public List<String> getWqaddress() {
		return wqaddress;
	}
	
	/**
	 * @param wqaddress the wqaddress to set
	 */
	public void setWqaddress(List<String> wqaddress) {
		this.wqaddress = wqaddress;
	}
	
	public String toString(){
		String output = "";
		for(String s: this.wqaddress){
			output += s;
		}
		return output;
	}
}
