package com.devsuperior.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.demo.dto.EmployeeDTO;
import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.entities.Employee;
import com.devsuperior.demo.repositories.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository repository;
	
	@Transactional(readOnly = true)
	public Page<EmployeeDTO> findAll(Pageable pageable) {
		Page<Employee> page = repository.findAll(pageable);
		return page.map(x -> new EmployeeDTO(x));
	}

	@Transactional
	public EmployeeDTO insert(EmployeeDTO dto) {
		if (dto.getName() == null || dto.getName().isEmpty()) {
		throw new IllegalArgumentException("Name must not be null or empty");
		}

		if (dto.getEmail() == null || !dto.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
			throw new IllegalArgumentException("Invalid email");
		}

		if (dto.getDepartmentId() == null) {
			throw new IllegalArgumentException("Department must not be null");
		}
		Employee entity = new Employee();
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setDepartment(new Department(dto.getDepartmentId(), null));
		entity = repository.save(entity);

		return new EmployeeDTO(entity);
	}
}
