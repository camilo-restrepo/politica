package co.inc.board.api.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import co.inc.board.domain.business.TargetBusiness;
import co.inc.board.domain.entities.TwitterTarget;

@Path("/targets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TargetResource {
	
	private final TargetBusiness targetBusiness;
	
	public TargetResource(TargetBusiness targetBusiness) {
		this.targetBusiness = targetBusiness;
	}

	@GET
	public Response getAllTargets() {
		
		List<TwitterTarget> targets = targetBusiness.getAllTargets();
		return Response.status(Status.OK).entity(targets).build();
	}
}