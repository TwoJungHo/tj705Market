package kr.co.tj.userservice.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.co.tj.userservice.info.dto.UserInfoDTO;
import kr.co.tj.userservice.info.dto.UserInfoRequest;
import kr.co.tj.userservice.info.dto.UserInfoResponse;
import kr.co.tj.userservice.info.dto.UserLoginRequest;
import kr.co.tj.userservice.info.service.UserInfoService;
import kr.co.tj.userservice.pic.dto.UserPicDTO;
import kr.co.tj.userservice.pic.service.UserPicService;
import kr.co.tj.userservice.utils.ImageResize;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user-service")
public class UserController {

	
	private Environment env;
	private UserInfoService userInfoService;
	private UserPicService userPicService;
	
	@Autowired
	public UserController(Environment env, UserInfoService userInfoService, UserPicService userPicService) {
		super();
		this.env = env;
		this.userInfoService = userInfoService;
		this.userPicService = userPicService;
	}
	
	@GetMapping("/pic/{username}")
	public ResponseEntity<?> getUserPic(@PathVariable("username") String username){
		UserPicDTO userPicDTO = userPicService.findByUsername(username);
		if(userPicDTO == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		String filename = userPicDTO.getFilename();
		byte[] picData = userPicDTO.getPicData();
		
		String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
		MediaType mediaType = MediaType.parseMediaType("image/" + fileExtension);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		
		return ResponseEntity.ok().headers(headers).body(picData);
	}



	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest){
		Map<String, Object> map = new HashMap<>();
		
		if(userLoginRequest.getUsername() == null || userLoginRequest.getUsername().isEmpty()) {
			map.put("result", "id를 똑바로 입력하세요");
			return ResponseEntity.ok().body(map);
		}
		
		if(userLoginRequest.getPassword() == null || userLoginRequest.getPassword().isEmpty()) {
			map.put("result", "password를 똑바로 입력하세요");
			return ResponseEntity.ok().body(map);
		}
		
		UserInfoDTO userInfoDTO = UserInfoDTO.toUserDTO(userLoginRequest);
		
		userInfoDTO = userInfoService.login(userInfoDTO);
		
		if(userInfoDTO == null) {
			map.put("result", "사용자명이나 비밀번호가 잘못되었습니다.");
			return ResponseEntity.ok().body(map);
		}
		
		UserInfoResponse userInfoResponse = UserInfoResponse.builder()
				.username(userInfoDTO.getUsername())
				.name(userInfoDTO.getName())
				.longitude(userInfoDTO.getLongitude())
				.latitude(userInfoDTO.getLatitude())
				.token(userInfoDTO.getToken())
				.build();
		
		map.put("result", userInfoResponse);
		return ResponseEntity.ok().body(map);
	}
	
	

	// 회원가입
		@PostMapping("/users")
		public ResponseEntity<?> insertUser(MultipartHttpServletRequest request) {
			
			
			
			if(request.getFile("picFile") == null) {
				
				
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 프로필 사진을 등록하세요");
			}
			
			MultipartFile picFile = request.getFile("picFile");
			String picFilename = picFile.getOriginalFilename();
			byte[] picData = ImageResize.getResizedImageData(picFile, 150, 150, 0.5);
					
			
			String username = request.getParameter("username");
			String name = request.getParameter("name");
			String password = request.getParameter("password");
			Double longitude = Double.parseDouble(request.getParameter("longitude"));
			Double latitude = Double.parseDouble(request.getParameter("latitude"));
			
			UserInfoDTO userInfoDTO = UserInfoDTO.builder()
					.username(username)
					.password(password)
					.name(name)
					.longitude(longitude)
					.latitude(latitude)
					.build();

			userInfoDTO = userInfoService.insertUser(userInfoDTO);
			
			UserInfoResponse userInfoResponse = UserInfoResponse.builder()
					.username(userInfoDTO.getUsername())
					.name(userInfoDTO.getName())
					.longitude(userInfoDTO.getLongitude())
					.latitude(userInfoDTO.getLatitude())
					.createAt(userInfoDTO.getCreateAt())
					.updateAt(userInfoDTO.getUpdateAt())		
					.build();
			
			UserPicDTO userPicDTO = UserPicDTO.builder()
					.username(userInfoResponse.getUsername())
					.filename(picFilename)
					.picData(picData)
					.build();
			
			username = userPicService.insertPic(userPicDTO);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(userInfoResponse);
		}



	// 사용자 목록
	@GetMapping("/users")
	public ResponseEntity<?> getUsers() {
		List<UserInfoDTO> userInfoDTOs = userInfoService.getUsers();
		//List<UserResponse> userResponses = userDTOs.stream().map(UserDTO::toUserResponse).collect(Collectors.toList());
		List<UserInfoResponse> userInfoResponses = new ArrayList<>();
		
		for(UserInfoDTO x : userInfoDTOs) {
			userInfoResponses.add(x.toUserResponse());
		}

		return ResponseEntity.status(HttpStatus.OK).body(userInfoResponses);
	}

	// 정보 상세보기
	@GetMapping("/users/{username}")
	public ResponseEntity<?> getUser(@PathVariable("username") String username) {
		UserInfoDTO userInfoDTO = userInfoService.getUser(username);
		UserInfoResponse userInfoResponse = userInfoDTO.toUserResponse();

		return ResponseEntity.status(HttpStatus.OK).body(userInfoResponse);
	}

	// 수정
	@PutMapping("/users")
	public ResponseEntity<?> updateUser(@RequestBody UserInfoRequest userInfoRequest) {
		String orgPassword = userInfoRequest.getOrgPassword();
		String dbPassword = userInfoService.getUser(userInfoRequest.getUsername()).getPassword();
	
			if (orgPassword == null || !orgPassword.equals(dbPassword)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호를 확인하세요");
			}

			if (!userInfoRequest.getPassword().equals(userInfoRequest.getPassword2())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호를 확인하세요");
			}
			
			UserInfoDTO userInfoDTO = UserInfoDTO.toUserDTO(userInfoRequest);

			userInfoDTO = userInfoService.updateUser(userInfoDTO);
			UserInfoResponse userInfoResponse = userInfoDTO.toUserResponse();
			
			
			return ResponseEntity.status(HttpStatus.OK).body(userInfoResponse);
			
		
		
		
	}

	// 삭제
	@DeleteMapping("/users")
	public ResponseEntity<?> deleteUser(@RequestBody UserInfoRequest userInfoRequest) {
		
		UserInfoDTO orgDTO = userInfoService.getUser(userInfoRequest.getUsername());
		if(!orgDTO.getPassword().equals(userInfoRequest.getPassword())){
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비번 확인하세요");
		}
		
		userInfoService.deleteUser(orgDTO.getUsername());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// 테스트용
	@GetMapping("/health_check")
	public String status() {
		log.info("data.world: {}", env.getProperty("data.world"));
		log.info("data.test: {}", env.getProperty("data.test"));
		
		
		return "user service입니다" + env.getProperty("local.server.port");
	}
	
	
	@GetMapping("/test")
	public ResponseEntity<?> test(){
		System.out.println(":::::::::::::::잘 될까?::::::로그인 하고 토큰 첨부해야 되는데:::::::::::::");
		
		return ResponseEntity.status(HttpStatus.OK).body(new UserInfoResponse());
	}
	
	@PostMapping("/testinsert")
	public void testinsert() {
		System.out.println("테스트용 데이터를 여러개 주입합니다.");
		System.out.println("테스트용 데이터를 여러개 주입합니다.");
		System.out.println("테스트용 데이터를 여러개 주입합니다.");
		System.out.println("테스트용 데이터를 여러개 주입합니다.");
		System.out.println("테스트용 데이터를 여러개 주입합니다.");
		
		Random rand = new Random();
		for (int i = 1; i < 101; i++) {
			
			String idnum = String.format("%03d", i);
			int year = rand.nextInt(3) + 2021;
			int month = rand.nextInt(12) + 1;
			int day = rand.nextInt(28) + 1;
			Calendar cal = Calendar.getInstance();
			cal.set(year, month-1, day);
			Date date = cal.getTime();
			
			UserInfoDTO dto = UserInfoDTO.builder()
					.username("m" + idnum)
					.name(idnum + "번째 장원영")
					.password("1")
					.createAt(date)
					.updateAt(date)
					.build();
			
			userInfoService.testinsert(dto);
			
		}
				
	}

}
