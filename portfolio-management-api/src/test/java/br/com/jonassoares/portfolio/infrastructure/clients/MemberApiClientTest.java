package br.com.jonassoares.portfolio.infrastructure.clients;

import br.com.jonassoares.portfolio.api.dtos.member.CreateMemberRequest;
import br.com.jonassoares.portfolio.api.dtos.member.MemberResponse;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(properties = "api.member.url=http://localhost:8081", value = MemberApiClient.class)
class MemberApiClientTest {

    @Autowired
    private MemberApiClient memberApiClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMemberById_ShouldReturnMember_WhenFound() throws Exception {
        MemberResponse expectedResponse = new MemberResponse(1L, "John Doe", "Developer");
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);

        server.expect(requestTo("http://localhost:8081/members/1"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        MemberResponse actualResponse = memberApiClient.getMemberById(1L);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getMemberById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        server.expect(requestTo("http://localhost:8081/members/1"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> memberApiClient.getMemberById(1L));
    }

    @Test
    void createMember_ShouldReturnCreatedMember() throws Exception {
        CreateMemberRequest request = new CreateMemberRequest("Jane Doe", "Manager");
        MemberResponse expectedResponse = new MemberResponse(2L, "Jane Doe", "Manager");
        String jsonRequest = objectMapper.writeValueAsString(request);
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);

        server.expect(requestTo("http://localhost:8081/members"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().json(jsonRequest))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        MemberResponse actualResponse = memberApiClient.createMember(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
