package kr.co.tj.chatservice.room.service;



import java.util.Map;

import kr.co.tj.chatservice.room.dto.ChatRoomDTO;

public interface ChatRoomService {

	ChatRoomDTO insertRoom(ChatRoomDTO dto);

	Map<String, Object> enter(String title);

	String delete(String title);



	

}
