package com.niam.kardan.repository;

import com.niam.kardan.model.StopReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StopReasonRepository extends JpaRepository<StopReason, Long>, JpaSpecificationExecutor<StopReason> {
}