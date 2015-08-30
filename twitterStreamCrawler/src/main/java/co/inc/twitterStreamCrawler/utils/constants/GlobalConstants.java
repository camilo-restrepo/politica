package co.inc.twitterStreamCrawler.utils.constants;

import java.util.regex.Pattern;

public class GlobalConstants {
	public final static String BOARD_URL = "http://localhost:9001/board/api/broadcast";
	public static final Pattern UNDESIRABLES = Pattern.compile("[\\d+\\]\\[\\+(){},.;¡!¿“”?/\\-<>%\r\n\\|\\.,;:\u2026►\"]");
	public static final Pattern SPACE = Pattern.compile(" +");

}
