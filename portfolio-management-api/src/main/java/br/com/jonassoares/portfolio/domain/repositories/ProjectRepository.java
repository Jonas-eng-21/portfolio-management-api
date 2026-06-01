package br.com.jonassoares.portfolio.domain.repositories;

import br.com.jonassoares.portfolio.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {

    @Query("SELECT p.status, COUNT(p) FROM Project p WHERE p.deleted = false GROUP BY p.status")
    List<Object[]> countProjectsByStatus();

    @Query("SELECT p.status, SUM(p.budget) FROM Project p WHERE p.deleted = false GROUP BY p.status")
    List<Object[]> sumBudgetByStatus();

    @Query("SELECT p FROM Project p WHERE p.status = br.com.jonassoares.portfolio.domain.enums.ProjectStatus.ENCERRADO AND p.deleted = false")
    List<Project> findClosedProjects();
}
