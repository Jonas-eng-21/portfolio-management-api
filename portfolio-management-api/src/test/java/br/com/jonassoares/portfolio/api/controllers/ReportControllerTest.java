package br.com.jonassoares.portfolio.api.controllers;

import br.com.jonassoares.portfolio.api.dtos.report.PortfolioReportResponse;
import br.com.jonassoares.portfolio.domain.services.PortfolioReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioReportService portfolioReportService;

    @Test
    @DisplayName("shouldReturnPortfolioReport")
    @WithMockUser
    void shouldReturnPortfolioReport() throws Exception {
        PortfolioReportResponse response = new PortfolioReportResponse(
                Collections.emptyMap(),
                Collections.emptyMap(),
                0L,
                0L
        );

        when(portfolioReportService.generateReport()).thenReturn(response);

        mockMvc.perform(get("/reports/portfolio")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectsByStatus").exists())
                .andExpect(jsonPath("$.budgetByStatus").exists())
                .andExpect(jsonPath("$.averageClosedProjectDurationDays").exists())
                .andExpect(jsonPath("$.uniqueMembersAllocated").exists());
    }
}
