package com.domain.system.repository.postgresql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.domain.system.models.postgresql.Obra;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

}
