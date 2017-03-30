package speedyrest.entities;

import java.util.HashMap;

import redis.clients.johm.CollectionMap;
import redis.clients.johm.Id;
import redis.clients.johm.Model;

@Model
public class SpeedyHeaders {
	
    @Id
    private int id;

	@CollectionMap(key = String.class, value = String.class)
	private HashMap<String, String> speedyHeaders;

	public SpeedyHeaders () {
		this.speedyHeaders = new HashMap<>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void addHeader (String key, String value) {
		this.speedyHeaders.put(key, value);
	}
	
	public String getHeader (String key) {
		return this.speedyHeaders.get(key);
	}
	
	public HashMap<String, String> getHeaders () {
		return this.speedyHeaders;
	}
}
