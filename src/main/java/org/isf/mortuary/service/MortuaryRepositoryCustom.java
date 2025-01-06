package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.mortuary.model.Mortuary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface MortuaryRepositoryCustom {

	List<Mortuary> getMortuariesWhereData(
		String patientName,
		String deathReason,
		String ward,
		LocalDateTime movFrom,
		LocalDateTime movTo
	);
}