package co.inc.board.api.resources;

import co.inc.board.domain.business.TargetBusiness;
import co.inc.board.domain.business.TweetBusiness;
import co.inc.board.domain.entities.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tweets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TweetResource {

    private final TweetBusiness tweetBusiness;
    private final TargetBusiness targetBusiness;

    public TweetResource(TweetBusiness tweetBusiness, TargetBusiness targetBusiness) {
        this.tweetBusiness = tweetBusiness;
        this.targetBusiness = targetBusiness;
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
    @Path("{twitterId}/stats")
    public Response getTweetStats(@PathParam("twitterId") String twitterId) {

        TweetStats tweetStats = tweetBusiness.getTweetStats(twitterId);
        return Response.status(Response.Status.OK).entity(tweetStats).build();
    }

    @GET
    @Path("{twitterId}/polarity")
    public Response getCandidatePolarity(@PathParam("twitterId") String twitterId, @QueryParam("time") String time) {

        Polarity candidatePolarity;

        if (time.equalsIgnoreCase(TimeEnum.DAY.getValue())) {
            candidatePolarity = tweetBusiness.getCandidatePolarityToday(twitterId);
        } else {
            // return monthly polarity by default
            candidatePolarity = tweetBusiness.getCandidatePolarityMonth(twitterId);
        }

        return Response.status(Response.Status.OK).entity(candidatePolarity).build();
    }

    @GET
    @Path("/polarity")
    public Response getAllTargetsPolarity(@QueryParam("time") String time) {

        List<Polarity> polarityList;

        List<TwitterTarget> allTargets = targetBusiness.getAllTargets();

        if (time.equalsIgnoreCase(TimeEnum.DAY.getValue())) {
            polarityList = tweetBusiness.getAllTargetsPolarityToday(allTargets);
        } else {
            // return monthly polarity by default
            polarityList = tweetBusiness.getAllTargetsPolarityLastMonth(allTargets);
        }

        return Response.status(Response.Status.OK).entity(polarityList).build();
    }

    @GET
    @Path("/count")
    public Response getAllTweetsCount(){
        return Response.status(Response.Status.OK).entity(tweetBusiness.getAllTweetsCount()).build();
    }

    @GET
    @Path("{twitterId}/last")
    public Response getLastTweetsCandidate(@PathParam("twitterId") String twitterId){
        List<String> tweets = tweetBusiness.getLastTweetsCandidate(twitterId);
        return Response.status(Response.Status.OK).entity(tweets).build();
    }

    @GET
    @Path("/prediction/{prediction}/last")
    public Response getLastTweetsPolarity(@PathParam("prediction") String prediction){
        List<String> tweets = tweetBusiness.getLastTweetsPolarity(prediction);
        return Response.status(Response.Status.OK).entity(tweets).build();
    }

    @GET
    @Path("/location")
    public Response getTweetsLocation(){
        List<List<Double>> locations = tweetBusiness.getTweetsLocation();
        return Response.status(Response.Status.OK).entity(locations).build();
    }
}
