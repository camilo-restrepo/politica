package co.inc.board.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import co.inc.board.api.ws.BroadcastSocket;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("broadcast")
public class BroadcasterResource {

	private final ObjectMapper objectMapper;

	public BroadcasterResource(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@POST
	@Consumes("application/json")
	public void broadcast(Object data) throws Exception {

		String jsonData = objectMapper.writeValueAsString(data);
		BroadcastSocket.broadcast(jsonData);
	}

	@POST
	@Consumes("text/plain")
	public void broadcastString(String data) throws Exception {
//		System.out.println("Received Text: " + data);
		BroadcastSocket.broadcast(data);
	}
}