package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.AssociateRequestDto;
import com.brunofelix.api.votes.controller.dto.AssociateResponseDto;
import com.brunofelix.api.votes.exception.AssociateNotFoundException;
import com.brunofelix.api.votes.exception.CpfAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.CpfInvalidException;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.repository.AssociateRepository;
import com.brunofelix.api.votes.service.client.CpfServiceClient;
import com.brunofelix.api.votes.service.client.dto.CpfResponseDto;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class AssociateService {


    @Autowired
    private AssociateRepository associateRepository;
    @Autowired
    private CpfServiceClient cpfServiceClient;

    public AssociateResponseDto create(AssociateRequestDto associateRequestDto) {
        if (associateRepository.findByCpf(associateRequestDto.getCpf()).isPresent()) {
            throw new CpfAlreadyRegisteredException();
        }

        String cpf = formatCpf(associateRequestDto.getCpf());
        if (!validateCpf(cpf)) {
            throw new CpfInvalidException();
        }

        Associate associate = new Associate(associateRequestDto.getCpf(), associateRequestDto.getName());
        associate = associateRepository.save(associate);

        return new AssociateResponseDto(associate);
    }

    public Page<AssociateResponseDto> getAll(Pageable pageable) {
        return associateRepository.findAll(pageable).map(AssociateResponseDto::new);
    }

    public AssociateResponseDto getById(Long id) {
        return new AssociateResponseDto(this.findById(id));
    }

    protected Associate findById(Long id) {
        return associateRepository.findById(id).orElseThrow(AssociateNotFoundException::new);
    }

    private Boolean validateCpf(String cpf) {

        if (cpf.length() < 11) return false;

        /**
         * Integration with external API
         * using OpenFeign to validate CPF
         */
        try {
            if (cpfServiceClient.getValidateCpf(cpf).getStatus().toString().equals(CpfResponseDto.Status.UNABLE_TO_VOTE.toString()))
                return false;
        } catch (FeignException.NotFound e) {
            return false;
        }

        return true;
    }

    private String formatCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

}
