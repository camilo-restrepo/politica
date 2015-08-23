package co.inc.board.api.resources;

import co.inc.board.domain.business.TargetBusiness;
import co.inc.board.domain.business.TweetBusiness;
import co.inc.board.domain.entities.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
    @Path("{twitterId}/polarity")
    public Response getCandidatePolarity(@PathParam("twitterId") String twitterId, @QueryParam("time") String time) {

        Polarity candidatePolarity = null;

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

        List<Polarity> polarityList = new ArrayList<Polarity>();

        List<TwitterTarget> allTargets = targetBusiness.getAllTargets();

        if (time.equalsIgnoreCase(TimeEnum.DAY.getValue())) {
            polarityList = tweetBusiness.getAllTargetsPolarityToday(allTargets);
        } else {
            // return monthly polarity by default
            polarityList = tweetBusiness.getAllTargetsPolarityLastMonth(allTargets);
        }

        return Response.status(Response.Status.OK).entity(polarityList).build();
    }
}
