package crawler.facebook;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Version;
import com.restfb.types.Comment;
import com.restfb.types.Page;
import com.restfb.types.Post;

import crawler.entities.FacebookComment;
import crawler.entities.FacebookPage;
import crawler.entities.FacebookPost;
import crawler.entities.FacebookTarget;

public class FacebookCrawler {

	private final static String APP_ID = "756372024384811";
	private final static String APP_SECRET = "4f97cf98298f9abe86d99328bc7a8b64";

	private final FacebookMongoDAO dao;

	private FacebookClient client;

	public FacebookCrawler(MongoDatabase database, ObjectMapper objectMapper) {
		dao = new FacebookMongoDAO(database, objectMapper);
	}

	public void init() {
		AccessToken accessToken = new DefaultFacebookClient(Version.VERSION_2_2).obtainAppAccessToken(APP_ID, APP_SECRET);
		client = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET, Version.VERSION_2_2);

		List<FacebookTarget> targets = dao.getTargets();
		System.out.println(targets);
		for (FacebookTarget target : targets) {
			FacebookPage fbPage = getTargetPage(target);
			getTargetFeed(fbPage);
		}
	}

	//TODO Next Page URL
	private void getTargetFeed(FacebookPage fbPage) {
		Connection<Post> feed = client.fetchConnection(fbPage.getFacebookId() + "/feed", Post.class);
		List<Post> posts = feed.getData();
		
		for (int i = 0; i < posts.size() && posts != null; i++) {
			Post post = posts.get(i);
			FacebookPost fbPost = new FacebookPost(post.getId(), post.getMessage(), post.getCommentsCount(),
					post.getLikesCount(), post.getSharesCount(), post.getCreatedTime().getTime(), fbPage);
			dao.insertPost(fbPost);
			getPostComments(post, fbPost);
		}
	}

	//TODO Comments tienen comments... Recursivo
	private void getPostComments(Post post, FacebookPost fbPost) {
		if(post.getComments() != null){
			List<Comment> comments = post.getComments().getData();
			for (Comment comment : comments) {
				FacebookComment fbComment = new FacebookComment(comment.getId(), comment.getMessage(), comment.getLikeCount(),
						comment.getCreatedTime().getTime(), fbPost);
				dao.insertComment(fbComment);
			}			
		}
	}

	private FacebookPage getTargetPage(FacebookTarget target) {
		Page page = client.fetchObject(target.getId(), Page.class);
		FacebookPage fbPage = new FacebookPage(page.getId(), page.getName(), page.getLikes(), System.currentTimeMillis());
		dao.insertPage(fbPage);
		return fbPage;
	}
}
