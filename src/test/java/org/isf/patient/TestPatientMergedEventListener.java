package org.isf.patient;

import org.isf.patient.model.PatientMergedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestPatientMergedEventListener {
	private PatientMergedEvent patientMergedEvent;

	private boolean shouldFail = false;

	@EventListener
	public void handle(PatientMergedEvent patientMergedEvent) {
		this.patientMergedEvent = patientMergedEvent;
		if(shouldFail) {
			throw new RuntimeException("failure testing");
		}
	}

	public PatientMergedEvent getPatientMergedEvent() {
		return patientMergedEvent;
	}

	public void setShouldFail(boolean shouldFail) {
		this.shouldFail = shouldFail;
	}

}
