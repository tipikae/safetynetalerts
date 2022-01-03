package com.tipikae.safetynetalerts.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tipikae.safetynetalerts.dto.PersonDTO;
import com.tipikae.safetynetalerts.exception.ConverterException;
import com.tipikae.safetynetalerts.exception.ServiceException;
import com.tipikae.safetynetalerts.exception.StorageException;
import com.tipikae.safetynetalerts.service.IPersonService;

/**
 * A Person controller CRUD.
 * @author tipikae
 * @version 1.0
 *
 */
@Validated
@RestController
public class PersonController {
	
	@Autowired
	private IPersonService service;

	/**
	 * Add a person.
	 * @param personDTO a PersonDTO object.
	 * @return ResponseEntity<Object>
	 */
	@PostMapping(value="/person", consumes={"application/json"})
	public ResponseEntity<Object> addPerson(@Valid @RequestBody PersonDTO personDTO)
			throws StorageException , ServiceException, ConverterException {
		PersonDTO added = service.addPerson(personDTO);
		return new ResponseEntity<>(added, HttpStatus.OK);
	}

	/**
	 * Update a person.
	 * @param firstName a String firstname.
	 * @param lastName a String lastname.
	 * @param personDTO a PersonDTO object.
	 * @return ResponseEntity<Object>
	 */
	// /person?firstName={firstname}&lastName={lastname}
	@PutMapping(value="/person", consumes={"application/json"})
	public ResponseEntity<Object> updatePerson(
			@RequestParam @NotBlank String firstName, 
			@RequestParam @NotBlank String lastName, 
			@Valid @RequestBody PersonDTO personDTO)
					throws StorageException , ServiceException, ConverterException {
		PersonDTO updated = service.updatePerson(firstName, lastName, personDTO);
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	/**
	 * Delete a person.
	 * @param firstName a String firstname.
	 * @param lastName a String lastname.
	 * @return ResponseEntity<Object>
	 */
	// /person?firstName={firstname}&lastName={lastname}
	@DeleteMapping("/person")
	public ResponseEntity<Object> deletePerson(
			@RequestParam @NotBlank String firstName, 
			@RequestParam @NotBlank String lastName)
					throws StorageException , ServiceException {
		service.deletePerson(firstName, lastName);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
