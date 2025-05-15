package com.devsuperior.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.devsuperior.demo.dto.RoleDTO;
import com.devsuperior.demo.services.RoleService;

@Controller
@RequestMapping(value = "/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping()
    public ResponseEntity<Page<RoleDTO>> findAll(Pageable pageable) {
        Page <RoleDTO> list = roleService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }
    
}
