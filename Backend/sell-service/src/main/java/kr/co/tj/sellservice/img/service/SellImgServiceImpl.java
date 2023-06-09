package kr.co.tj.sellservice.img.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import kr.co.tj.sellservice.img.dto.SellImgDTO;
import kr.co.tj.sellservice.img.persistence.SellImgEntity;
import kr.co.tj.sellservice.img.persistence.SellImgRepository;


@Service
public class SellImgServiceImpl implements SellImgService{
	
	private SellImgRepository sellImgRepository;
	
	
	
	@Autowired
	public SellImgServiceImpl(SellImgRepository sellImgRepository) {
		super();
		this.sellImgRepository = sellImgRepository;
	}
	
	
	@Override
	public SellImgDTO findBySellId(String sellId) {
		
		Optional<SellImgEntity> optional = sellImgRepository.findBySid(sellId);
		
		if(!optional.isPresent()) {
			
			return null;
		}
		
		SellImgEntity sellImgEntity = optional.get();
		
		SellImgDTO sellImgDTO = SellImgDTO.builder()
				.id(sellImgEntity.getId())
				.sid(sellImgEntity.getSid())
				.filename(sellImgEntity.getFilename())
				.imgData(sellImgEntity.getImgData())
				.thumData(sellImgEntity.getThumData())
				.build();
		
		return sellImgDTO;
	}
	
	
	
	

	@Override
	public String insertImg(SellImgDTO imgDTO) {
		
		SellImgEntity entity = SellImgEntity.builder()
				.sid(imgDTO.getSid())
				.filename(imgDTO.getFilename())
				.imgData(imgDTO.getImgData())
				.thumData(imgDTO.getThumData())
				.build();
		
		entity = sellImgRepository.save(entity);
		
		String sid = entity.getSid();
		
		return sid;
		
	}
	
	@Override
	public String updateImg(SellImgDTO imgDTO) {
		
		Optional<SellImgEntity> optional = sellImgRepository.findBySid(imgDTO.getSid());
		
		SellImgEntity sellImgEntity;
		
		if(optional.isPresent()) {
			
			SellImgEntity orgSellImgEntity = optional.get();
			Long orgId = orgSellImgEntity.getId();
			
			sellImgEntity = SellImgEntity.builder()
					.id(orgId)
					.sid(imgDTO.getSid())
					.filename(imgDTO.getFilename())
					.imgData(imgDTO.getImgData())
					.thumData(imgDTO.getThumData())
					.build();
		} else {
			
			sellImgEntity = SellImgEntity.builder()
					.id(null)
					.sid(imgDTO.getSid())
					.filename(imgDTO.getFilename())
					.imgData(imgDTO.getImgData())
					.thumData(imgDTO.getThumData())
					.build();
		}
		
		sellImgEntity = sellImgRepository.save(sellImgEntity);
		
		return sellImgEntity.getSid();
			
	}

	
	
	
	
	
	
//	@Override
//	public Map<String, byte[]> getImgData(MultipartFile file) {
//		
//		Map<String, byte[]> images = new HashMap<>();
//				
//		InputStream fileStream = null;
//	    ByteArrayOutputStream outputStream = null;
//	   
//		try {
//			
//			fileStream = file.getInputStream();
//			outputStream = new ByteArrayOutputStream();
//			
//			Thumbnails.of(fileStream)
//			.size(500, 500)
//			.outputQuality(0.75)
//			.toOutputStream(outputStream);
//
//			byte[] imgData = outputStream.toByteArray();
//			images.put("imgData", imgData);
//						
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//		
//		try {
//			fileStream = file.getInputStream();
//			outputStream = new ByteArrayOutputStream();
//			
//			Thumbnails.of(fileStream)
//			.size(50, 50)
//			.outputQuality(0.5)
//			.toOutputStream(outputStream);
//
//			byte[] thumData = outputStream.toByteArray();
//			images.put("thumData", thumData);
//		}
//		
//		catch (IOException e) {
//
//			e.printStackTrace();
//			
//		}
//		
//		return images;
//	}

}
