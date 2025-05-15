package com.devsuperior.demo.dto;

import com.devsuperior.demo.entities.Employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class EmployeeDTO {
	
	private Long id;

	@NotBlank(message = "Required field")
	private String name;

	@Email(message = "Invalid email")
	private String email;

	@NotNull(message = "Required field")
	private Long departmentId;
	
	public EmployeeDTO(Employee entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		departmentId = entity.getDepartment().getId();
	}
}
