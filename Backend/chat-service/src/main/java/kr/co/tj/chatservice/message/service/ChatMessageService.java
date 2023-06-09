package kr.co.tj.chatservice.message.service;

import kr.co.tj.chatservice.message.dto.ChatMessageDTO;

public interface ChatMessageService {

	void saveAndSendMessage(ChatMessageDTO dto);

}
