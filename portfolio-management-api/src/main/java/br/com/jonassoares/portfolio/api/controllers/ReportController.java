package br.com.jonassoares.portfolio.api.controllers;

import br.com.jonassoares.portfolio.api.dtos.report.PortfolioReportResponse;
import br.com.jonassoares.portfolio.domain.services.PortfolioReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PortfolioReportService portfolioReportService;

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioReportResponse> getPortfolioReport() {
        return ResponseEntity.ok(portfolioReportService.generateReport());
    }
}
