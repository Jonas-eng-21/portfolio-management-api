package br.com.jonassoares.portfolio.domain.specifications;

import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> withFilters(String name, ProjectStatus status, Long managerId, RiskLevel riskLevel) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (managerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("managerId"), managerId));
            }

            if (riskLevel != null) {
                predicates.add(criteriaBuilder.equal(root.get("riskLevel"), riskLevel));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
