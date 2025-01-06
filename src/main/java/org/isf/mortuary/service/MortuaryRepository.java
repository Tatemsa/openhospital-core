package org.isf.mortuary.service;

import org.isf.mortuary.model.Mortuary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortuaryRepository extends JpaRepository<Mortuary, Integer>, MortuaryRepositoryCustom {

}