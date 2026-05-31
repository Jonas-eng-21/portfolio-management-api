package br.com.jonassoares.portfolio.domain.repositories;

import br.com.jonassoares.portfolio.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
}
