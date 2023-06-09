package kr.co.tj.chatservice.room.controller;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import kr.co.tj.chatservice.room.dto.ChatRoomDTO;
import kr.co.tj.chatservice.room.dto.ChatRoomRequest;
import kr.co.tj.chatservice.room.dto.ChatRoomResponse;
import kr.co.tj.chatservice.room.service.ChatRoomService;

@RestController
@RequestMapping("/chat-service")
public class ChatRoomController {

	private Environment env;
	private ChatRoomService chatRoomService;

	@Autowired
	public ChatRoomController(Environment env, ChatRoomService chatRoomService) {
		super();
		this.env = env;
		this.chatRoomService = chatRoomService;
	}

	// 채팅방 생성 시도.
	// RequestBody에 ChatRoomRequest를 받는 것 뿐만 아니라,
	// RequestHeader에서 Authorization을 key로 하는 value인 {Bearer Token}을 받아옴.
	// token을 디코딩해서 username을 추출할 것임.
	
	@PostMapping("/enter")
	public ResponseEntity<?> insert(@RequestHeader("Authorization") String bearerToken,
			@RequestBody ChatRoomRequest chatRoomRequest) {

		// 헤더의 토큰을 활용하여, 현재 대화를 시도하고 있는 사용자의 username을 username1으로 반환 받음
		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username1 = Jwts.parser()
				.setSigningKey(encodedSecKey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();

		String username2 = chatRoomRequest.getUsername2();

		String[] users = { username1, username2 };
		Arrays.sort(users);
		username1 = users[0];
		username2 = users[1];
		String title = username1 + "-" + username2;

		Map<String, Object> map = chatRoomService.enter(title);

		if (map == null) {
			Map<String, Object> responseMap = new HashMap<>();
			ChatRoomDTO dto = ChatRoomDTO.builder()
					.title(title)
					.username1(username1)
					.username2(username2)
					.build();

			dto = chatRoomService.insertRoom(dto);
			
			ChatRoomResponse response = ChatRoomResponse.builder()
					.title(dto.getTitle())
					.username1(dto.getUsername1())
					.username2(dto.getUsername2())
					.build();
			responseMap.put("isExist", false);
			responseMap.put("roomInfo", response);

			return ResponseEntity.ok().body(responseMap);	

		}
		
		ChatRoomDTO dto = (ChatRoomDTO)map.get("roomInfo");
		ChatRoomResponse response = ChatRoomResponse.builder()
				.title(dto.getTitle())
				.username1(dto.getUsername1())
				.username2(dto.getUsername2())
				.build();
		
		map.put("roomInfo", response);
		map.put("isExist", true);

		return ResponseEntity.ok().body(map);


		
	}

	
	
	
	
	
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(@RequestBody ChatRoomRequest chatRoomRequest){
		
		String title = chatRoomRequest.getTitle();
		String result;
		
		try {
			result = chatRoomService.delete(title);
		} catch (Exception e) {
			
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 채팅방이 존재하지 않습니다");
			
		}
		
		return ResponseEntity.ok().body(result);
	}

}
