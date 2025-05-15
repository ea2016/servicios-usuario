package com.easj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easj.dto.SolicitudVacacionesRequest;
import com.easj.service.VacacionesServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vacaciones")
@RequiredArgsConstructor
public class VacacionesController {

    private final VacacionesServiceImpl vacacionesService;

    @PostMapping("/solicitudes")
    public ResponseEntity<Void> crearSolicitud(@RequestBody SolicitudVacacionesRequest request) {
        vacacionesService.registrarSolicitud(request);
       
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
}
