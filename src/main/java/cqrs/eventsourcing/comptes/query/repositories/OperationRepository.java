package cqrs.eventsourcing.comptes.query.repositories;

import cqrs.eventsourcing.comptes.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
