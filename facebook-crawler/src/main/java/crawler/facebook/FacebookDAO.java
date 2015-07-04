package crawler.facebook;

import java.util.List;

import crawler.entities.FacebookComment;
import crawler.entities.FacebookPage;
import crawler.entities.FacebookPost;
import crawler.entities.FacebookTarget;

public interface FacebookDAO {
	
	public List<FacebookTarget> getTargets();
	
	public void insertPage(FacebookPage page);
	
	public void insertPost(FacebookPost fbPost);
	
	public void insertComment(FacebookComment fbComment);
}