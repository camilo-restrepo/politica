package co.inc.board.api.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import co.inc.board.domain.business.ScoreBusiness;
import co.inc.board.domain.entities.TargetScore;

@Path("/scores")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ScoreResource {

	private final ScoreBusiness business;
	
	public ScoreResource(ScoreBusiness business){
		this.business = business;
	}
	
	@GET
	public Response getScores(){
		List<TargetScore> scores = business.getScores();
		return Response.status(Status.OK).entity(scores).build();
	}
}
