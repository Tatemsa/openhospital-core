package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.isf.medtype.model.MedicalType;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Mortuary;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MortuaryIoOperationsImpl implements MortuaryRepositoryCustom{

	private static final String PATIENT = "patient";
	private static final String NAME = "name";
	private static final String DEATHREASON = "deathReason";
	private static final String DESC = "description";
	private static final String WARD = "ward";
	private static final String CODE = "code";
	private static final String DATETO = "enteredDate";
	private static final String DATEFROM = "releaseDate";

	@PersistenceContext
	private EntityManager entityManager;


	public List<Mortuary> getMortuariesWhereData(
		String patientName,
		String deathReason,
		String ward,
		LocalDateTime movFrom,
		LocalDateTime movTo
	) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Mortuary> query = builder.createQuery(Mortuary.class);
		Root<Mortuary> root = query.from(Mortuary.class);

		List<Predicate> predicates = new ArrayList<>();

		if (patientName != null) {
			predicates.add(builder.equal(root.<Mortuary> get(PATIENT).<String> get(NAME), patientName));
		}
		if (deathReason != null) {
			predicates.add(builder.equal(root.<DeathReason> get(DEATHREASON).<MedicalType> get(DESC), deathReason));
		}
		if (ward != null) {
			predicates.add(builder.equal(root.<Ward> get(WARD).<String> get(CODE), ward));
		}

		List<Order> orderList = new ArrayList<>();

		orderList.add(builder.desc(root.<LocalDateTime>get(DATEFROM)));

		query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(orderList);

		TypedQuery<Mortuary> typedQuery = entityManager.createQuery(query);

//		int firstResult = pageable.getPageNumber() * pageable.getPageSize();
//		typedQuery.setFirstResult(firstResult);
//		typedQuery.setMaxResults(pageable.getPageSize());

		return typedQuery.getResultList();
	}
}
