package com.tipikae.safetynetalerts.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tipikae.safetynetalerts.dao.IFirestationDAO;
import com.tipikae.safetynetalerts.dao.IMedicalRecordDAO;
import com.tipikae.safetynetalerts.dao.IPersonDAO;
import com.tipikae.safetynetalerts.dto.ChildAlert;
import com.tipikae.safetynetalerts.dto.ChildAlertDTO;
import com.tipikae.safetynetalerts.dto.CommunityEmail;
import com.tipikae.safetynetalerts.dto.CommunityEmailDTO;
import com.tipikae.safetynetalerts.dto.Fire;
import com.tipikae.safetynetalerts.dto.FireDTO;
import com.tipikae.safetynetalerts.dto.FirestationDTO;
import com.tipikae.safetynetalerts.dto.FirestationInfo;
import com.tipikae.safetynetalerts.dto.Flood;
import com.tipikae.safetynetalerts.dto.FloodAddress;
import com.tipikae.safetynetalerts.dto.FloodDTO;
import com.tipikae.safetynetalerts.dto.PersonInfo;
import com.tipikae.safetynetalerts.dto.PersonInfoDTO;
import com.tipikae.safetynetalerts.dto.PhoneAlert;
import com.tipikae.safetynetalerts.dto.PhoneAlertDTO;
import com.tipikae.safetynetalerts.exception.ServiceException;
import com.tipikae.safetynetalerts.exception.StorageException;
import com.tipikae.safetynetalerts.model.Firestation;
import com.tipikae.safetynetalerts.model.MedicalRecord;
import com.tipikae.safetynetalerts.model.Person;
import com.tipikae.safetynetalerts.util.Util;

@Service
public class InformationServiceImpl implements IInformationService {
	
	private static final Logger LOGGER = LogManager.getLogger("InformationService");

	@Autowired
	private IFirestationDAO firestationDao;
	
	@Autowired
	private IPersonDAO personDao;
	
	@Autowired
	private IMedicalRecordDAO medicalRecordDao;

	public void setFirestationDao(IFirestationDAO firestationDao) {
		this.firestationDao = firestationDao;
	}

	public void setPersonDao(IPersonDAO personDao) {
		this.personDao = personDao;
	}

	public void setMedicalRecordDao(IMedicalRecordDAO medicalRecordDao) {
		this.medicalRecordDao = medicalRecordDao;
	}

	@Override
	public FirestationDTO getResidentsByStation(int stationNumber) throws ServiceException, StorageException {
		List<Firestation> firestations = firestationDao.findByStation(stationNumber);
		if(!firestations.isEmpty()) {
			List<FirestationInfo> residents = new ArrayList<>();
			int nbAdults = 0;
			int nbChildren = 0;
			for(Firestation firestation: firestations) {
				List<Person> persons = personDao.findByAddress(firestation.getAddress());
				for(Person person: persons) {
					MedicalRecord medicalRecord = medicalRecordDao.findByFirstnameLastname(
							person.getFirstname(), person.getLastname());
					if(medicalRecord != null) {
						if(Util.isAdult(medicalRecord.getBirthdate())) {
							nbAdults++;
						} else {
							nbChildren++;
						}
					}
					FirestationInfo resident = new FirestationInfo(person.getFirstname(), person.getLastname(), 
							person.getAddress(), person.getPhone());
					residents.add(resident);
				}
			}
			
			return new FirestationDTO(stationNumber, nbAdults, nbChildren, residents);
		} else {
			LOGGER.error("getResidentsByStation: station: " + stationNumber + " not found.");
			throw new ServiceException("station: " + stationNumber + " not found.");
		}
	}

	@Override
	public ChildAlertDTO getChildrenByAddress(String address) throws ServiceException, StorageException {
		List<Person> persons = personDao.findByAddress(address);
		if(!persons.isEmpty()) {
			List<ChildAlert> children = new ArrayList<>();
			List<ChildAlert> adults = new ArrayList<>();
			for(Person person: persons) {
				MedicalRecord medicalRecord = medicalRecordDao.findByFirstnameLastname(
						person.getFirstname(), person.getLastname());
				if(Util.isAdult(medicalRecord.getBirthdate())) {
					adults.add(new ChildAlert(person.getFirstname(), person.getLastname(), 
							Util.calculateAge(medicalRecord.getBirthdate())));
				} else {
					children.add(new ChildAlert(person.getFirstname(), person.getLastname(), 
							Util.calculateAge(medicalRecord.getBirthdate())));
				}
			}
			
			if(!children.isEmpty()) {
				return new ChildAlertDTO(address, children, adults);
			} else {
				return new ChildAlertDTO(address, new ArrayList<ChildAlert>(), new ArrayList<ChildAlert>());
			}
		} else {
			LOGGER.error("getChildrenByAddress: address: " + address + " not found.");
			throw new ServiceException("address: " + address + " not found.");
		}
	}

	@Override
	public PhoneAlertDTO getPhoneNumbersByStation(int station) throws ServiceException, StorageException {
		List<Firestation> firestations = firestationDao.findByStation(station);
		if(!firestations.isEmpty()) {
			List<PhoneAlert> phones = new ArrayList<>();
			for(Firestation firestation: firestations) {
				List<Person> persons = personDao.findByAddress(firestation.getAddress());
				for(Person person: persons) {
					phones.add(new PhoneAlert(person.getPhone()));
				}
			}
			
			return new PhoneAlertDTO(phones);
		} else {
			LOGGER.error("getPhoneNumbersByStation: station: " + station + " not found.");
			throw new ServiceException("station: " + station + " not found.");
		}
	}

	@Override
	public FireDTO getMembersByAddress(String address) throws ServiceException, StorageException {
		List<Person> persons = personDao.findByAddress(address);
		if(!persons.isEmpty()) {
			List<Fire> members = new ArrayList<>();
			Firestation firestation = firestationDao.findByAddress(address);
			int station = firestation.getStation();
			
			for(Person person: persons) {
				MedicalRecord medicalRecord = medicalRecordDao.findByFirstnameLastname(
						person.getFirstname(), person.getLastname());
				Fire member;
				if(medicalRecord != null) {
					int age = Util.calculateAge(medicalRecord.getBirthdate());
					member = new Fire(person.getFirstname(), person.getLastname(), person.getPhone(), 
							age, medicalRecord.getMedications(), medicalRecord.getAllergies());
				} else {
					member = new Fire(person.getFirstname(), person.getLastname(), person.getPhone(), 
							0, null, null);
				}
				members.add(member);
			}
			
			return new FireDTO(address, station, members);
		} else {
			LOGGER.error("getMembersByAddress: address: " + address + " not found.");
			throw new ServiceException("address: " + address + " not found.");
		}
	}

	@Override
	public List<FloodDTO> getResidentsByStations(List<Integer> stations) throws ServiceException, StorageException {
		List<FloodDTO> dtos = new ArrayList<>();
		for(Integer station: stations) {
			List<FloodAddress> adresses = new ArrayList<>();
			List<Firestation> firestations = firestationDao.findByStation(station);
			if (!firestations.isEmpty()) {
				for (Firestation firestation : firestations) {
					List<Flood> residents = new ArrayList<>();
					List<Person> persons = personDao.findByAddress(firestation.getAddress());
					if(!persons.isEmpty()) {
						for(Person person: persons) {
							MedicalRecord medicalRecord = medicalRecordDao.findByFirstnameLastname(
									person.getFirstname(), person.getLastname());
							if(medicalRecord != null) {
								residents.add(new Flood(person.getFirstname(), person.getLastname(), 
										person.getPhone(), Util.calculateAge(medicalRecord.getBirthdate()), 
										medicalRecord.getMedications(), medicalRecord.getAllergies()));
							} else {
								LOGGER.error("getResidentsByStations: name: " + person.getFirstname() + " " + 
										person.getLastname() + " not found in MedicalRecord.");
							}
						}
					} else {
						LOGGER.error("getResidentsByStations: address: " + firestation.getAddress() + 
								" not found in Person.");
					}
					adresses.add(new FloodAddress(firestation.getAddress(), residents));
				} 
			} else {
				LOGGER.error("getResidentsByStations: station: " + station + " not found in Firestation.");
			}
			dtos.add(new FloodDTO(station, adresses));
		}
		
		if(!dtos.isEmpty()) {
			return dtos;
		} else {
			StringBuilder sb = new StringBuilder();
			for(Integer station: stations) {
				sb.append(station.toString() + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			LOGGER.error("getResidentsByStations: stations: " + sb + " not found.");
			throw new ServiceException("stations: " + sb + " not found.");
		}
	}

	@Override
	public PersonInfoDTO getPersonInfoByLastname(String firstname, String lastname)
			throws ServiceException, StorageException {
		List<Person> persons = personDao.findAll();
		List<PersonInfo> personsInfo = new ArrayList<>();
		
		for(Person person: persons) {
			if (person.getLastname().equals(lastname)) {
				MedicalRecord medicalRecord = medicalRecordDao.findByFirstnameLastname(firstname, lastname);
				if(medicalRecord != null) {
					personsInfo.add(new PersonInfo(firstname, lastname, person.getAddress(), 
							Util.calculateAge(medicalRecord.getBirthdate()), person.getEmail(), 
							medicalRecord.getMedications(), medicalRecord.getAllergies()));
				}
			}
		}
		
		if(!personsInfo.isEmpty()) {
			return new PersonInfoDTO(lastname, personsInfo);
		} else {
			LOGGER.error("getPersonInfoByLastname: lastname: " + lastname + " not found.");
			throw new ServiceException("lastname: " + lastname + " not found.");
		}
	}

	@Override
	public CommunityEmailDTO getEmailsByCity(String city) throws ServiceException, StorageException {
		List<Person> persons = personDao.findByCity(city);
		if(!persons.isEmpty()) {
			List<CommunityEmail> emails = new ArrayList<>();
			for(Person person: persons) {
				emails.add(new CommunityEmail(person.getEmail()));
			}
			
			return new CommunityEmailDTO(emails);
		} else {
			LOGGER.error("getEmailsByCity: city: " + city + " not found.");
			throw new ServiceException("city: " + city + " not found.");
		}
	}

}
