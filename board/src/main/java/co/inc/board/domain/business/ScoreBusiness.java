package co.inc.board.domain.business;

import java.util.ArrayList;
import java.util.List;

import co.inc.board.domain.entities.Score;
import co.inc.board.domain.entities.TargetScore;
import co.inc.board.domain.entities.TwitterTarget;
import co.inc.board.persistence.daos.ScoreDAO;

public class ScoreBusiness {
	
	private final ScoreDAO dao;
	private final TargetBusiness targetBusiness;
	
	public ScoreBusiness(ScoreDAO dao, TargetBusiness targetBusiness){
		this.dao = dao;
		this.targetBusiness = targetBusiness;
	}

	public List<TargetScore> getScores() {
		List<TwitterTarget> targets = targetBusiness.getAllTargets();
		List<TargetScore> scores = new ArrayList<>();
		for(TwitterTarget target : targets){
			List<Score> targetScores = dao.getTargetScores(target.getTwitterId().getId());
			scores.add(new TargetScore(target.getTwitterId().getId(), targetScores));
		}
		return scores;
	}	
}
