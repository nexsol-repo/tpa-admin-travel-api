package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.city.CityInfo;

public record CityResponse(String cityNatlCd, String korNatlNm, String engNatlNm, String korCityNm, String engCityNm,
		String trvRskGrdeCd) {

	public static CityResponse of(CityInfo info) {
		return new CityResponse(info.cityNatlCd(), info.korNatlNm(), info.engNatlNm(), info.korCityNm(),
				info.engCityNm(), info.trvRskGrdeCd());
	}

}