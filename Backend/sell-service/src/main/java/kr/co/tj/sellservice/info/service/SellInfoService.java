package kr.co.tj.sellservice.info.service;

import java.util.List;

import kr.co.tj.sellservice.SellState;
import kr.co.tj.sellservice.info.dto.SellInfoDTO;

public interface SellInfoService {

	
	void testinsert(int trialNum, double rangeInKm);

	SellInfoDTO insert(SellInfoDTO dto);

	SellInfoDTO update(SellInfoDTO dto);
	
	SellInfoDTO updateState(String id, SellState sellState);

	SellInfoDTO isReviewed(String id, boolean isReviewed);

	SellInfoDTO findBySellId(String sellId);

	List<SellInfoDTO> findAroundAll(Double longitude, Double latitude, Double rangeInKm);
	

}
