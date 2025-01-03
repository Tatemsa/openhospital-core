package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.isf.mortuary.model.Mortuary;
import org.isf.patient.model.Patient;
import org.springframework.data.domain.Pageable;

public interface MortuaryRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Mortuary> findMortuariesWhereData(
		String patientName,
		String deathReason,
		String ward,
		LocalDateTime movFrom,
		LocalDateTime movTo,
		Pageable pageable) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Mortuary> query = builder.createQuery(Mortuary.class);
		Root<Mortuary> root = query.from(Mortuary.class);

		List<Predicate> predicates = new ArrayList<>();

		if (patientName != null) {
			predicates.add(builder.equal(root.<Patient>get(PATIENT).<String>get(CODE), medicalCode));
		}
		if (medicalType != null) {
			predicates.add(builder.equal(root.<Medical>get(MEDICAL).<MedicalType>get(TYPE).<String>get(CODE), medicalType));
		}
		if ((movFrom != null) && (movTo != null)) {
			predicates.add(builder.between(root.<LocalDateTime>get(DATE), TimeTools.getBeginningOfDay(movFrom), TimeTools.getBeginningOfNextDay(movTo)));
		}
		if ((lotPrepFrom != null) && (lotPrepTo != null)) {
			predicates.add(builder.between(root.<Lot>get(LOT).<LocalDateTime>get("preparationDate"),
				TimeTools.getBeginningOfDay(lotPrepFrom), TimeTools.getBeginningOfNextDay(lotPrepTo)));
		}
		if ((lotDueFrom != null) && (lotDueTo != null)) {
			predicates.add(builder.between(root.<Lot>get(LOT).<LocalDateTime>get("dueDate"),
				TimeTools.getBeginningOfDay(lotDueFrom), TimeTools.getBeginningOfNextDay(lotDueTo)));
		}
		if ("+".equals(movType)) {
			predicates.add(builder.equal(root.<MovementType>get(TYPE).<String>get(TYPE), movType));
		} else if ("-".equals(movType)) {
			predicates.add(builder.equal(root.<MovementType>get(TYPE).<String>get(TYPE), movType));
		} else if (movType != null) {
			predicates.add(builder.equal(root.<MovementType>get(TYPE).<String>get(CODE), movType));
		}
		if (wardId != null) {
			predicates.add(builder.equal(root.<Ward>get(WARD).<String>get(CODE), wardId));
		}

		List<Order> orderList = new ArrayList<>();

		orderList.add(builder.desc(root.<LocalDateTime>get(DATE)));

		query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(orderList);

		TypedQuery<Movement> typedQuery = entityManager.createQuery(query);

		int firstResult = pageable.getPageNumber() * pageable.getPageSize();
		typedQuery.setFirstResult(firstResult);
		typedQuery.setMaxResults(pageable.getPageSize());

		return typedQuery.getResultList();
	}
}
