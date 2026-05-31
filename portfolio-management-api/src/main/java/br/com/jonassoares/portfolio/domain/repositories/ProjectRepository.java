package br.com.jonassoares.portfolio.domain.repositories;

import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
	
	@Query("SELECT p FROM Project p WHERE p.deleted = false AND " +
			"(CAST(:name AS string) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) AND " +
			"(:status IS NULL OR p.status = :status)")
	Page<Project> findWithFilters(
			@Param("name") String name,
			@Param("status") ProjectStatus status,
			Pageable pageable
	);
}
