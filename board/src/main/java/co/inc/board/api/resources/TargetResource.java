package co.inc.board.api.resources;

import co.inc.board.domain.business.TargetBusiness;
import co.inc.board.domain.entities.TwitterTarget;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/targets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TargetResource {
	
	private final TargetBusiness targetBusiness;
	
	public TargetResource(TargetBusiness targetBusiness) {
		this.targetBusiness = targetBusiness;
	}

	@GET
    @Path("/{twitterId}")
	public Response getSingleTarget(@PathParam("twitterId") String twitterId) {

		TwitterTarget target = targetBusiness.getSingleTarget(twitterId);
        Status statusCode = (target != null) ? Status.OK : Status.NOT_FOUND;
		return Response.status(statusCode).entity(target).build();
	}

	@GET
	public Response getAllTargets() {
		
		List<TwitterTarget> targets = targetBusiness.getAllTargets();
		return Response.status(Status.OK).entity(targets).build();
	}
}