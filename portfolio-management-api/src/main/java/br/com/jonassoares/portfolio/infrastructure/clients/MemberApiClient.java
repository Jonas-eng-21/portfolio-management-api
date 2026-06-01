package br.com.jonassoares.portfolio.infrastructure.clients;

import br.com.jonassoares.portfolio.api.dtos.member.CreateMemberRequest;
import br.com.jonassoares.portfolio.api.dtos.member.MemberResponse;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MemberApiClient {

    private final RestClient restClient;

    public MemberApiClient(RestClient.Builder restClientBuilder, @Value("${api.member.url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public MemberResponse getMemberById(Long id) {
        return restClient.get()
                .uri("/members/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 404) {
                        throw new ResourceNotFoundException("Member not found with id: " + id);
                    }
                })
                .body(MemberResponse.class);
    }

    public MemberResponse createMember(CreateMemberRequest request) {
        return restClient.post()
                .uri("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(MemberResponse.class);
    }
}
