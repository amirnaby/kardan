package com.niam.kardan.repository;

import com.niam.kardan.model.PartOperationTask;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartOperationTaskRepository extends JpaRepository<PartOperationTask, Long>, JpaSpecificationExecutor<PartOperationTask> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from PartOperationTask t where t.id = :id")
    Optional<PartOperationTask> findByIdForUpdate(@Param("id") Long id);
    List<PartOperationTask> findByTargetMachineIdAndTaskStatusCode(Long machineId, String statusCode);
    List<PartOperationTask> findByTaskStatusCode(String statusCode);
}