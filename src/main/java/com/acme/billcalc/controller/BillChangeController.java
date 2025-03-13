package com.acme.billcalc.controller;

import com.acme.billcalc.dto.InRequestChange;
import com.acme.billcalc.dto.OutRequestChange;
import com.acme.billcalc.service.BillToCoinCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bill-to-coin/v1")
@Slf4j
@RequiredArgsConstructor
public class BillChangeController {

	private final BillToCoinCalculatorService billToCoinCalculatorService;

	@PostMapping(path = "/request-change", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> requestChange(@RequestBody InRequestChange inRequestChange) {
		try {
			log.info("Request received: {}", inRequestChange.toString());
			OutRequestChange outRequestChange = billToCoinCalculatorService.billToCoinCalculator(inRequestChange.getBillAmount(), inRequestChange.isMostAmountRequested());
			return ResponseEntity.status(HttpStatus.OK).body(outRequestChange.toString());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(e.getMessage());
		}
	}
}
