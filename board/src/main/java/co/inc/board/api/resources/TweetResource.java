package co.inc.board.api.resources;

import co.inc.board.domain.business.TweetBusiness;
import co.inc.board.domain.entities.MapCoordinate;
import co.inc.board.domain.entities.PolarityPerDay;
import co.inc.board.domain.entities.TweetPerDay;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tweets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TweetResource {

    private final TweetBusiness tweetBusiness;

    public TweetResource(TweetBusiness tweetBusiness) {
        this.tweetBusiness = tweetBusiness;
    }

    @GET
    @Path("{twitterId}/map")
    public Response getMapFromTweetsLastMonth(@PathParam("twitterId") String twitterId) {

        List<MapCoordinate> coordinatesList = tweetBusiness.getMapFromTweetsLastMonth(twitterId);
        return Response.status(Response.Status.OK).entity(coordinatesList).build();
    }

    @GET
    @Path("{twitterId}/day")
    public Response getTweetsPerDayLastMonth(@PathParam("twitterId") String twitterId) {

        List<TweetPerDay> tweetsPerDayList = tweetBusiness.getTweetsPerDayLastMonth(twitterId);
        return Response.status(Response.Status.OK).entity(tweetsPerDayList).build();
    }

    @GET
    @Path("{twitterId}/polarity")
    public Response getPolarityLastMonth(@PathParam("twitterId") String twitterId) {

        List<PolarityPerDay> polarityPerDayList = tweetBusiness.getPolarityLastMonth(twitterId);
        return Response.status(Response.Status.OK).entity(polarityPerDayList).build();
    }
}
