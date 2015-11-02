package co.inc.board.domain.business;

import co.inc.board.domain.entities.TwitterTarget;
import co.inc.board.persistence.daos.UsersDAO;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pisco on 10/30/15.
 */
public class UsersBusiness {

    private final UsersDAO usersDAO;
    private final TargetBusiness targetBusiness;

    public UsersBusiness(UsersDAO usersDAO, TargetBusiness targetBusiness) {
        this.usersDAO = usersDAO;
        this.targetBusiness = targetBusiness;
    }

    public List<Document> getAllCandidatesUserCount(){
        List<TwitterTarget> allTargets = targetBusiness.getAllTargets();
        List<Document> result = new ArrayList<>();
        for(TwitterTarget target : allTargets){
            long count = getCandidateUserCount(target.getTwitterId().getId());
            Document doc = new Document();
            doc.put("target", target.getTwitterId().getId());
            doc.put("count", count);
            result.add(doc);
        }
        return result;
    }

    public long getCandidateUserCount(String targetId){
        return usersDAO.getCandidateUsersCount(targetId);
    }

    public List<Document> getTargetUsers(String twitterId) {
        return usersDAO.getTargetUsers(twitterId);
    }

    public List<Document> getCreationSummary(){
        return usersDAO.getUserCreationDateSummary();
    }

    public List<Document> getUserCreationCandidate(String twitterId){
        return usersDAO.getUserCreationCandidate(twitterId);
    }
}
