package com.MedicNote.prescriptionService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class ApplicationTests {
	void contextLoads() {
	}
}