package org.isf.mortuary.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Mortuary;
import org.isf.opd.model.Opd;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MortuaryIoOperations {

	private final MortuaryRepository mortuaryRepository;

	public MortuaryIoOperations(MortuaryRepository mortuaryRepository) {
		this.mortuaryRepository = mortuaryRepository;
	}

	/**
	 * Store the specified {@link Mortuary}.
	 * @param mortuary the death  to store.
	 * @return {@link Mortuary} if the {@link Mortuary} has been stored, null otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public Mortuary add(Mortuary mortuary) throws OHException {
		return mortuaryRepository.save(mortuary);
	}

	/**
	 * Get all the {@link Mortuary}s.
	 *
	 * @return a list of deaths.
	 * @throws OHException if an error occurs retrieving the deaths.
	 */
	public List<Mortuary> getAll() throws OHException {
		return mortuaryRepository.findAll();
	}

	/**
	 *
	 * method that update an existing {@link Mortuary} in the db
	 *
	 * @param mortuary - the {@link Mortuary} to update
	 * @return {@link Mortuary} has been updated
	 * @throws OHException
	 */
	public Mortuary update(Mortuary mortuary) throws OHException{
		return mortuaryRepository.save(mortuary);
	}

	/**
	 * method that delete a death
	 *
	 * @param mortuary
	 * @throws OHException
	 */
	public void delete(Mortuary mortuary) throws OHException {
		mortuaryRepository.delete(mortuary);
	}

	public List<Mortuary> getMortuariesWhereData(
		String patientName,
		String deathReason,
		String ward,
		LocalDateTime movFrom,
		LocalDateTime movTo,
		Pageable pageable
	) throws OHServiceException {
		movFrom = movFrom.withHour(0).withMinute(0);
		movTo = movTo.withHour(23).withMinute(59);

		return mortuaryRepository.findMortuariesWhereData(
			patientName,
			deathReason,
			ward,
			TimeTools.truncateToSeconds(movFrom),
			TimeTools.truncateToSeconds(movTo),
			pageable
		);
	}
}
