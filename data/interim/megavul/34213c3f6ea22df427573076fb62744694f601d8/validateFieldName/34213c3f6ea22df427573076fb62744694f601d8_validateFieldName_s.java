class validateFieldName {
private void validateFieldName(Errors errors, AppointmentType appointmentType) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.name");
		if (appointmentService.verifyDuplicatedAppointmentTypeName(appointmentType)) {
			errors.rejectValue("name", "appointmentscheduling.AppointmentType.nameDuplicated");
		}
		if (verifyIfNameHasMoreThan100Characters(appointmentType.getName())) {
			errors.rejectValue("name", "appointmentscheduling.AppointmentType.longName.errorMessage");
		}
	}
}
