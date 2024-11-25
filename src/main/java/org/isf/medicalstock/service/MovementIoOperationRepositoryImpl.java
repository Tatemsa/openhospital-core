/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.medicalstock.service;

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

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medtype.model.MedicalType;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.PageRequest;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MovementIoOperationRepositoryImpl implements MovementIoOperationRepositoryCustom {

	private static final String WARD = "ward";
	private static final String DATE = "date";
	private static final String CODE = "code";
	private static final String REF_NO = "refNo";
	private static final String MEDICAL = "medical";
	private static final String LOT = "lot";
	private static final String TYPE = "type";
	private static final String DESCRIPTION = "description";

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findMovementWhereDatesAndId(String wardId, LocalDateTime dateFrom, LocalDateTime dateTo) {
		return getMovementWhereDatesAndId(wardId, dateFrom, dateTo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findMovementWhereData(
					Integer medicalCode,
					String medicalType,
					String wardId,
					String movType,
					LocalDateTime movFrom,
					LocalDateTime movTo,
					LocalDateTime lotPrepFrom,
					LocalDateTime lotPrepTo,
					LocalDateTime lotDueFrom,
					LocalDateTime lotDueTo) {
		return getMovementWhereData(medicalCode, medicalType, wardId, movType, movFrom, movTo,
						lotPrepFrom, lotPrepTo, lotDueFrom, lotDueTo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findMovementWhereData(
		Integer medicalCode,
		String medicalType,
		String wardId,
		String movType,
		LocalDateTime movFrom,
		LocalDateTime movTo,
		LocalDateTime lotPrepFrom,
		LocalDateTime lotPrepTo,
		LocalDateTime lotDueFrom,
		LocalDateTime lotDueTo,
		Pageable pageable) {
		return getMovementWhereData(medicalCode, medicalType, wardId, movType, movFrom, movTo,
			lotPrepFrom, lotPrepTo, lotDueFrom, lotDueTo, pageable);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findMovementForPrint(
					String medicalDescription,
					String medicalTypeCode,
					String wardId,
					String movType,
					LocalDateTime movFrom,
					LocalDateTime movTo,
					String lotCode,
					MovementOrder order) {
		return getMovementForPrint(medicalDescription, medicalTypeCode, wardId, movType, movFrom, movTo,
						lotCode, order);
	}

	private List<Integer> getMovementWhereDatesAndId(String wardId, LocalDateTime dateFrom, LocalDateTime dateTo) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Movement> root = query.from(Movement.class);
		query.select(root.<Integer> get(CODE));
		List<Predicate> predicates = new ArrayList<>();

		if ((dateFrom != null) && (dateTo != null)) {
			predicates.add(builder.between(root.<LocalDateTime> get(DATE), TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo)));
		}
		if (wardId != null && !wardId.equals("")) {
			predicates.add(builder.equal(root.<Ward> get(WARD).<String> get(CODE), wardId));
		}

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.desc(root.get(DATE)));
		orderList.add(builder.desc(root.get(REF_NO)));
		query.where(predicates.toArray(new Predicate[] {})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}

	private List<Integer> getMovementWhereData(
					Integer medicalCode,
					String medicalType,
					String wardId,
					String movType,
					LocalDateTime movFrom,
					LocalDateTime movTo,
					LocalDateTime lotPrepFrom,
					LocalDateTime lotPrepTo,
					LocalDateTime lotDueFrom,
					LocalDateTime lotDueTo) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Movement> root = query.from(Movement.class);
		query.select(root.<Integer> get(CODE));
		List<Predicate> predicates = new ArrayList<>();

		if (medicalCode != null) {
			predicates.add(builder.equal(root.<Medical> get(MEDICAL).<String> get(CODE), medicalCode));
		}
		if (medicalType != null) {
			predicates.add(builder.equal(root.<Medical> get(MEDICAL).<MedicalType> get(TYPE).<String> get(CODE), medicalType));
		}
		if ((movFrom != null) && (movTo != null)) {
			predicates.add(builder.between(root.<LocalDateTime> get(DATE), TimeTools.getBeginningOfDay(movFrom), TimeTools.getBeginningOfNextDay(movTo)));
		}
		if ((lotPrepFrom != null) && (lotPrepTo != null)) {
			predicates.add(builder.between(root.<Lot> get(LOT).<LocalDateTime> get("preparationDate"), TimeTools.getBeginningOfDay(lotPrepFrom),
							TimeTools.getBeginningOfNextDay(lotPrepTo)));
		}
		if ((lotDueFrom != null) && (lotDueTo != null)) {
			predicates.add(builder.between(root.<Lot> get(LOT).<LocalDateTime> get("dueDate"), TimeTools.getBeginningOfDay(lotDueFrom),
							TimeTools.getBeginningOfNextDay(lotDueTo)));
		}
		if ("+".equals(movType)) {
			predicates.add(builder.equal(root.<MovementType> get(TYPE).<String> get(TYPE), movType));

		} else if ("-".equals(movType)) {
			predicates.add(builder.equal(root.<MovementType> get(TYPE).<String> get(TYPE), movType));

		} else if (movType != null) {
			predicates.add(builder.equal(root.<MovementType> get(TYPE).<String> get(CODE), movType));
		}
		if (wardId != null) {
			predicates.add(builder.equal(root.<Ward> get(WARD).<String> get(CODE), wardId));
		}

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.desc(root.get(CODE)));
		orderList.add(builder.desc(root.get(REF_NO)));
		query.where(predicates.toArray(new Predicate[] {})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}

	private List<Integer> getMovementWhereData(
		Integer medicalCode,
		String medicalType,
		String wardId,
		String movType,
		LocalDateTime movFrom,
		LocalDateTime movTo,
		LocalDateTime lotPrepFrom,
		LocalDateTime lotPrepTo,
		LocalDateTime lotDueFrom,
		LocalDateTime lotDueTo,
		Pageable pageable) {  // Ajouter Pageable comme paramètre
		// Créer le CriteriaBuilder et CriteriaQuery
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Movement> root = query.from(Movement.class);
		query.select(root.<Integer> get(CODE));

		// Liste des prédicats (filtres)
		List<Predicate> predicates = new ArrayList<>();

		// Ajouter les conditions de filtrage
		if (medicalCode != null) {
			predicates.add(builder.equal(root.<Medical> get(MEDICAL).<String> get(CODE), medicalCode));
		}
		if (medicalType != null) {
			predicates.add(builder.equal(root.<Medical> get(MEDICAL).<MedicalType> get(TYPE).<String> get(CODE), medicalType));
		}
		if ((movFrom != null) && (movTo != null)) {
			predicates.add(builder.between(root.<LocalDateTime> get(DATE), TimeTools.getBeginningOfDay(movFrom), TimeTools.getBeginningOfNextDay(movTo)));
		}
		if ((lotPrepFrom != null) && (lotPrepTo != null)) {
			predicates.add(builder.between(root.<Lot> get(LOT).<LocalDateTime> get("preparationDate"), TimeTools.getBeginningOfDay(lotPrepFrom),
				TimeTools.getBeginningOfNextDay(lotPrepTo)));
		}
		if ((lotDueFrom != null) && (lotDueTo != null)) {
			predicates.add(builder.between(root.<Lot> get(LOT).<LocalDateTime> get("dueDate"), TimeTools.getBeginningOfDay(lotDueFrom),
				TimeTools.getBeginningOfNextDay(lotDueTo)));
		}
		if ("+".equals(movType)) {
			predicates.add(builder.equal(root.<MovementType> get(TYPE).<String> get(TYPE), movType));
		} else if ("-".equals(movType)) {
			predicates.add(builder.equal(root.<MovementType> get(TYPE).<String> get(TYPE), movType));
		} else if (movType != null) {
			predicates.add(builder.equal(root.<MovementType> get(TYPE).<String> get(CODE), movType));
		}
		if (wardId != null) {
			predicates.add(builder.equal(root.<Ward> get(WARD).<String> get(CODE), wardId));
		}

		// Appliquer le tri via Pageable
		List<Order> orderList = new ArrayList<>();
		if (pageable.getSort() != null) {
			pageable.getSort().forEach(order -> {
				if (order.isAscending()) {
					orderList.add(builder.asc(root.get(order.getProperty())));
				} else {
					orderList.add(builder.desc(root.get(order.getProperty())));
				}
			});
		} else {
			// Tri par défaut
			orderList.add(builder.desc(root.get(CODE)));
			orderList.add(builder.desc(root.get(REF_NO)));
		}

		// Appliquer les conditions et les ordres à la requête
		query.where(predicates.toArray(new Predicate[0])).orderBy(orderList);

		// Créer la requête paginée
		TypedQuery<Integer> typedQuery = entityManager.createQuery(query);

		// Pagination : Calculer l'index du premier résultat et la taille de la page
		int firstResult = pageable.getPageNumber() * pageable.getPageSize();
		typedQuery.setFirstResult(firstResult);
		typedQuery.setMaxResults(pageable.getPageSize());

		// Exécuter la requête et obtenir la liste des résultats
		List<Integer> result = typedQuery.getResultList();

		return result;
	}


	private List<Integer> getMovementForPrint(
					String medicalDescription,
					String medicalTypeCode,
					String wardId,
					String movType,
					LocalDateTime movFrom,
					LocalDateTime movTo,
					String lotCode,
					MovementOrder order) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Movement> root = query.from(Movement.class);
		query.select(root.<Integer> get(CODE));
		List<Predicate> predicates = new ArrayList<>();

		if (medicalDescription != null) {
			predicates.add(builder.equal(root.<Medical> get(MEDICAL).<String> get(DESCRIPTION), medicalDescription));
		}
		if (medicalTypeCode != null) {
			predicates.add(builder.equal(root.<Medical> get(MEDICAL).<MedicalType> get(TYPE).<String> get(CODE), medicalTypeCode));
		}
		if (lotCode != null) {
			predicates.add(builder.equal(root.<Ward> get(LOT).<String> get(CODE), lotCode));
		}
		if ((movFrom != null) && (movTo != null)) {
			predicates.add(builder.between(root.<LocalDateTime> get(DATE), TimeTools.getBeginningOfDay(movFrom), TimeTools.getBeginningOfNextDay(movTo)));
		}
		if (movType != null) {
			predicates.add(builder.equal(root.<MedicalType> get(TYPE).<String> get(CODE), movType));
		}
		if (wardId != null) {
			predicates.add(builder.equal(root.<Ward> get(WARD).<String> get(CODE), wardId));
		}
		List<Order> orderList = new ArrayList<>();
		switch (order) {
		case DATE:
			orderList.add(builder.desc(root.get(DATE)));
			orderList.add(builder.desc(root.get(REF_NO)));
			break;
		case WARD:
			orderList.add(builder.desc(root.get(REF_NO)));
			orderList.add(builder.desc(root.<Ward> get(WARD).get(DESCRIPTION)));
			break;
		case PHARMACEUTICAL_TYPE:
			orderList.add(builder.desc(root.get(REF_NO)));
			orderList.add(builder.asc(root.<Medical> get(MEDICAL).<MedicalType> get(TYPE)));
			orderList.add(builder.asc(root.<Medical> get(MEDICAL).<MedicalType> get(TYPE).get(DESCRIPTION)));
			break;
		case TYPE:
			orderList.add(builder.desc(root.get(REF_NO)));
			orderList.add(builder.asc(root.<MovementType> get(TYPE).<MedicalType> get(DESCRIPTION)));
			break;
		}
		query.where(predicates.toArray(new Predicate[] {})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}

}
