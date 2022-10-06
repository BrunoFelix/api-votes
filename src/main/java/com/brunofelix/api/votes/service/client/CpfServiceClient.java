package com.brunofelix.api.votes.service.client;

import com.brunofelix.api.votes.service.client.dto.CpfResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cpf", url = "${cpf.client.url}")
public interface CpfServiceClient {

    @GetMapping(value = "/{cpf}", consumes = MediaType.APPLICATION_JSON_VALUE)
    CpfResponseDto getValidateCpf(@PathVariable String cpf);
}