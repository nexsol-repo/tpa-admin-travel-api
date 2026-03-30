package com.nexsol.tpa.core.api.controller.v1;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexsol.tpa.core.api.controller.v1.response.CityResponse;
import com.nexsol.tpa.core.domain.city.CitySearchService;
import com.nexsol.tpa.core.support.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class CityController {

	private final CitySearchService citySearchService;

	@GetMapping("/cities")
	public ApiResponse<List<CityResponse>> searchCities(@RequestParam String keyword,
			@RequestParam(defaultValue = "1") String type) {

		List<CityResponse> cities = citySearchService.search(keyword, type).stream().map(CityResponse::of).toList();

		return ApiResponse.success(cities);
	}

}