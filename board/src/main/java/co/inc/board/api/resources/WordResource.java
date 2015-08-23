package co.inc.board.api.resources;

import co.inc.board.domain.business.TargetBusiness;
import co.inc.board.domain.business.WordBusiness;
import co.inc.board.domain.entities.CloudTag;
import co.inc.board.domain.entities.TwitterTarget;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/words")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WordResource {

    private final WordBusiness wordBusiness;
    private final TargetBusiness targetBusiness;

    public WordResource(WordBusiness wordBusiness, TargetBusiness targetBusiness) {
        this.wordBusiness = wordBusiness;
        this.targetBusiness = targetBusiness;
    }

    @GET
    public Response getAllTargetsCloudTags(@QueryParam("limit") @DefaultValue("10") int limit) {

        List<TwitterTarget> allTargets = targetBusiness.getAllTargets();
        List<CloudTag> allTargetsCloudTags = wordBusiness.getAllTargetsCloudTags(allTargets, limit);
        return Response.status(Response.Status.OK).entity(allTargetsCloudTags).build();
    }
}
