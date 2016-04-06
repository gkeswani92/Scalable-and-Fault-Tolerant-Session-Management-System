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
	
	public LocationMetadata(String locationData){
		this.wqaddress = new ArrayList<String>();
		String[] locations = locationData.split(":");
		for(String location: locations){
			this.wqaddress.add(location);
		}
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
		StringBuilder wqList = new StringBuilder();
		for(String s: this.wqaddress){
			wqList.append(s + ":");
	    }
	    return new String(wqList.deleteCharAt(wqList.length() - 1));
	}
}
