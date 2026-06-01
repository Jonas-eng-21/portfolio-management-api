package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectStatusTransitionValidator {

    private static final List<ProjectStatus> STATUS_FLOW = List.of(
            ProjectStatus.EM_ANALISE,
            ProjectStatus.ANALISE_REALIZADA,
            ProjectStatus.ANALISE_APROVADA,
            ProjectStatus.INICIADO,
            ProjectStatus.PLANEJADO,
            ProjectStatus.EM_ANDAMENTO,
            ProjectStatus.ENCERRADO
    );

    public void validate(ProjectStatus currentStatus, ProjectStatus nextStatus) {
        if (currentStatus == nextStatus || nextStatus == ProjectStatus.CANCELADO) {
            return;
        }

        int currentIndex = STATUS_FLOW.indexOf(currentStatus);
        int nextIndex = STATUS_FLOW.indexOf(nextStatus);

        if (currentIndex == -1 || nextIndex == -1 || nextIndex != currentIndex + 1) {
            throw new BusinessRuleException(String.format("Invalid status transition from %s to %s", currentStatus, nextStatus));
        }
    }
}
