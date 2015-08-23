package co.inc.board.domain.business;

import co.inc.board.domain.entities.CloudTag;
import co.inc.board.domain.entities.TwitterTarget;
import co.inc.board.persistence.daos.WordDAO;

import java.util.ArrayList;
import java.util.List;

public class WordBusiness {

    private final WordDAO wordDAO;

    public WordBusiness(WordDAO wordDAO) {
        this.wordDAO = wordDAO;
    }

    public List<CloudTag> getAllTargetsCloudTags(List<TwitterTarget> allTargets, int limit) {

        List<CloudTag> cloudTagList = new ArrayList<CloudTag>();

        for (TwitterTarget target : allTargets) {
            cloudTagList.add(wordDAO.getTargetCloudTag(target.getTwitterId().getId(), limit));
        }

        return cloudTagList;
    }
}
