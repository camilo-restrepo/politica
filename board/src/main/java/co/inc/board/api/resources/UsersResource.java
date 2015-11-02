package co.inc.board.api.resources;

import co.inc.board.domain.business.UsersBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Pisco on 10/30/15.
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    private final UsersBusiness usersBusiness;

    public UsersResource(UsersBusiness usersBusiness) {
        this.usersBusiness = usersBusiness;
    }

    @GET
    @Path("/count")
    public Response getAllUserCount(@QueryParam("limit") @DefaultValue("10") int limit) {
        return Response.status(Response.Status.OK).entity(usersBusiness.getAllCandidatesUserCount()).build();
    }

    @GET
    @Path("{twitterId}/count")
    public Response getTargetUsersCount(@PathParam("twitterId") String twitterId){
        return Response.status(Response.Status.OK).entity(usersBusiness.getTargetUsers(twitterId)).build();
    }

    @GET
    @Path("/time")
    public Response getCreationSummary(){
        return Response.status(Response.Status.OK).entity(usersBusiness.getCreationSummary()).build();
    }

    @GET
    @Path("{twitterId}/time")
    public Response getUserCreationCandidate(@PathParam("twitterId") String twitterId){
        return Response.status(Response.Status.OK).entity(usersBusiness.getUserCreationCandidate(twitterId)).build();
    }

    @GET
    @Path("/venn")
    public Response getVennData(){
        return Response.status(Response.Status.OK).entity(usersBusiness.getVennData()).build();
    }
}
