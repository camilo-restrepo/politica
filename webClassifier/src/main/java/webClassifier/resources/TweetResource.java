package webClassifier.resources;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import webClassifier.daos.TweetDAO;
import webClassifier.entities.ClassifiedTweet;
import webClassifier.entities.Tweet;

@Path("/tweets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TweetResource {

	private final TweetDAO dao;

	public TweetResource(TweetDAO dao) {
		this.dao = dao;
	}

	@GET
	public Response getTweets(@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("10") int limit) {
		List<Tweet> tweets = dao.getTweets(offset, limit);
		return Response.status(Status.OK).entity(tweets).build();
	}
	
	@PUT
	@Path("/{id}")
	public Response updateTweet(@PathParam("id") String id, @Valid ClassifiedTweet classifiedTweet){
		dao.updateTweet(classifiedTweet);
		return Response.status(Status.OK).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteTweet(@PathParam("id") String id){
		dao.deleteTweet(id);
		return Response.status(Status.OK).build();
	}
}
