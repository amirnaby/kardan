package com.niam.kardan.repository;

import com.niam.kardan.model.OperatorMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatorMachineRepository extends JpaRepository<OperatorMachine, Long>, JpaSpecificationExecutor<OperatorMachine> {
    List<OperatorMachine> findByOperatorIdAndUnassignedAtIsNull(Long operatorId);
    boolean existsByOperatorIdAndMachineIdAndUnassignedAtIsNull(Long operatorId, Long machineId);
}