package co.inc.board.api.ws;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.board.Board;

public class BroadcastSocket extends WebSocketAdapter {

	private static final Logger log = LoggerFactory.getLogger(Board.class);

	private static Set<Session> sessions = new CopyOnWriteArraySet<Session>();

	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		sessions.add(session);
		log.info("Socket Connected: {}", Integer.toHexString(session.hashCode()));
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		sessions.remove(getSession());
		super.onWebSocketClose(statusCode, reason);
		log.info("Socket Closed: [{}] {}", statusCode, reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		log.error("Websocket error", cause);
	}

	@Override
	public void onWebSocketText(String message) {
		log.info("Got text {} from {}", message, Integer.toHexString(getSession().hashCode()));
	}

	public static void broadcast(String msg) {
		sessions.forEach(session -> {
			try {
				session.getRemote().sendString(msg);
			} catch (IOException e) {
				log.error("Problem broadcasting message", e);
			}
		});
	}
}