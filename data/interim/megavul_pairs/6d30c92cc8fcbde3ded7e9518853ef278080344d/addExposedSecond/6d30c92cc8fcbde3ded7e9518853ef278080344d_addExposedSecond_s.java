class addExposedSecond {
@PostMapping(value = "/exposednextday")
	@Loggable
	@Transactional
	@Operation(description = "Allows the client to send the last exposed key of the infection to the backend server. The JWT must come from a previous call to /exposed")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The exposed key has been stored in the backend"),
			@ApiResponse(responseCode = "400", description = 
					"- Ivnalid base64 encoded Temporary Exposure Key" +
					"- TEK-date does not match delayedKeyDAte claim in Jwt" +
					"- TEK has negative rolling period"),
			@ApiResponse(responseCode = "403", description = "No delayedKeyDate claim in authentication") })
	public @ResponseBody Callable<ResponseEntity<String>> addExposedSecond(
			@Valid @RequestBody @Parameter(description = "The last exposed key of the user") GaenSecondDay gaenSecondDay,
			@RequestHeader(value = "User-Agent") @Parameter(description = "App Identifier (PackageName/BundleIdentifier) + App-Version + OS (Android/iOS) + OS-Version", example = "ch.ubique.android.starsdk;1.0;iOS;13.3") String userAgent,
			@AuthenticationPrincipal @Parameter(description = "JWT token that can be verified by the backend server, must have been created by /v1/gaen/exposed and contain the delayedKeyDate") Object principal) {
		var now = Instant.now().toEpochMilli();

		if (!validationUtils.isValidBase64Key(gaenSecondDay.getDelayedKey().getKeyData())) {
			return () -> new ResponseEntity<>("No valid base64 key", HttpStatus.BAD_REQUEST);
		}
		if (principal instanceof Jwt && !((Jwt) principal).containsClaim("delayedKeyDate")) {
			return () -> ResponseEntity.status(HttpStatus.FORBIDDEN).body("claim does not contain delayedKeyDate");
		}
		if (principal instanceof Jwt) {
			var jwt = (Jwt) principal;
			var claimKeyDate = Integer.parseInt(jwt.getClaimAsString("delayedKeyDate"));
			if (!gaenSecondDay.getDelayedKey().getRollingStartNumber().equals(claimKeyDate)) {
				return () -> ResponseEntity.badRequest().body("keyDate does not match claim keyDate");
			}
		}

		if (!this.validateRequest.isFakeRequest(principal, gaenSecondDay.getDelayedKey())) {
			if (gaenSecondDay.getDelayedKey().getRollingPeriod().equals(0)) {
				// currently only android seems to send 0 which can never be valid, since a non used key should not be submitted
				// default value according to EN is 144, so just set it to that. If we ever get 0 from iOS we should log it, since
				// this should not happen
				gaenSecondDay.getDelayedKey().setRollingPeriod(GaenKey.GaenKeyDefaultRollingPeriod);
				if(userAgent.toLowerCase().contains("ios")) {
					logger.error("Received a rolling period of 0 for an iOS User-Agent");
				}
			} else if(gaenSecondDay.getDelayedKey().getRollingPeriod() < 0) {
				return () -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rolling Period MUST NOT be negative.");
			}
			List<GaenKey> keys = new ArrayList<>();
			keys.add(gaenSecondDay.getDelayedKey());
			dataService.upsertExposees(keys);
		}

		return () -> {
			normalizeRequestTime(now);
			return ResponseEntity.ok().body("OK");
		};

	}
}
