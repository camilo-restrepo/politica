package co.inc.board.domain.business;

import java.util.List;

import co.inc.board.domain.entities.TwitterTarget;
import co.inc.board.persistence.daos.TargetDAO;

public class TargetBusiness {
	
	private final TargetDAO targetDAO;

	public TargetBusiness(TargetDAO targetDAO) {
		this.targetDAO = targetDAO;
	}

	public List<TwitterTarget> getAllTargets() {
		
		return targetDAO.getAllTargets();
	}	
}
