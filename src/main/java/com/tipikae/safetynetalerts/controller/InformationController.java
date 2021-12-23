package com.tipikae.safetynetalerts.controller;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tipikae.safetynetalerts.dto.ChildAlertDTO;
import com.tipikae.safetynetalerts.dto.CommunityEmailDTO;
import com.tipikae.safetynetalerts.dto.FireDTO;
import com.tipikae.safetynetalerts.dto.FirestationDTO;
import com.tipikae.safetynetalerts.dto.FloodDTO;
import com.tipikae.safetynetalerts.dto.PersonInfoDTO;
import com.tipikae.safetynetalerts.dto.PhoneAlertDTO;
import com.tipikae.safetynetalerts.exception.ControllerException;
import com.tipikae.safetynetalerts.exception.ServiceException;
import com.tipikae.safetynetalerts.exception.StorageException;
import com.tipikae.safetynetalerts.service.IInformationService;

@Validated
@RestController
public class InformationController {

	@Autowired
	private IInformationService service;
	
	// /firestation?stationNumber=<station_number>
	@GetMapping(value="/firestation", params="stationNumber")
    public ResponseEntity<Object> residentsByStation(@RequestParam @Positive int stationNumber) {
		try {
			FirestationDTO dto = service.getResidentsByStation(stationNumber);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ServiceException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date()), 
					HttpStatus.NOT_FOUND);
		} catch (StorageException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage(), new Date()), 
					HttpStatus.INSUFFICIENT_STORAGE);
		}
	}
	
	// /childAlert?address=<address>
	@GetMapping(value="/childAlert", params="address")
    public ResponseEntity<Object> childrenByAddress(@RequestParam @NotBlank String address) {
		try {
			ChildAlertDTO dto = service.getChildrenByAddress(address);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ServiceException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date()), 
					HttpStatus.NOT_FOUND);
		} catch (StorageException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage(), new Date()), 
					HttpStatus.INSUFFICIENT_STORAGE);
		}
	}
	
	// /phoneAlert?firestation=<firestation_number>
	@GetMapping(value="/phoneAlert", params="firestation")
    public ResponseEntity<Object> phoneNumbersByStation(@RequestParam @Positive int firestation) {
		try {
			PhoneAlertDTO dto = service.getPhoneNumbersByStation(firestation);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ServiceException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date()), 
					HttpStatus.NOT_FOUND);
		} catch (StorageException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage(), new Date()), 
					HttpStatus.INSUFFICIENT_STORAGE);
		}
	}
	
	// /fire?address=<address>
	@GetMapping(value="/fire", params="address")
    public ResponseEntity<Object> membersByAddress(@RequestParam @NotBlank String address) {
		try {
			FireDTO dto = service.getMembersByAddress(address);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ServiceException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date()), 
					HttpStatus.NOT_FOUND);
		} catch (StorageException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage(), new Date()), 
					HttpStatus.INSUFFICIENT_STORAGE);
		}
	}
	
	// /flood/stations?stations=<a list of station_numbers>
	@GetMapping(value="/flood/stations", params="stations")
	public ResponseEntity<Object> residentsByStations(
			@RequestParam @NotEmpty(message = "Station number list cannot be empty.") List<Integer> stations) {
		try {
			List<FloodDTO> dto = service.getResidentsByStations(stations);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ServiceException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date()), 
					HttpStatus.NOT_FOUND);
		} catch (StorageException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage(), new Date()), 
					HttpStatus.INSUFFICIENT_STORAGE);
		}
	}
	
	// /personInfo?firstname=<firstname>&lastname=<lastname>
	@GetMapping(value="/personInfo", params={"firstname", "lastname"})
	public ResponseEntity<Object> personInfoByLastname(
			@RequestParam @NotBlank String firstname, 
    		@RequestParam @NotBlank String lastname) {
		try {
			PersonInfoDTO dto = service.getPersonInfoByLastname(firstname, lastname);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ServiceException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date()), 
					HttpStatus.NOT_FOUND);
		} catch (StorageException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage(), new Date()), 
					HttpStatus.INSUFFICIENT_STORAGE);
		}
	}
	
	// /communityEmail?city=<city>
	@GetMapping(value="/communityEmail", params="city")
	public ResponseEntity<Object> emailsByCity(@RequestParam @NotBlank String city) {
		try {
			CommunityEmailDTO dto = service.getEmailsByCity(city);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ServiceException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date()), 
					HttpStatus.NOT_FOUND);
		} catch (StorageException e) {
			return new ResponseEntity<>(
					new ControllerException(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage(), new Date()), 
					HttpStatus.INSUFFICIENT_STORAGE);
		}
	}
	
}
