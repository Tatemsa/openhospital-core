package org.isf.mortuary.service;

import org.isf.mortuary.model.DeathReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeathReasonRepository extends JpaRepository<DeathReason, Integer> {

}
